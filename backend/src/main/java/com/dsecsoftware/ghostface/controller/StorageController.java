package com.dsecsoftware.ghostface.controller;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dsecsoftware.ghostface.ParamHelper;
import com.dsecsoftware.ghostface.PathManager;
import com.dsecsoftware.ghostface.services.CookieService;
import com.dsecsoftware.ghostface.services.StorageService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class StorageController {

    private StorageService storageService;

    public StorageController() {
        this.storageService = new StorageService();
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(HttpServletRequest request,
            @RequestParam(ParamHelper.UPLOADED_IMAGE) MultipartFile file) {

        try {
            // Check if cookie is set
            if (request.getCookies() == null || !request.getCookies()[0].getName().equals(CookieService.COOKIE_NAME)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cookie missing!");
            }
            // Check if uplaoded file is image
            if (ImageIO.read(file.getInputStream()) != null) {
                this.storageService.store(request, file);
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("The uploaded file is not an image!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/download")
    public ResponseEntity<Object> downloadImage(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null && request.getCookies()[0].getValue().equals(CookieService.COOKIE_NAME)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cookie missing!");
        }
        return this.storageService.download(request, response, PathManager.FILE_RESISTANT);
    }

}
