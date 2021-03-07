package com.dsecsoftware.ghostface.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dsecsoftware.ghostface.PathManager;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    public void init() {
        new File(PathManager.UPLOAD_DIR).mkdir(); // Create uploads folder
    }

    public ResponseEntity<Object> storeImage(String cookie, MultipartFile file) {
        File dir = new File(PathManager.UPLOAD_DIR, cookie);
        if (dir.exists()) {
            dir.delete();
        }
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

    public ResponseEntity<Object> download(String cookie, HttpServletResponse response) {
        try {
            File searchedFile = findFile(cookie, PathManager.FILE_RESISTANT);
            if (searchedFile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find file!");
            }
            String name = searchedFile.getName();
            Path filePath = searchedFile.toPath();
            byte[] fileBytes = Files.readAllBytes(filePath);
            response.setHeader("Content-Disposition", "attachment; filename=" + name);
            response.setContentLength(fileBytes.length);
            response.getOutputStream().write(fileBytes);
            response.flushBuffer();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected download error occurred!");
    }

    public ResponseEntity<Object> getImage(String cookie, HttpServletRequest request, HttpServletResponse response) {
        try {
            File originalImage = findFile(cookie, PathManager.FILE_ORIGINAL);
            if (originalImage == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find file!");
            }
            InputStream in = new FileInputStream(ResourceUtils.getFile(originalImage.getPath()));
            byte[] media = in.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return new ResponseEntity<>(media, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find file!");
    }

    public ResponseEntity<Object> getTmpConvertedImage(String cookie, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            File tmpConvertedImage = findFile(cookie, PathManager.FILE_TEMP);
            if (tmpConvertedImage == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find file!");
            }
            InputStream in = new FileInputStream(ResourceUtils.getFile(tmpConvertedImage.getPath()));
            byte[] media = in.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return new ResponseEntity<>(media, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find file!");
    }

    public void removeUploads() {
        // TODO delete uploads folder
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index);
    }

    private File findFile(String cookie, String fileName) {
        File dir = new File(PathManager.UPLOAD_DIR, cookie);
        File[] list = dir.listFiles();
        if (list != null) {
            for (File file : list) {
                if (file.getName().contains(fileName)) {
                    return file;
                }
            }
        }
        return null;
    }

}
