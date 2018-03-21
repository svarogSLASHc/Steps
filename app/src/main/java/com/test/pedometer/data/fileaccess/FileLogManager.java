package com.test.pedometer.data.fileaccess;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileLogManager {
    private static final String TAG = "FileLogManager";
    private static final String FILE_NAME = "impr.log";
    private static final String NEW_LINE = "\n";
    private static FileLogManager INSTANCE;
    private final Context context;
    private StringBuilder builder;

    private FileLogManager(Context context) {
        this.context = context;
    }

    public static FileLogManager getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new FileLogManager(context.getApplicationContext());
        }
        return INSTANCE;
    }

    public void log(String logText) {
        if (null == builder) {
            builder = new StringBuilder(logText);
        } else {
            builder.append(NEW_LINE)
                    .append(logText);
        }
        Log.d(TAG, logText);
        writeBufferToFile();
    }

    public File getLogFile() throws FileNotFoundException {
        File file = getFile();
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }

    public String getLogFileAsString() {
        String ret = "";
        try {
            InputStream inputStream = new FileInputStream(getFile());

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder
                            .append(receiveString)
                            .append(NEW_LINE);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void clear() {
        builder = null;
        getFile().deleteOnExit();
    }

    private void writeBufferToFile() {
        if (null == builder) {
            return;
        }

        try {
            File file = getFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write((builder.toString()).getBytes());
            fileOutputStream.close();

        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private File getFile() {
        return new File(context.getCacheDir() + FILE_NAME);
    }
}