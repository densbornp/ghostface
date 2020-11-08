package com.dsecsoftware.ghostface;

import com.dsecsoftware.ghostface.services.StorageService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GhostfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GhostfaceApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.removeUploads();
            storageService.init();
        };
    }

}
