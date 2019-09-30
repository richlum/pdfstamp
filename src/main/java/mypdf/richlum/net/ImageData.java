package mypdf.richlum.net;

public class ImageData {
    public int pagenumber;
    public String imagename;
    public int imagenumber;
    public String barcodetext;
    public float xpos;
    public float ypos;
    public int width;
    public int height;

    public ImageData(int pagenumber, String imagename, int imagenumber, float xpos, float ypos, int w, int h) {
        this(pagenumber, imagename, imagenumber);
        this.xpos = xpos;
        this.ypos = ypos;
        this.width = w;
        this.height = h;
    }
    public ImageData(int pagenumber, String imagename,  float xpos, float ypos, int w, int h) {
        this(pagenumber, imagename );
        this.xpos = xpos;
        this.ypos = ypos;
        this.width = w;
        this.height = h;
    }

    public ImageData(int pagenumber, String imagename, int imagenumber) {
        this.pagenumber = pagenumber;
        this.imagename = imagename;
        this.imagenumber = imagenumber;
    }
    public ImageData(int pagenumber, String imagename) {
        this.pagenumber = pagenumber;
        this.imagename = imagename;
    }


    @Override
    public String toString() {
        return new String("pg:" + pagenumber + " ,name:" + imagename +
                " ,position " + xpos + "," + ypos +
                " ,dim " + width + "," + height +
                " ,txt:" + barcodetext);
    }
}


