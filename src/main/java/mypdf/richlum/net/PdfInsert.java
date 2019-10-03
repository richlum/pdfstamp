package mypdf.richlum.net;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.itextpdf.text.Annotation;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.parser.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class PdfInsert {

    String inputfn;
    String imagefn;
    String outfn;

    public PdfInsert(String inputpdffile, String inputimage, String outputfile) throws IOException, DocumentException {
       inputfn = inputpdffile;
       imagefn = inputimage;
       outfn = outputfile;
       System.out.format(" %s %s %s \n", inputfn,imagefn,outfn);


        PdfReader readerOriginalDoc = new PdfReader(inputpdffile);
        int numpages = readerOriginalDoc.getNumberOfPages();
        System.out.println("pages : " + numpages);
        PdfStamper stamper = new PdfStamper(readerOriginalDoc,new FileOutputStream(outputfile));
        for (int i = 1 ;i<=numpages;i++) {
            System.out.println( "page " + i );
            PdfContentByte content = stamper.getOverContent(i);
            Image image = Image.getInstance(inputimage);
            image.scaleAbsolute(150, 150);
            image.setAbsolutePosition(0, 0);
            int target  = (i >= numpages) ? 1 : (i + 1) ;
            System.out.println("target " + target );
            image.setAnnotation(new Annotation(0, 0, 100, 100, target));
            content.addImage(image);

            Image square = Image.getInstance(getBlankRectImage(200,200));
//            image.getInstance(getBlankRectImage(200,200));
            square.setAbsolutePosition(100,100);
            content.addImage(square);

            Image qrcode = makeBarCode("test entry " + ",page=" +i+",image="+imagefn);
            qrcode.setAbsolutePosition(0,0);
            content.addImage(qrcode);

        }
        stamper.close();
        testing(outfn);
    }
    public static byte[] getBlankRectImage(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.setPaint(Color.BLUE);
        graphics.fill(new Rectangle(image.getWidth(),image.getHeight()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image,"png",baos);
        baos.flush();
        return baos.toByteArray();
    }

    private class ImageData{
        public int pagenumber;
        public String imagename;
        public int imagenumber;
        public String barcodetext;
        public float xpos;
        public float ypos;
        public int width;
        public int height;

        public ImageData(int pagenumber,String imagename,int imagenumber,float xpos, float ypos, int w, int h){
            this(pagenumber,imagename,imagenumber);
            this.xpos=xpos;
            this.ypos=ypos;
            this.width=w;
            this.height=h;
        }
        public ImageData(int pagenumber,String imagename,int imagenumber){
            this.pagenumber = pagenumber;
            this.imagename = imagename;
            this.imagenumber = imagenumber;
        }
        @Override
        public String toString(){
            return new String("pg:" + pagenumber + " ,name:" + imagename +
                    " ,position " + xpos + "," + ypos +
                    " ,dim " + width + "," + height +
                    " ,txt:" + barcodetext);
        }
    }

    private void testing(String inputpdf) throws IOException {
        PdfReader reader = new PdfReader(inputpdf);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        final List<ImageData> imagelist = new ArrayList<ImageData>();
        AtomicInteger imagenum = new AtomicInteger(0);
        for (AtomicInteger i=new AtomicInteger(1);i.get()<=reader.getNumberOfPages();i.getAndIncrement()){
            parser.processContent(i.get(), new RenderListener() {
                @Override
                public void beginTextBlock() {

                }

                @Override
                public void renderText(TextRenderInfo renderInfo) {

                }

                @Override
                public void endTextBlock() {

                }

                @Override
                public void renderImage(ImageRenderInfo renderInfo) {
                    try {
                        PdfImageObject image = renderInfo.getImage();
                        if (image==null) return;
                        Vector startpt = renderInfo.getStartPoint();
                        System.out.println( "startpt: " +startpt.toString());
                        String fn = String.format("pageimage%s.%s",renderInfo.getRef().getNumber(),image.getFileType());
                        FileOutputStream os = new FileOutputStream(fn);
                        os.write(image.getImageAsBytes());
                        os.flush();
                        os.close();
                        int page = i.get();
                        int imagecnt = imagenum.get();
                        System.out.println("info: " +page + " " + fn + " " + imagecnt);
                        //ImageData id = new ImageData(page,fn,imagecnt);
                        ImageData id = new ImageData(page,fn,imagecnt,
                                renderInfo.getStartPoint().get(0),
                                renderInfo.getStartPoint().get(1),
                                renderInfo.getImage().getBufferedImage().getWidth(),
                                renderInfo.getImage().getBufferedImage().getHeight());
                        System.out.println( "adding " + id);
                        imagelist.add(id);
                        String rslt = scanForBarcode(image.getBufferedImage(),fn);
                        id.barcodetext = rslt;
                        System.out.println("barcodetext= " + rslt);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
        imagelist.stream()
                .forEach( img -> {
                    System.out.println(img);
                });
    }

    private String scanForBarcode(BufferedImage bi,String imagename)  {
        // pick something bigger than the size of the barcode that we generate
        int cropht = 201;
        int cropwt = 201;
        System.out.println("w:" + bi.getWidth() + ", h:"+ bi.getHeight());

        LuminanceSource source;
        if(bi.getHeight()>=cropht&&bi.getWidth()>=cropwt) {
            source = new BufferedImageLuminanceSource(bi, 0, 0, cropwt, cropht);
        }else{
            source = new BufferedImageLuminanceSource(bi);
        }
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader qrCodeReader = new QRCodeReader();
        Result result = null;
        try {
            result = qrCodeReader.decode(bitmap);
            Map<ResultMetadataType, Object> meta = result.getResultMetadata();
            meta.forEach( (t,o) -> {
                System.out.println( " meta: " +
                    t.name() + ":" + o.toString()
                );
            });
            return result.getText();
        } catch ( Exception e){
            System.out.println("EX: " +imagename + " " + e.getMessage());
        }
        return result == null ? "" : result.getText();
    }

    private Image makeBarCode(String contents) throws BadElementException {
//        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        int w = 125;
        int h = 125;
        BarcodeQRCode qrcode = new BarcodeQRCode(contents, w, h, null);
        return qrcode.getImage();
    }
}
