package com.dsecsoftware.ghostface;

import java.io.File;

public class PathManager {

    public static String ROOT_DIR = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath();
    public static final String UPLOAD_DIR = ROOT_DIR + "/uploads";
    public static final String FILE_ORIGINAL = "original";
    public static final String FILE_TEMP = "tmp";
    public static final String FILE_RESISTANT = "resistant";

    private PathManager() {
    }
}
