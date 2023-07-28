package com.burningstack.ghostface.controller;

import com.burningstack.ghostface.storage.StorageHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
public class CookieController {

    @Inject
    private StorageHandler storageHandler;

    public CookieController() {
    }

    @PostMapping("/cookie")
    public ResponseEntity<Object> setCookie(@CookieValue(value = "user_session", defaultValue = "") String cookie,
            HttpServletResponse response) {
        if (!cookie.isEmpty() && storageHandler.isClientActive(cookie)) {
            return ResponseEntity.badRequest().body("Cookie already set!");
        }
        String cookieValue = storageHandler.createCookie();
        Cookie newCookie = new Cookie(StorageHandler.COOKIE_NAME, cookieValue);
        newCookie.setSecure(true);
        newCookie.setHttpOnly(true);
        newCookie.setPath(StorageHandler.COOKIE_PATH);
        // add cookie to response
        response.addCookie(newCookie);
        storageHandler.printActiveClients();
        return ResponseEntity.ok().body("New Cookie added!");
    }
}
