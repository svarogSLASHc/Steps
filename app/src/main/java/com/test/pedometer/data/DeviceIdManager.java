package com.test.pedometer.data;

import android.os.Build;

public class DeviceIdManager {

    private DeviceIdManager(){
    }

    public static String getDeviceName(){
        return Build.MODEL;
    }
}
