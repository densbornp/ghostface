package com.dsecsoftware.ghostface.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.dsecsoftware.ghostface.services.CookieService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CookieController {

    private CookieService cookieService;

    public CookieController() {
        this.cookieService = new CookieService();
    }

    @PostMapping("/cookie")
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(CookieService.COOKIE_NAME, this.cookieService.createCookie());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath(CookieService.COOKIE_PATH);

        // add cookie to response
        response.addCookie(cookie);
        return "Cookie added!";
    }
}
