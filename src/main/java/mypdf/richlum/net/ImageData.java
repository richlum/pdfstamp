package mypdf.richlum.net;

import com.itextpdf.text.pdf.parser.Matrix;

public class ImageData {
    private Matrix imageCTM;
    private int pagenumber;
    private String imagename;
    private int imagenumber;
    private String barcodetext;
    private float xpos;
    private float ypos;
    private int width;
    private int height;

    public static class ImageDataBuilder {
        private Matrix imageCTM;
        private int pagenumber;
        private String imagename;
        private int imagenumber;
        private String barcodetext;
        private float xpos;
        private float ypos;
        private int width;
        private int height;

        public ImageData build(){
            ImageData imageData = new ImageData();
            imageData.imageCTM =          this.imageCTM;
            imageData.imageCTM =          this.imageCTM;
            imageData.pagenumber =        this.pagenumber;
            imageData.imagename =         this.imagename;
            imageData.imagenumber =       this.imagenumber;
            imageData.barcodetext =       this.barcodetext;
            imageData.xpos =              this.xpos;
            imageData.ypos =              this.ypos;
            imageData.width =             this.width;
            imageData.height =            this.height;
            return imageData;
        }

        public ImageDataBuilder withImageCTM(Matrix imageCTM) {
            this.imageCTM = imageCTM;
            return this;
        }


        public ImageDataBuilder withPagenumber(int pagenumber) {
            this.pagenumber = pagenumber;
            return this;
        }


        public ImageDataBuilder withImagename(String imagename) {
            this.imagename = imagename;
            return this;
        }


        public ImageDataBuilder withImagenumber(int imagenumber) {
            this.imagenumber = imagenumber;
            return this;
        }


        public ImageDataBuilder withBarcodetext(String barcodetext) {
            this.barcodetext = barcodetext;
            return this;
        }


        public ImageDataBuilder withXpos(float xpos) {
            this.xpos = xpos;
            return this;
        }


        public ImageDataBuilder withYpos(float ypos) {
            this.ypos = ypos;
            return this;
        }


        public ImageDataBuilder withWidth(int width) {
            this.width = width;
            return this;
        }


        public ImageDataBuilder withHeight(int height) {
            this.height = height;
            return this;
        }
    }

    public Matrix getImageCTM() {
        return imageCTM;
    }

    public int getPagenumber() {
        return pagenumber;
    }

    public String getImagename() {
        return imagename;
    }

    public int getImagenumber() {
        return imagenumber;
    }

    public String getBarcodetext() {
        return barcodetext;
    }

    public float getXpos() {
        return xpos;
    }

    public float getYpos() {
        return ypos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return new String("pg:" + pagenumber + " ,name:" + imagename +
                " ,position " + xpos + "," + ypos +
                " ,dim " + width + "," + height +
                " ,txt:" + barcodetext // + "\t, ctm: \n" + imageCTM
        );
    }

}