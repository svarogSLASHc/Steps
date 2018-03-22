package com.test.pedometer.data.sensors;

import android.content.Context;

import com.test.pedometer.domain.StepCountListener;

public class PedometerController {
    private static PedometerController INSTANCE;
    private final PedometerManager pedometerManager;

    public static PedometerController getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new PedometerController(context);
        }
        return INSTANCE;
    }

    private PedometerController(Context context){
        pedometerManager = PedometerManager.getInstance(context);
    }

    public void onCreate(){
        pedometerManager.onCreate();
    }

    public void registerListener(StepCountListener listener) {
        pedometerManager.registerListener(listener);
    }

    public void unregisterListener(StepCountListener listener) {
        pedometerManager.unregisterListener(listener);
    }

    public void onDestroy(){
        pedometerManager.onDestroy();
    }
}
