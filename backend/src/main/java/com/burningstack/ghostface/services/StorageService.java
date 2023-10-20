package com.burningstack.ghostface.services;

import com.burningstack.ghostface.storage.ImageStorage;
import com.burningstack.ghostface.storage.StorageHandler;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
@Slf4j
public class StorageService {

    private static final String DELETE_CYCLE = "5m"; // 5 min.
    private static final long MAX_TIME_INACTIVE = 600000; // 10 min.

    @Inject
    private StorageHandler storageHandler;

    @Inject
    private UploadFileService uploadFileService;

    /** Stores the uploaded image into an ImageStorage */
    public Response storeImage(String cookie, MultipartFormDataInput file) {
        try {
            // Check if uploaded file is image
            if (ImageIO.read(uploadFileService.getImageAsInputStream(file)) != null) {
                storageHandler.setImageStorage(cookie, uploadFileService.processUploadedImage(file));
                storageHandler.printActiveClients();
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /** Returns the converted image */
    public Response download(String cookie) {
        try {
            ImageStorage imgStore = storageHandler.getImageStorage(cookie);
            if (imgStore == null || imgStore.getFileName() == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Could not find file!").build();
            }
            imgStore.setLastTimeModified(new Date());
            String contentType = imgStore.getContentType();
            BufferedImage img = imgStore.getConvertedImage();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(img, imgStore.getFileExtension(), bout);
            byte[] media = bout.toByteArray();
            storageHandler.printActiveClients();
            return Response.ok().entity(media).type(contentType)
                    .header("Content-Disposition", "attachment; filename=" + imgStore.getFileName()).build();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected download error occurred!")
                .build();
    }

    /** Returns the original uploaded image */
    public Response getImage(String cookie) {
        try {
            ImageStorage imgStore = storageHandler.getImageStorage(cookie);
            if (imgStore == null || imgStore.getFileName() == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Could not find file!").build();
            }
            BufferedImage img = imgStore.getImage();
            String contentType = imgStore.getContentType();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(img, imgStore.getFileExtension(), bout);
            byte[] media = bout.toByteArray();
            return Response.ok().entity(media).type(contentType).build();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected download error occurred!")
                .build();
    }

    /** Returns the converted image with the face detection markers */
    public Response getTmpConvertedImage(String cookie) {
        try {
            ImageStorage imgStore = storageHandler.getImageStorage(cookie);
            if (imgStore == null || imgStore.getFileName() == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Could not find file!").build();
            }
            BufferedImage img = imgStore.getTemporaryImage();
            String contentType = imgStore.getContentType();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(img, imgStore.getFileExtension(), bout);
            byte[] media = bout.toByteArray();
            return Response.ok().entity(media).type(contentType).build();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Could not find file!").build();
    }

    /**
     * Removes all inactive clients after a given period
     */
    @Scheduled(every = DELETE_CYCLE)
    public void removeInactiveClients() {
        ConcurrentMap<String, ImageStorage> imageStorages = storageHandler.getAllImagesStorages();
        if (!imageStorages.isEmpty()) {
            Date now = new Date();
            imageStorages.forEach((cookie, imageStorage) -> {
                if ((imageStorage.getLastTimeModified().getTime() + MAX_TIME_INACTIVE) < now.getTime()) {
                    storageHandler.removeImageStorage(cookie);
                    log.info("Removed inactive client: {}", cookie.substring(0, 4));
                }
            });
        }
        storageHandler.printActiveClients();
    }
}
