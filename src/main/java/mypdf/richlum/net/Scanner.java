package mypdf.richlum.net;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.*;
import com.itextpdf.text.pdf.parser.Vector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Scanner {
    int cropht = 351;
    int cropwt = 351;
    ResultPoint[] resultPoints;
    String resultString;
    Map<ResultMetadataType, Object> meta;
    final List<ImageData> imagesData = new ArrayList<ImageData>();
    boolean saveImageFiles = false;

    public List<ImageData> getImagesData() {
        return imagesData;
    }

    public Scanner(){
    }

    private class ImageRenderListener implements RenderListener {
        int currPage;
        String currImageName;
        float currImageX;
        float currImageY;
        int currImageWidth;
        int currImageHeight;
        List<ImageData>  imagesData;
        int currImageNumber = 0;
        String currBarcodeText;
        Matrix imageCTM;
        StringBuilder sb;

        public ImageRenderListener(List<ImageData> imagesData){
            this.imagesData = imagesData;
        }

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
                this.resetCurrImageData();
                PdfImageObject image = renderInfo.getImage();
                if (image == null) return;
                Vector startpt = renderInfo.getStartPoint();
                String imageName = String.format("page%d.image%s.%s",
                        currPage,
                        renderInfo.getRef().getNumber(),
                        image.getFileType());
                this.setCurrImageName(imageName);
                this.setCurrImageX(renderInfo.getStartPoint().get(0));
                this.setCurrImageY(renderInfo.getStartPoint().get(1));
                this.setCurrImageWidth(renderInfo.getImage().getBufferedImage().getWidth());
                this.setCurrImageHeight(renderInfo.getImage().getBufferedImage().getHeight());
                this.setCurrImageCTM(renderInfo.getImageCTM());
                String barcodeString = scanForBarcode(renderInfo.getImage().getBufferedImage(),imageName);
                this.setCurrBarcodeText(barcodeString);

                this.setImageData();

                if (isSaveImageFiles()){
                    saveImageFiles(renderInfo.getImage(),
                            Paths.get(System.getProperty("java.io.tmpdir"),imageName));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void setCurrImageCTM(Matrix imageCTM) {
            this.imageCTM = imageCTM;
        }
        private Matrix getCurrImageCTM() {
            return this.imageCTM;
        }

        private void saveImageFiles(PdfImageObject image, Path path) throws IOException {
            FileOutputStream os = new FileOutputStream(path.toString());
            os.write(image.getImageAsBytes());
            os.flush();
            os.close();
            System.out.format("Saved %s\n", path.toString());
        }


        public int getCurrPage() {
            return currPage;
        }

        public void setCurrPage(int currPage) {
            this.currPage = currPage;
        }

        public String getCurrImageName() {
            return currImageName;
        }

        public void setCurrImageName(String currImageName) {
            this.currImageName = currImageName;
        }

        public float getCurrImageX() {
            return currImageX;
        }

        public void setCurrImageX(float currImageX) {
            this.currImageX = currImageX;
        }

        public float getCurrImageY() {
            return currImageY;
        }

        public void setCurrImageY(float currImageY) {
            this.currImageY = currImageY;
        }

        public int getCurrImageWidth() {
            return currImageWidth;
        }

        public void setCurrImageWidth(int currImageWidth) {
            this.currImageWidth = currImageWidth;
        }

        public int getCurrImageHeight() {
            return currImageHeight;
        }

        public void setCurrImageHeight(int currImageHeight) {
            this.currImageHeight = currImageHeight;
        }

        public void resetCurrImageData(){
            setCurrImageHeight(0);
            setCurrImageWidth(0);
            setCurrImageName("");
            setCurrImageX(0);
            setCurrImageY(0);
            setCurrBarcodeText("");
        }

        public String getCurrBarcodeText() {
            return currBarcodeText;
        }

        public void setCurrBarcodeText(String currBarcodeText) {
            this.currBarcodeText = currBarcodeText;
        }

        public void setImageData(){
            this.imagesData.add(
                    new ImageData.ImageDataBuilder()
                        .withPagenumber(this.currPage)
                        .withImagename(this.currImageName)
                        .withXpos(this.currImageX)
                        .withYpos(this.currImageY)
                        .withWidth(this.currImageWidth)
                        .withHeight(this.currImageHeight)
                        .withImageCTM(this.imageCTM)
                        .withBarcodetext(this.currBarcodeText)
                        .build());
        }

        public List<ImageData> getImagesData() {
            return imagesData;
        }
    }

    public void  processPdf(String infile, String outfile) throws IOException, DocumentException {
        PdfReader originalDoc = new PdfReader(infile);
        int numpages = originalDoc.getNumberOfPages();
        PdfReaderContentParser parser = new PdfReaderContentParser(originalDoc);
        PdfStamper stamper = new PdfStamper(originalDoc,new FileOutputStream(outfile));
        ImageRenderListener imageRenderListener = new ImageRenderListener(imagesData);
        for (int page=1;page<=numpages; page++) {
            imageRenderListener.setCurrPage(page);
            parser.processContent(page,imageRenderListener);
        }
        imageRenderListener.getImagesData()
                .stream()
                .forEach(System.out::println);
        stamper.flush();
        stamper.close();
    }


    public String scanForBarcode(BufferedImage bi, String imagename) throws IOException {

        LuminanceSource source;
        if(bi.getHeight()>=cropht&&bi.getWidth()>=cropwt) {
            // only look in bottom left corner of an images for a barcode if its a big one
            BufferedImage subimage = bi.getSubimage(0,bi.getHeight()-cropht,cropwt,cropht);
            source = new BufferedImageLuminanceSource(bi,0,bi.getHeight()-cropht,cropwt,cropht);

            if (isSaveImageFiles()) {
                Path targetfilepath = Paths.get(System.getProperty("java.io.tmpdir"), imagename + "_crop.png");
                FileOutputStream os = new FileOutputStream(targetfilepath.toString());
                System.out.println("targetfilepath" + targetfilepath.toString() );
                System.out.println(targetfilepath.getFileSystem() + "\t" + targetfilepath.getFileName() );
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(subimage, "png", baos);
                baos.flush();
                os.write(baos.toByteArray());
                os.flush();
                os.close();
            }
        }else{
            // smaller images look for qr code anywhere
            source = new BufferedImageLuminanceSource(bi);
        }
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader qrCodeReader = new QRCodeReader();
        Map<DecodeHintType,Object> hints = new HashMap<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        Result result = null;
        try {
            result = qrCodeReader.decode(bitmap,hints);
            if (result == null) return "";

            meta = result.getResultMetadata();
            meta.forEach( (t,o) -> {
                System.out.println( " meta: " +
                        t.name() + ":" + o.toString()
                );
            });
            System.out.println(result.getText());

            resultPoints = result.getResultPoints();
            Arrays.asList(resultPoints).stream()
                    .forEach( el ->{
                        System.out.print(" " + el );
                    });
            System.out.println();

            resultString = result.getText();
            return result.getText();
        } catch ( Exception e){
            System.out.println("EX: " +imagename + " " + e.getMessage());
        }
        return result == null ? "" : result.getText();
    }


    public ResultPoint[] getResultPoints() {
        return resultPoints;
    }

    public void setResultPoints(ResultPoint[] resultPoints) {
        this.resultPoints = resultPoints;
    }


    public int getCropht() {
        return cropht;
    }

    public void setCropht(int cropht) {
        this.cropht = cropht;
    }

    public int getCropwt() {
        return cropwt;
    }

    public void setCropwt(int cropwt) {
        this.cropwt = cropwt;
    }

    public String getresultString() {
        return resultString;
    }

    public Map<ResultMetadataType, Object> getMeta() {
        return meta;
    }

    public boolean isSaveImageFiles() {
        return saveImageFiles;
    }

    public void setSaveImageFiles(boolean saveImageFiles) {
        this.saveImageFiles = saveImageFiles;
    }


}
