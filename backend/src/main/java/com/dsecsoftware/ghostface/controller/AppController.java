package com.dsecsoftware.ghostface.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping({ "/faq", "/policy", "/error" })
    public String index() {
        return "forward:/";
    }
}
