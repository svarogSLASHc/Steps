package com.test.pedometer.domain.fileaccess;

import android.content.Context;

import java.util.Date;

public class DebugLoggerController extends AbstractLoggerController  {
    private static final String NEW_LINE = "\n";
    private static final String FILE_NAME = "Pedometer.log";
    private static final Date date = new Date();
    private static DebugLoggerController INSTANCE;

    private DebugLoggerController(Context context) {
       super(context);
    }

    public static DebugLoggerController getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new DebugLoggerController(context);
        }
        return INSTANCE;
    }

    public void logDebugMsg(String msg) {
        date.setTime(System.currentTimeMillis());
        loggerRepository.save(date.toLocaleString() + " - " + msg + NEW_LINE);
    }

    @Override
    protected String getFileName() {
        return FILE_NAME;
    }
}
