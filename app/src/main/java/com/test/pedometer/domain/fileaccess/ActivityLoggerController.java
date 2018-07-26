package com.test.pedometer.domain.fileaccess;

import android.content.Context;

import com.test.pedometer.data.DeviceIdManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityLoggerController extends AbstractLoggerController {
    private static final String SPACE = ",";
    private static final String FILE_NAME = "activity_recognition.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HH-mm-ss");
    private static ActivityLoggerController INSTANCE;

    private ActivityLoggerController(Context context) {
        super(context);
    }

    public static ActivityLoggerController getInstance(Context context) {
       if (null == INSTANCE){
           INSTANCE = new ActivityLoggerController(context);
       }
       return INSTANCE;
    }

    @Override
    public void add(String data) {
        final StringBuilder stringBuilder = new StringBuilder(DATE_FORMAT.format(new Date()));
        loggerRepository.add(
                stringBuilder
                        .append(SPACE)
                        .append(DeviceIdManager.getDeviceName())
                        .append(SPACE)
                        .append(data)
                        .toString());
    }

    @Override
    protected String getFileName() {
        return FILE_NAME;
    }
}
