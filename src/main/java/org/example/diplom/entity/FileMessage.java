package org.example.diplom.entity;

public class FileMessage {
    private String hash;
    private byte[] file;

    public FileMessage() {
    }

    public FileMessage(byte[] file, String hash) {
        this.file = file;
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
