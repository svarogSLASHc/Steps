package com.test.pedometer.data;

import android.content.Context;

import com.test.pedometer.ui.service.FStepService;

public class PedomenetController {
    private static PedomenetController INSTANCE;
    private final PedomenterManager pedomenterManager;

    public static PedomenetController getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new PedomenetController(context);
        }
        return INSTANCE;
    }

    private PedomenetController(Context context){
        pedomenterManager = PedomenterManager.getInstance(context);
    }

    public void onCreate(){
        pedomenterManager.onCreate();
    }

    public void registerListener(FStepService.StepCountListener listener) {
        pedomenterManager.registerListener(listener);
    }

    public void unregisterListener(FStepService.StepCountListener listener) {
        pedomenterManager.unregisterListener(listener);
    }

    public void onDestroy(){
        pedomenterManager.onDestroy();
    }
}
