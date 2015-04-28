package org.aurorawatchdevs.aurorawatch.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;

/**
 * IO utilities.
 */
public class IOUtil {

    private IOUtil() {

    }

    public static void closeQuietly(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception ignore) {
            }
        }
    }

    public static void closeQuietly(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void closeQuietly(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void closeQuietly(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception ignore) {
            }
        }
    }
}
