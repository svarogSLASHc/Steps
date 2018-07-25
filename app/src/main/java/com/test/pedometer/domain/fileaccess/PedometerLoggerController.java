package com.test.pedometer.domain.fileaccess;

import android.content.Context;

import com.test.pedometer.data.DeviceIdManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PedometerLoggerController extends AbstractLoggerController {
    private static final String SPACE = ",";
    private static final String FILE_NAME = "impr.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HH-mm-ss");
    private static PedometerLoggerController INSTANCE;

    private PedometerLoggerController(Context context) {
        super(context);
    }

    public static PedometerLoggerController getInstance(Context context) {
       if (null == INSTANCE){
           INSTANCE = new PedometerLoggerController(context);
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
