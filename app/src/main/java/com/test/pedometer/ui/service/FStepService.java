package com.test.pedometer.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.test.pedometer.data.PedomenetController;

/**
 * The main application service. Does stuff.
 */
public class FStepService extends Service {
private PedomenetController pedomenetController;

    @Override
    public void onCreate() {
        Log.v("FStepService", "Creating the service");
        super.onCreate();
        pedomenetController = PedomenetController.getInstance(getApplicationContext());
        pedomenetController.onCreate();
    }


    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public void onDestroy() {
        Log.v("FStepService", "Destroying the service");
        super.onDestroy();
        pedomenetController.onDestroy();
    }

    public interface StepCountListener {

        void onStepDataUpdate(int stepCount);

        void onStep(int count);

    }
}
