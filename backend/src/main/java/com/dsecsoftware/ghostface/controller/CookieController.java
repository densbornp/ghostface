package com.dsecsoftware.ghostface.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dsecsoftware.ghostface.services.CookieService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CookieController {

    private CookieService cookieService;

    public CookieController() {
        this.cookieService = new CookieService();
    }

    @PostMapping("/cookie")
    public ResponseEntity<Object> setCookie(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null && request.getCookies()[0].getName() == CookieService.COOKIE_NAME) {
            return ResponseEntity.badRequest().body("Cookie already set!");
        }
        Cookie cookie = new Cookie(CookieService.COOKIE_NAME, this.cookieService.createCookie());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath(CookieService.COOKIE_PATH);
        // add cookie to response
        response.addCookie(cookie);
        return ResponseEntity.ok().body("Cookie added!");
    }
}
