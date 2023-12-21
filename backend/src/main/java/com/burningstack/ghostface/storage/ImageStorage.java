package com.burningstack.ghostface.storage;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.util.Date;

@Data
public class ImageStorage {
    private String fileName;
    private String contentType;
    private String fileExtension;
    private BufferedImage[] imgBuffer = new BufferedImage[3];
    private Date lastTimeModified;

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
                    this.contentType = "image/jpeg";
                    this.fileExtension = "jpg";
                } else if (extensionString.contains("png")) {
                    this.contentType = "image/png";
                    this.fileExtension = "png";
                } else {
                    this.contentType = "image/gif";
                    this.fileExtension = "gif";
                }
            }
        }
        this.lastTimeModified = new Date();
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

    public void setLastTimeModified(Date modified) {
        this.lastTimeModified = modified;
    }
}
