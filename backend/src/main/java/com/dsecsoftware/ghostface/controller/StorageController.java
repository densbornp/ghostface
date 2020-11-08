package com.dsecsoftware.ghostface.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import com.dsecsoftware.ghostface.ParamHelper;
import com.dsecsoftware.ghostface.PathManager;
import com.dsecsoftware.ghostface.services.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  @PostMapping("/upload")
  public ResponseEntity<Object> uploadFile(@RequestParam(ParamHelper.UPLOADED_IMAGE) MultipartFile file) {

      try {
        InputStream in = file.getInputStream();
          // Check if uplaoded file is image
          if(ImageIO.read(in) == null) {
            storageService.store(file);
            return ResponseEntity.status(HttpStatus.OK).build();
          } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("The uploaded file is not an image!");
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @GetMapping("/download")
  public ResponseEntity<Object> downloadImage() {
      return null;
  }

}
