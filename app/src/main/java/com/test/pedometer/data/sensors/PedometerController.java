package com.test.pedometer.data.sensors;

import android.content.Context;

import com.test.pedometer.domain.StepCountListener;
import com.test.pedometer.domain.StepDetectorListener;

public class PedometerController {
    private static PedometerController INSTANCE;
    private final PedometerManager pedometerManager;

    public static PedometerController getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PedometerController(context);
        }
        return INSTANCE;
    }

    private PedometerController(Context context) {
        pedometerManager = PedometerManager.getInstance(context);
    }

    public void registerCounterListener(StepCountListener listener) {
        pedometerManager.registerStepListener(listener);
    }

    public void registerDetectorListener(StepDetectorListener listener) {
        pedometerManager.registerStepDetectorListener(listener);
    }

    public void reset() {
        pedometerManager.reset();
    }

    public void onDestroy() {
        pedometerManager.onDestroy();
    }
}
