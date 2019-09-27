package mypdf.richlum.net;


import com.itextpdf.text.DocumentException;

import java.io.IOException;

public class MyPdf {
    public static void main(String[] args){

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

