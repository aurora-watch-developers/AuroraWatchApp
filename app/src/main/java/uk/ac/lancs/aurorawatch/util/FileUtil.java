package uk.ac.lancs.aurorawatch.util;

import java.io.File;

/**
 * File utilities
 */
public class FileUtil {

    public static boolean existsAndIsUpToDate(String path, long maxAgeSecs) {
        long currentTime = System.currentTimeMillis();
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        long lastModified = file.lastModified();
        return lastModified + maxAgeSecs * 1000 >= currentTime;
    }

    public static void deleteQuietly(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        try {
            file.delete();
        } catch (Exception ignore) {
        }
    }
}
