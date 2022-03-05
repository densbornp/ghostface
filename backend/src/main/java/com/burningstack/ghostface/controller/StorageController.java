package com.burningstack.ghostface.controller;

import com.burningstack.ghostface.ParamHelper;
import com.burningstack.ghostface.services.StorageService;
import com.burningstack.ghostface.storage.StorageHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class StorageController {

    private StorageService storageService;
    private static final String VALID_COOKIE_MISSING = "Valid Cookie missing!";

    public StorageController() {
        this.storageService = new StorageService();
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(@CookieValue(value = "user_session", defaultValue = "") String cookie,
            @RequestParam(ParamHelper.UPLOADED_IMAGE) MultipartFile file) {

        try {
            if (cookie.isEmpty() || !StorageHandler.getInstance().isClientActive(cookie)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VALID_COOKIE_MISSING);
            }
            // Check if uploaded file is image
            if (ImageIO.read(file.getInputStream()) != null) {
                return this.storageService.storeImage(cookie, file);
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("The uploaded file is not an image!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected upload error occurred!");
    }

    @GetMapping("/download")
    public ResponseEntity<Object> downloadImage(@CookieValue(value = "user_session", defaultValue = "") String cookie,
            HttpServletResponse response) {
        if (cookie.isEmpty() || !StorageHandler.getInstance().isClientActive(cookie)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VALID_COOKIE_MISSING);
        }
        return this.storageService.download(cookie, response);
    }

    @GetMapping("/image")
    public ResponseEntity<Object> getImage(@CookieValue(value = "user_session", defaultValue = "") String cookie) {
        if (cookie.isEmpty() || !StorageHandler.getInstance().isClientActive(cookie)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VALID_COOKIE_MISSING);
        }
        return this.storageService.getImage(cookie);
    }

    @GetMapping("/tmpImage")
    public ResponseEntity<Object> getTmpConvertedImage(
            @CookieValue(value = "user_session", defaultValue = "") String cookie) {
        if (cookie.isEmpty() || !StorageHandler.getInstance().isClientActive(cookie)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VALID_COOKIE_MISSING);
        }
        return this.storageService.getTmpConvertedImage(cookie);
    }
}
