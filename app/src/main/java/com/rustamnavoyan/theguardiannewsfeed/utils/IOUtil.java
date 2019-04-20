package com.rustamnavoyan.theguardiannewsfeed.utils;

import java.io.Closeable;

public class IOUtil {
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            // Nothing to do
        }
    }
}
