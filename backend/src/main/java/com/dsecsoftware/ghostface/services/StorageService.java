package com.dsecsoftware.ghostface.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dsecsoftware.ghostface.PathManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    public void init() {
        new File(PathManager.UPLOAD_DIR).mkdir(); // Create uploads folder
    }

    public ResponseEntity<Object> store(HttpServletRequest request, MultipartFile file) {
        File dir = new File(PathManager.UPLOAD_DIR, request.getCookies()[0].getValue());
        dir.mkdir();
        File newFile = new File(
                dir + File.separator + PathManager.FILE_ORIGINAL + getExtension(file.getOriginalFilename()));
        Path targetLocation = newFile.toPath();
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.status(HttpStatus.OK).body("Image successfully uploaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not write file!");
    }

    public ResponseEntity<Object> download(HttpServletRequest request, HttpServletResponse response, String fileName) {
        try {
            String cookie = request.getCookies()[0].getValue();
            File searchedFile = findFile(cookie, fileName);
            String name = searchedFile.getName();
            Path filePath = searchedFile.toPath();
            byte[] fileBytes = Files.readAllBytes(filePath);
            if (name == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not find file!");
            }
            response.setHeader("Content-Disposition", "attachment; filename=" + name);
            response.setContentLength(fileBytes.length);
            response.getOutputStream().write(fileBytes);
            response.flushBuffer();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    public void removeUploads() {

    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index);
    }

    private File findFile(String cookie, String fileName) {
        File dir = new File(PathManager.UPLOAD_DIR, cookie);
        File[] list = dir.listFiles();
        if (list != null) {
            for (File fil : list) {
                if (fil.getName().contains(fileName)) {
                    return fil;
                }
            }
        }
        return null;
    }

}
