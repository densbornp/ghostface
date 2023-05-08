package com.burningstack.ghostface.storage;

import org.springframework.http.MediaType;

import java.awt.image.BufferedImage;
import java.util.Date;

public class ImageStorage {
    private String fileName;
    private MediaType contentType;
    private String fileExtension;
    private BufferedImage[] imgBuffer = new BufferedImage[3];
    private Date lastModified;

    public ImageStorage(BufferedImage img, String fileName) {
        this.imgBuffer[0] = img; // Original image
        this.imgBuffer[1] = img; // Temporary converted image
        this.imgBuffer[2] = img; // Converted image
        this.fileName = fileName;
        if(fileName != null) {
            int index = fileName.lastIndexOf('.');
            if (index > 0) {
                String extensionString = fileName.substring(index + 1).toLowerCase();
                if (extensionString.contains("jpg") || extensionString.contains("jpeg")) {
                    this.contentType = MediaType.IMAGE_JPEG;
                    this.fileExtension = "jpg";
                } else if (extensionString.contains("png")) {
                    this.contentType = MediaType.IMAGE_PNG;
                    this.fileExtension = "png";
                } else {
                    this.contentType = MediaType.IMAGE_GIF;
                    this.fileExtension = "gif";
                }
            }
        }
        this.lastModified = new Date();
    }

    public BufferedImage getImage() {
        return imgBuffer[0];
    }

    public BufferedImage getTemporaryImage() {
        return imgBuffer[1];
    }

    public void setTemporaryImage(BufferedImage image) {
        this.imgBuffer[1] = image;
    }

    public BufferedImage getConvertedImage() {
        return imgBuffer[2];
    }

    public void setConvertedImage(BufferedImage image) {
        this.imgBuffer[2] = image;
    }

    public String getFileName() {
        return fileName;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public Date getLastTimeModified() {
        return lastModified;
    }

    public void setLastTimeModified(Date modified) {
        this.lastModified = modified;
    }
}
