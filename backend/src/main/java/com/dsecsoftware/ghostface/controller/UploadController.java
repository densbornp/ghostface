package com.dsecsoftware.ghostface.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;

import com.dsecsoftware.ghostface.ParamHelper;
import com.dsecsoftware.ghostface.PathManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

  @PostMapping("/upload")
  public ResponseEntity<Object> uploadFile(@RequestParam(ParamHelper.UPLOADED_IMAGE) MultipartFile file) {

      File image = new File(PathManager.UPLOAD_DIR, file.getOriginalFilename());

      // TODO non image file gets written too ?!?
      try (InputStream in = file.getInputStream(); FileOutputStream fout = new FileOutputStream(image)) {
          // Check if uplaoded file is image
          String mimetype= new MimetypesFileTypeMap().getContentType(image);
          String type = mimetype.split("/")[0];
          if(type.equals("image")) {
            int line;
            while ((line = in.read()) != -1) {
                fout.write(line);
            }
            fout.flush();
            return ResponseEntity.status(HttpStatus.OK).build();
          } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("The uploaded file is not an image!");
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
