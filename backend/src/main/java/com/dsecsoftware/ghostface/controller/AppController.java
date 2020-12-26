package com.dsecsoftware.ghostface.controller;

import com.dsecsoftware.ghostface.PathManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "http://localhost:4200")
public class AppController {

    @GetMapping("/")
    public String index() {
        return PathManager.ROOT_DIR + "frontend/src/index.html";
    }

    @GetMapping("/error")
    public String error() {
        return "An error occurred!";
    }
}
