package com.test.pedometer.domain.fileaccess;

import android.content.Context;
import android.text.TextUtils;

import com.test.pedometer.data.DeviceIdManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PedometerLoggerController extends AbstractLoggerController {
    private static final String SPACE = ", ";
    private static final String FILE_NAME = "impr.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HH-mm-ss");
    private static PedometerLoggerController INSTANCE;

    private PedometerLoggerController(Context context) {
        super(context);
    }

    public static PedometerLoggerController getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new PedometerLoggerController(context);
        }
        return INSTANCE;
    }

    @Override
    public void add(String data) {
        if (TextUtils.isEmpty(data)) {
            data = "No results";
        }
        loggerRepository.add(
                new StringBuilder(DATE_FORMAT.format(new Date()))
                        .append(SPACE)
                        .append(data)
                        .append("\n")
                        .toString());
    }


    public String formatStart(String data) {
        return new StringBuilder(DeviceIdManager.getDeviceName())
                .append(SPACE)
                .append(data)
                .append("\n")
                .toString();
    }

    @Override
    protected String getFileName() {
        return FILE_NAME;
    }
}
