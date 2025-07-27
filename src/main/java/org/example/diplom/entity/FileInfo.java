package org.example.diplom.entity;

public class FileInfo {
    private String filename;
    private Integer size;

    public FileInfo() { }

    public FileInfo(String filename, Integer size) {
        this.filename = filename;
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
