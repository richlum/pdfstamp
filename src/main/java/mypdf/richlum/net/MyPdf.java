package mypdf.richlum.net;


import com.google.zxing.ResultPoint;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyPdf {
    public static void main(String[] args){

        //test1(args);
        test2();

    }

    private static void test2() {
        final String fn = "pg_0001.png.pdf";
        Scanner scanner = new Scanner();
//        PdfReader reader = null;
        try {
            final PdfReader reader = new PdfReader(fn);

            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            parser.processContent(1, new RenderListener() {
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
                    Scanner sc = new Scanner();
                    try {
                        String result = sc.scanForBarcode(renderInfo.getImage().getBufferedImage(),fn);
                        System.out.println("barcode text: " + result);
                        ResultPoint[] points = sc.getResultPoints();
                        cover(reader,points);

                    } catch (IOException | DocumentException e) {
                        e.printStackTrace();
                    }
                }
            });



        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private static void cover(PdfReader reader, ResultPoint[] points) throws IOException, DocumentException {
        List<ResultPoint> pointList = Arrays.asList(points);

        float min_y = pointList.stream().min(Comparator.comparing(ResultPoint::getY)).get().getY();
        float max_y = pointList.stream().max(Comparator.comparing(ResultPoint::getY)).get().getY();
        float min_x = pointList.stream().min(Comparator.comparing(ResultPoint::getX)).get().getX();
        float max_x = pointList.stream().max(Comparator.comparing(ResultPoint::getX)).get().getX();

        float ht = max_y - min_y;
        float wt = max_x - min_x;

        byte[] coverimage = PdfInsert.getBlankRectImage((int)Math.ceil(wt), (int)Math.ceil(ht));
        Image square = Image.getInstance(coverimage);
        square.setAbsolutePosition(0,17);

        System.out.println("ht,wt = " + ht + " , " + wt);
        System.out.println("min x,y = " + min_x + "," + min_y);

        String outputfile = "coveredPdf.pdf";
        PdfStamper stamper = new PdfStamper(reader,new FileOutputStream(outputfile));
        PdfContentByte content = stamper.getOverContent(1);
        content.addImage(square);
        stamper.close();
    }

    private static void test1(String[] args) {
        String inputpdffile = "src/main/resources/page.pdf";
        String inputimage = "src/main/resource/cpp.pdf";
        String outputfile = "newfile.pdf";
        if (args.length==3){
            inputpdffile = args[0];
            inputimage = args[1];
            outputfile = args[2];
        }


        try {
            PdfInsert pdfInsert = new PdfInsert(inputpdffile, inputimage,outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


}

