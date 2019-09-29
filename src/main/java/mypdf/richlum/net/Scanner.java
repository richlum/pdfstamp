package mypdf.richlum.net;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

public class Scanner {
    int cropht = 351;
    int cropwt = 351;
    ResultPoint[] resultPoints;

    public Scanner(){

    }

    public String scanForBarcode(BufferedImage bi, String imagename) throws IOException {
        System.out.println("w:" + bi.getWidth() + ", h:"+ bi.getHeight());

        LuminanceSource source;
        if(bi.getHeight()>=cropht&&bi.getWidth()>=cropwt) {
            BufferedImage subimage = bi.getSubimage(0,bi.getHeight()-cropht,cropwt,cropht);
            //source = new BufferedImageLuminanceSource(subimage);
            source = new BufferedImageLuminanceSource(bi,0,bi.getHeight()-cropht,cropwt,cropht);
            FileOutputStream os = new FileOutputStream("subimage.png");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(subimage,"png",baos);
            baos.flush();
            os.write(baos.toByteArray());
            os.flush();
            os.close();

        }else{
            source = new BufferedImageLuminanceSource(bi);
        }
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader qrCodeReader = new QRCodeReader();
        Hashtable hints = new Hashtable();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        Result result = null;
        try {
            result = qrCodeReader.decode(bitmap,hints);

            Map<ResultMetadataType, Object> meta = result.getResultMetadata();
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

}
