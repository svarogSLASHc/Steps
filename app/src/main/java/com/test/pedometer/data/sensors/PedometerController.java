package com.test.pedometer.data.sensors;

import android.content.Context;

import com.test.pedometer.domain.StepCountListener;

public class PedometerController {
    private static PedometerController INSTANCE;
    private final PedomenterManager pedomenterManager;

    public static PedometerController getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new PedometerController(context);
        }
        return INSTANCE;
    }

    private PedometerController(Context context){
        pedomenterManager = PedomenterManager.getInstance(context);
    }

    public void onCreate(){
        pedomenterManager.onCreate();
    }

    public void registerListener(StepCountListener listener) {
        pedomenterManager.registerListener(listener);
    }

    public void unregisterListener(StepCountListener listener) {
        pedomenterManager.unregisterListener(listener);
    }

    public void onDestroy(){
        pedomenterManager.onDestroy();
    }
}
