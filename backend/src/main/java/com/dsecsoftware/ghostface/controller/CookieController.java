package com.dsecsoftware.ghostface.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.dsecsoftware.ghostface.services.CookieService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CookieController {

    public CookieController() {
    }

    @PostMapping("/cookie")
    public ResponseEntity<Object> setCookie(@CookieValue(value = "user_session", defaultValue = "") String cookie,
            HttpServletResponse response) {
        CookieService cookieService = CookieService.getInstance();
        if (!cookie.isEmpty() && cookieService.isClientActive(cookie)) {
            return ResponseEntity.badRequest().body("Cookie already set!");
        }
        String cookieValue = cookieService.createCookie();
        Cookie newCookie = new Cookie(CookieService.COOKIE_NAME, cookieValue);
        newCookie.setSecure(true);
        newCookie.setHttpOnly(true);
        newCookie.setPath(CookieService.COOKIE_PATH);
        // add cookie to response
        response.addCookie(newCookie);
        cookieService.addClient(newCookie.getValue());
        return ResponseEntity.ok().body("New Cookie added!");
    }
}
