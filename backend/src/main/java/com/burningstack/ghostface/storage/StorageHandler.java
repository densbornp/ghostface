package com.burningstack.ghostface.storage;

import com.burningstack.ghostface.GhostfaceApplication;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The StorageHandler handles the HashMap where all the uploaded and converted
 * images are temporarily stored
 */
@Component
public class StorageHandler {
    private final ConcurrentHashMap<String, ImageStorage> imageStorage;
    public static final String COOKIE_NAME = "user_session";
    public static final String COOKIE_PATH = "/";
    private static final String CHARS = "ABCDEFGJKLMNPRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$&()*+-/:<=>?@[]^_{}";
    private static SecureRandom random;
    private static final int COOKIE_LENGTH = 2048;

    private StorageHandler() {
        imageStorage = new ConcurrentHashMap<>();
        random = new SecureRandom();
    }

    public ConcurrentHashMap<String, ImageStorage> getAllImagesStorages() {
        return new ConcurrentHashMap<>(this.imageStorage);
    }

    public ImageStorage getImageStorage(String cookie) {
        return this.imageStorage.get(cookie);
    }

    public void setImageStorage(String cookie, ImageStorage imageStorage) {
        this.imageStorage.put(cookie, imageStorage);
    }

    public void removeImageStorage(String cookie) {
        this.imageStorage.remove(cookie);
    }

    public void printActiveClients() {
        GhostfaceApplication.LOGGER.info("Currently the application is used by: {} user(s).",  this.imageStorage.size());
    }

    public boolean isClientActive(String cookie) {
        return this.imageStorage.containsKey(cookie);
    }

    public String createCookie() {
        char[] buffer = new char[COOKIE_LENGTH];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = CHARS.charAt(random.nextInt(CHARS.length()));
        }
        String cookie = new String(buffer);
        buffer = null;
        setImageStorage(cookie, new ImageStorage(null, null));
        return cookie;
    }
}