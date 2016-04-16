package com.starikovskiy_cr.scope_8.util;

/**
 * Created by starikovskiy_cr on 12.03.16.
 */
public class FileInfo {
    private String path;
    private int width;
    private int height;

    public FileInfo(String path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPath() {

        return path;
    }
}
