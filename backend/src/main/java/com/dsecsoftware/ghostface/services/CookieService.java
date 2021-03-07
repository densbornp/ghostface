package com.dsecsoftware.ghostface.services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CookieService {

    public static final String COOKIE_NAME = "user_session";
    public static final String COOKIE_PATH = "ghostface.dsecsoftware.com/";
    private static final String SYMBOLS = "ABCDEFGJKLMNPRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!*+-=";
    private static SecureRandom random;
    private static CookieService cookieService;
    private List<String> activeClients;

    private CookieService() {
        this.activeClients = new ArrayList<String>();
    }

    public static CookieService getInstance() {
        if (cookieService == null) {
            cookieService = new CookieService();
            random = new SecureRandom();
        }
        return cookieService;
    }

    public void addClient(String cookie) {
        this.activeClients.add(cookie);
    }

    public void removeClient(String cookie) {
        this.activeClients.remove(cookie);
    }

    public boolean isClientActive(String cookie) {
        return this.activeClients.contains(cookie);
    }

    public String createCookie() {
        char[] buffer = new char[128];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = SYMBOLS.charAt(random.nextInt(SYMBOLS.length()));
        }
        String cookie = new String(buffer);
        buffer = null;
        return cookie;
    }

}
