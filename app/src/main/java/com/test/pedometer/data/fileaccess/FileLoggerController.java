package com.test.pedometer.data.fileaccess;

import android.content.Context;

import com.test.pedometer.data.DeviceIdManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLoggerController {
    private static final String SPACE = ". ";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
    private FileLogManager fileLogManager;


    public static FileLoggerController newInstance(Context context) {
        return new FileLoggerController(context);
    }

    private FileLoggerController(Context context) {
        fileLogManager = FileLogManager.getInstance(context);
    }

    public void logRedometerData(String data) {
        final StringBuilder stringBuilder = new StringBuilder(DATE_FORMAT.format(new Date()));
        fileLogManager.log(
                stringBuilder
                        .append(SPACE)
                        .append(DeviceIdManager.getDeviceName())
                        .append(SPACE)
                        .append(data)
                        .toString());
    }

    public File getLogFile() throws FileNotFoundException {
        return fileLogManager.getLogFile();
    }

    public String getLogFileAsString() throws FileNotFoundException {
        return fileLogManager.getLogFileAsString();
    }

    public void clear() {
        fileLogManager.clear();
    }
}
