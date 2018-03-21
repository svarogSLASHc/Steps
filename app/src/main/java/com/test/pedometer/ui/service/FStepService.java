package com.test.pedometer.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.test.pedometer.data.PedomenetController;

public class FStepService extends Service {
    private static String TAG = "FStepService";
    private PedomenetController pedomenetController;

    @Override
    public void onCreate() {
        Log.v(TAG, "Creating the service");
        super.onCreate();
        pedomenetController = PedomenetController.getInstance(getApplicationContext());
        pedomenetController.onCreate();
    }


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroying the service");
        super.onDestroy();
        pedomenetController.onDestroy();
    }
}
