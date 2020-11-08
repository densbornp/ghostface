package com.dsecsoftware.ghostface.services;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class CookieService {

    public static final String COOKIE_NAME = "user_session";
    public static final String COOKIE_PATH = "ghostface.dsecsoftware.com/";
    private static final String SYMBOLS = "ABCDEFGJKLMNPRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!*+-&()=%";
    private final Random random;

    public CookieService() {
        this.random = new SecureRandom();
    }

    public String createCookie() {
        char[] buffer = new char[32];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = SYMBOLS.charAt(random.nextInt(SYMBOLS.length()));
        }
        String cookie = new String(buffer);
        buffer = null;
        return cookie;
    }

}
