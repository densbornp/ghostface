package com.burningstack.ghostface.storage;

import com.burningstack.ghostface.GhostfaceApplication;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The StorageHandler handles the HashMap where all the uploaded and converted
 * images are temporarily stored
 */
public class StorageHandler {
    private ConcurrentHashMap<String, ImageStorage> imageStorage;
    public static final String COOKIE_NAME = "user_session";
    public static final String COOKIE_PATH = "/";
    private static final String SYMBOLS = "ABCDEFGJKLMNPRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
    private static SecureRandom random;

    private static StorageHandler storageHandler = null;

    private StorageHandler() {
        imageStorage = new ConcurrentHashMap<>();
    }

    public static StorageHandler getInstance() {
        if(storageHandler == null) {
            storageHandler = new StorageHandler();
            random = new SecureRandom();
        }
        return storageHandler;
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
        GhostfaceApplication.LOGGER.info("Currently the application is used by: " + this.imageStorage.size() + " users.");
    }

    public boolean isClientActive(String cookie) {
        return this.imageStorage.keySet().contains(cookie);
    }

    public String createCookie() {
        char[] buffer = new char[256];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = SYMBOLS.charAt(random.nextInt(SYMBOLS.length()));
        }
        String cookie = new String(buffer);
        buffer = null;
        setImageStorage(cookie, new ImageStorage(null, null));
        // Encode cookie in Base64 and return it
        return cookie;
    }
}
