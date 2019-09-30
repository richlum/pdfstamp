package mypdf.richlum.net;

import com.itextpdf.text.DocumentException;
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
        Assert.assertEquals("pg:1 ,name:page1.image6.png ,position 34.96774,613.0605 ,dim 728,90 ,txt:null",imagesData.get(0).toString());
        Assert.assertEquals("pg:1 ,name:page1.image16.jpg ,position 281.88306,268.4234 ,dim 32,32 ,txt:null",imagesData.get(1).toString());
        Assert.assertEquals("pg:1 ,name:page1.image18.jpg ,position 431.0766,268.4234 ,dim 32,32 ,txt:null",imagesData.get(2).toString());
        Assert.assertEquals("pg:2 ,name:page2.image35.jpg ,position 431.0766,312.4355 ,dim 32,32 ,txt:null",imagesData.get(3).toString());
    }
}
