package mypdf.richlum.net;

import com.itextpdf.text.Annotation;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.Image;

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
        }
        stamper.close();
    }
}
