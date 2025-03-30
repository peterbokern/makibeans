package com.makibeans.util;

import org.apache.tika.Tika;

/**
 * Utility class for detecting the content type of files using Apache Tika.
 */

public class FileTypeUtils {
    private static final Tika tika = new Tika();

    public static String detectImageContentType(byte[] data) {
        return tika.detect(data);
    }
}
