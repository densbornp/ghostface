package com.dsecsoftware.ghostface;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GhostfaceApplication {

	public static void main(String[] args) {
      new File(PathManager.UPLOAD_DIR).mkdir(); // Create uploads folder
		  SpringApplication.run(GhostfaceApplication.class, args);
	}

}
