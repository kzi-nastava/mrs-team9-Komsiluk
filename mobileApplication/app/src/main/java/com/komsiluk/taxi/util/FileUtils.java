package com.komsiluk.taxi.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    public static File from(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            String fileName = "profile_" + System.currentTimeMillis();

            File tempFile = new File(context.getCacheDir(), fileName);
            tempFile.deleteOnExit();

            FileOutputStream out = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            out.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create file from uri", e);
        }
    }
}

