package com.dsecsoftware.ghostface.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cookie")
public class CookieController {

    @RequestMapping(method = RequestMethod.POST)
    public void setCookie() {
        //
    }
}
