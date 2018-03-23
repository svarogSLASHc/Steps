package com.test.pedometer.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.test.pedometer.data.sensors.StepDetectorTestRunner;

public class FStepService extends Service {
    private static String TAG = "FStepService";
    private StepDetectorTestRunner stepDetectorTestRunner;

    @Override
    public void onCreate() {
        Log.v(TAG, "Creating the service");
        super.onCreate();
        stepDetectorTestRunner = StepDetectorTestRunner.getInstance(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stepDetectorTestRunner.start();
        stepDetectorTestRunner.isRunning()
                .subscribe(isRunning -> {
                    if (!isRunning){
                        stopSelf();
                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroying the service");
        super.onDestroy();
        stepDetectorTestRunner.stop();
    }
}
