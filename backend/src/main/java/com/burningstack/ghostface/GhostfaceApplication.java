package com.burningstack.ghostface;

import nu.pattern.OpenCV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GhostfaceApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(GhostfaceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GhostfaceApplication.class, args);
        OpenCV.loadLocally();
    }
}
