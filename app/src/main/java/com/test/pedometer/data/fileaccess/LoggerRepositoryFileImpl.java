package com.test.pedometer.data.fileaccess;

import android.content.Context;
import android.util.Log;

import com.test.pedometer.domain.fileaccess.LoggerRepository;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoggerRepositoryFileImpl implements LoggerRepository {
    private static final String TAG = LoggerRepositoryFileImpl.class.getCanonicalName();
    private static final String NEW_LINE = "\n";
    private final Context context;
    private final StringBuilder builder;
    private final String FILE_NAME;


    private LoggerRepositoryFileImpl(Context context, String fileName) {
        this.context = context;
        this.FILE_NAME = fileName;
        builder = new StringBuilder();
    }

    public static LoggerRepositoryFileImpl newInstance(Context context, String fileName) {
        return new LoggerRepositoryFileImpl(context.getApplicationContext(), fileName);
    }


    @Override
    public void add(@NotNull String msg) {
        Log.d(TAG, msg);
        builder.append(msg)
                .append(NEW_LINE);
    }

    @Override
    public void save(@NotNull String msg) {
        Log.d(TAG, msg);
        writeBufferToFile(msg);
    }
    @Override
    public String saveAll() {
        final String logMsg = builder.toString();
        writeBufferToFile(logMsg);
        builder.setLength(0);
        return logMsg;
    }

    @Override
    public void clear() {
        getFile().delete();
        builder.setLength(0);
    }

    @Override
    public String getRawText() {
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
