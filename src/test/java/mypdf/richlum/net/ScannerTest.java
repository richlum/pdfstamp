package mypdf.richlum.net;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.parser.Matrix;
import org.junit.Assert;
import org.junit.Test;
import mypdf.richlum.net.Scanner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ScannerTest {
    private Scanner scanner;


    @Test
    public void testScanForBarCode_nonePresent() throws IOException, DocumentException {
        String testfile = "src/test/resources/page.pdf";
        String outputfile = Paths.get(System.getProperty("java.io.tmpdir") ,"ScannerTestOuput.pdf").toString();

        Assert.assertTrue(true);
        scanner = new Scanner();
        scanner.setSaveImageFiles(true);
        scanner.processPdf(testfile,outputfile);
        List<ImageData> imagesData = scanner.getImagesData();
        Assert.assertEquals(4,imagesData.size());
        Assert.assertEquals("pg:1 ,name:page1.image6.png ,position 34.96774,613.0605 ,dim 728,90 ,txt:",imagesData.get(0).toString());
        Assert.assertEquals(  new Matrix (543.0645f,0.0f,0.0f,67.13709f,34.96774f,613.0605f),imagesData.get(0).imageCTM);
        Assert.assertEquals("pg:1 ,name:page1.image16.jpg ,position 281.88306,268.4234 ,dim 32,32 ,txt:",imagesData.get(1).toString());
        Assert.assertEquals("pg:1 ,name:page1.image18.jpg ,position 431.0766,268.4234 ,dim 32,32 ,txt:",imagesData.get(2).toString());
        Assert.assertEquals("pg:2 ,name:page2.image35.jpg ,position 431.0766,312.4355 ,dim 32,32 ,txt:",imagesData.get(3).toString());
    }

    @Test
    public void testScanForBarCode_twoPresent() throws IOException, DocumentException {
        String testfile = "src/test/resources/barcode1page.pdf";
        String outputfile = Paths.get(System.getProperty("java.io.tmpdir") ,"ScannerTestOuput2.pdf").toString();

        Assert.assertTrue(true);
        scanner = new Scanner();
        scanner.setSaveImageFiles(true);
        scanner.processPdf(testfile,outputfile);
        List<ImageData> imagesData = scanner.getImagesData();
        Assert.assertEquals(7,imagesData.size());
        Assert.assertEquals("pg:1 ,name:page1.image21.jpg ,position 431.0766,312.4355 ,dim 32,32 ,txt:",imagesData.get(0).toString());
        Assert.assertEquals("pg:1 ,name:page1.image24.png ,position 0.0,0.0 ,dim 225,225 ,txt:",imagesData.get(1).toString());
        Assert.assertEquals("pg:1 ,name:page1.image23.png ,position 100.0,100.0 ,dim 200,200 ,txt:",imagesData.get(2).toString());
        Assert.assertEquals("pg:1 ,name:page1.image22.png ,position 0.0,0.0 ,dim 125,125 ,txt:test entry ,page=2,image=src/main/resources/cpp.png",imagesData.get(3).toString());
        Assert.assertEquals("pg:1 ,name:page1.image1.png ,position 0.0,0.0 ,dim 225,225 ,txt:",imagesData.get(4).toString());
        Assert.assertEquals("pg:1 ,name:page1.image3.png ,position 100.0,100.0 ,dim 200,200 ,txt:",imagesData.get(5).toString());
        Assert.assertEquals("pg:1 ,name:page1.image4.png ,position 0.0,0.0 ,dim 125,125 ,txt:test entry ,page=1,image=src/main/resources/cpp.png",imagesData.get(6).toString());
    }
}
