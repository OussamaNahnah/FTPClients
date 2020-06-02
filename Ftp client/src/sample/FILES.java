package sample;

import javafx.scene.image.ImageView;

import java.awt.*;

public class FILES {
    private String path;
    private String name;
    private long size;
    private boolean readable;
    private boolean writeble;
    private boolean excutibale;
    private boolean isDirectory;

    private ImageView type;


   FILES(ImageView type,String path, String name, long size ,boolean readable,boolean writeble,boolean excutibale ,boolean isDirectory) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.type = type;
        this.readable = readable;
        this.writeble = writeble;
        this.excutibale = excutibale;
        this.isDirectory = isDirectory;

    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }


    public boolean isExcutibale() {
        return excutibale;
    }

    public boolean isReadable() {
        return readable;
    }

    public boolean isWriteble() {
        return writeble;
    }

    public ImageView getType() {
        return type;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}
