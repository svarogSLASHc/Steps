package com.test.pedometer.data;

import android.os.Build;

public class DeviceIdManager {
    private static final String SPACE = " ";

    private DeviceIdManager(){
    }

    public static String getDeviceName(){
        return Build.MANUFACTURER
                + SPACE +  Build.MODEL
                + SPACE + "API"  + SPACE + Build.VERSION.SDK_INT + SPACE + Build.VERSION.CODENAME;
    }
}
