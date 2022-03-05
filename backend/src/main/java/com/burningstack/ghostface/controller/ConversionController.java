package com.burningstack.ghostface.controller;

import com.burningstack.ghostface.ParamHelper;
import com.burningstack.ghostface.services.ConService;
import com.burningstack.ghostface.storage.StorageHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConversionController {

    private ConService conService;
    private static final String VALID_COOKIE_MISSING = "Valid Cookie missing!";

    public ConversionController() {
        this.conService = new ConService();
    }

    @PostMapping("/convert")
    public ResponseEntity<Object> convertImage(@CookieValue(value = "user_session", defaultValue = "") String cookie, @RequestParam(ParamHelper.CONVERSION_TYPE_PARAM) int type) {
        if (cookie.isEmpty() || !StorageHandler.getInstance().isClientActive(cookie)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VALID_COOKIE_MISSING);
        }
        return null;
    }
}
