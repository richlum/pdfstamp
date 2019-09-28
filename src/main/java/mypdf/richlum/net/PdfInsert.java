package mypdf.richlum.net;

import com.itextpdf.text.Annotation;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.parser.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


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

        }
        stamper.close();
        testing(outfn);
    }
    private byte[] getBlankRectImage(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.setPaint(Color.RED);
        graphics.fill(new Rectangle(image.getWidth(),image.getHeight()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image,"png",baos);
        baos.flush();
        return baos.toByteArray();
    }


    private void testing(String inputpdf) throws IOException {
        PdfReader reader = new PdfReader(inputpdf);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        for (int i=1;i<=reader.getNumberOfPages();i++){
            parser.processContent(i, new RenderListener() {
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
                        String fn = String.format("pageimage%s.%s",renderInfo.getRef().getNumber(),image.getFileType());
                        FileOutputStream os = new FileOutputStream(fn);
                        os.write(image.getImageAsBytes());
                        os.flush();
                        os.close();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
