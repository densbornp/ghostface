package com.burningstack.ghostface.services;

import com.burningstack.ghostface.GhostfaceApplication;
import com.burningstack.ghostface.storage.ImageStorage;
import com.burningstack.ghostface.storage.StorageHandler;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
public class StorageService {

    // private static final long DELETE_CYCLE = 300000; // 5 min.
    // private static final long MAX_TIME_INACTIVE = 600000; // 10 min.
    private static final long DELETE_CYCLE = 30000; // 30 sec.
    private static final long MAX_TIME_INACTIVE = 60000; // 1 min.

    /** Stores the uploaded image into an ImageStorage */
    public ResponseEntity<Object> storeImage(String cookie, MultipartFile file) {
        try {
            StorageHandler.getInstance().setImageStorage(cookie, new ImageStorage(ImageIO.read(file.getInputStream()), file.getOriginalFilename()));
            StorageHandler.getInstance().printActiveClients();
            return ResponseEntity.status(HttpStatus.OK).body("Image successfully uploaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not write file!");
    }

    /** Returns the converted image */
    public ResponseEntity<Object> download(String cookie, HttpServletResponse response) {
        try {
            ImageStorage imgStore = StorageHandler.getInstance().getImageStorage(cookie);
            if (imgStore == null && imgStore.getFileName() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find file!");
            }
            imgStore.setLastTimeModified(new Date());
            MediaType contentType = imgStore.getContentType();
            BufferedImage img = imgStore.getConvertedImage();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(img, imgStore.getFileExtension(), bout);
            byte[] media = bout.toByteArray();
            StorageHandler.getInstance().printActiveClients();
            return ResponseEntity.ok().contentType(contentType).body(media);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected download error occurred!");
    }

    /** Returns the original uploaded image */
    public ResponseEntity<Object> getImage(String cookie) {
        try {
            ImageStorage imgStore = StorageHandler.getInstance().getImageStorage(cookie);
            if (imgStore == null && imgStore.getFileName() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find file!");
            }
            BufferedImage img = imgStore.getImage();
            MediaType contentType = imgStore.getContentType();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(img, imgStore.getFileExtension(), bout);
            byte[] media = bout.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return ResponseEntity.ok().contentType(contentType).body(media);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected download error occurred!");
    }

    /** Returns the converted image with the face detection markers */
    public ResponseEntity<Object> getTmpConvertedImage(String cookie) {
        try {
            ImageStorage imgStore = StorageHandler.getInstance().getImageStorage(cookie);
            if (imgStore == null && imgStore.getFileName() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find file!");
            }
            BufferedImage img = imgStore.getTemporaryImage();
            MediaType contentType = imgStore.getContentType();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(img, imgStore.getFileExtension(), bout);
            byte[] media = bout.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return ResponseEntity.ok().contentType(contentType).body(media);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find file!");
    }

    /**
     * Removes all inactive clients after a given period
     */
    @Scheduled(fixedDelay = DELETE_CYCLE)
    public void removeInactiveClients() {
        ConcurrentHashMap<String, ImageStorage> imageStorages = StorageHandler.getInstance().getAllImagesStorages();
        if(imageStorages.size() > 0) {
            Date now = new Date();
            imageStorages.forEach((cookie, imageStorage) -> {
                if((imageStorage.getLastTimeModified().getTime() + MAX_TIME_INACTIVE) < now.getTime()) {
                    StorageHandler.getInstance().removeImageStorage(cookie);
                    GhostfaceApplication.LOGGER.info("Removed inactive client: {}", cookie.substring(0, 10));
                }
            });
        }
        StorageHandler.getInstance().printActiveClients();
    }
}
