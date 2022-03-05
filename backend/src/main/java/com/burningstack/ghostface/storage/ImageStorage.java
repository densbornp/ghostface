package com.burningstack.ghostface.storage;

import java.awt.image.BufferedImage;
import java.util.Date;

public class ImageStorage {
    private String fileName;
    private String extension;
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
                this.extension = fileName.substring(index + 1);
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

    public String getExtension() {
        return extension;
    }

    public Date getLastTimeModified() {
        return lastModified;
    }

    public void setLastTimeModified(Date modified) {
        this.lastModified = modified;
    }
}
