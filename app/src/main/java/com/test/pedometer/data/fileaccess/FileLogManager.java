package com.test.pedometer.data.fileaccess;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class FileLogManager {
    private static final String TAG = "FileLogManager";
    private final static String APP = "Pedometer";
    private static final String FILE_NAME = "impr.log";
    private static final String NEW_LINE = "\n";
    private static final Date date = new Date();
    private static FileLogManager INSTANCE;
    private final Context context;
    private final StringBuilder builder;

    private FileLogManager(Context context) {
        this.context = context;
        builder = new StringBuilder();
    }

    public static FileLogManager getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new FileLogManager(context.getApplicationContext());
        }
        return INSTANCE;
    }

    public void log(String logText) {
        Log.d(TAG, logText);
        builder.append(logText)
                .append(NEW_LINE);
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

    public String saveLog() {
        final String logMsg = builder.toString();
        writeBufferToFile(logMsg);
        builder.setLength(0);
        return logMsg;
    }

    public void clear() {
        getFile().delete();
        builder.setLength(0);
    }

    public void logToInternal(String msg) {
        FileWriter fw;
        try {
            fw = new FileWriter(new File(
                    context.getFilesDir().getAbsolutePath() + "/" + APP + ".log"),
                    true);
            date.setTime(System.currentTimeMillis());
            fw.write(date.toLocaleString() + " - " + msg + "\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToInternal() {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(new File(
                    context.getFilesDir().getAbsolutePath() + "/" + APP + ".log")));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Empty";
    }

    public void clearInternal() {
        new File(context.getFilesDir().getAbsolutePath() + "/" + APP + ".log").delete();
    }

    private void writeBufferToFile(String text) {

        try {
            File file = getFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(text.getBytes());
            fileOutputStream.close();

        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private File getFile() {
        return new File(context.getCacheDir() + FILE_NAME);
    }
}