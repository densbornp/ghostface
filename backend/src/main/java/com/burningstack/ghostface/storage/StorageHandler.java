package com.burningstack.ghostface.storage;


import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The StorageHandler handles the HashMap where all the uploaded and converted
 * images are temporarily stored
 */
@ApplicationScoped
@Slf4j
public class StorageHandler {
    private final ConcurrentHashMap<String, ImageStorage> imageStorage;
    public static final String COOKIE_NAME = "user_session";
    public static final String COOKIE_PATH = "/";

    private StorageHandler() {
        imageStorage = new ConcurrentHashMap<>();
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
        log.info("Currently the application is used by: {} user(s).", this.imageStorage.size());
    }

    public boolean isClientActive(String cookie) {
        return this.imageStorage.containsKey(cookie);
    }

    public String createCookie() {
        UUID uuid = UUID.randomUUID();
        String cookie = String.valueOf(uuid);
        setImageStorage(cookie, new ImageStorage(null, null));
        return cookie;
    }
}
