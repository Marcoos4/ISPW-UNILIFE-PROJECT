package it.ispw.unilife.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class Document {
    private String fileName;
    private String fileType;
    private double fileSize;
    private byte[] content;

    public Document() {}

    public Document(String fileName, String fileType, double fileSize, byte[] content) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.content = content;
    }

    public Document(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        this.fileName = path.getFileName().toString();
        this.content = Files.readAllBytes(path);
        this.content = Files.readAllBytes(path);
        this.fileSize = (double) Files.size(path) / 1024;
        this.fileType = Files.probeContentType(path);
    }

    public void setFileSize(double fileSize) {this.fileSize = fileSize;}
    public double getFileSize() { return fileSize; }

    public String getFileType() { return fileType; }

    public void setFileType(String fileType) {this.fileType = fileType;}

    public byte[] getContent() { return content; }

    public void setContent(byte[] content) {this.content = content;}

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) {this.fileName = fileName; }
}