package com.test.pedometer.data;

import android.content.Context;

import com.test.pedometer.data.sensors.PedometerManager;
import com.test.pedometer.domain.pedometer.PedometerRepository;
import com.test.pedometer.domain.pedometer.PedometerRunnerController;

public class ObjectInstances {
    private static ObjectInstances INSTANCE;
    private final Context context;

    private ObjectInstances(Context context) {
        this.context = context;
    }

    public static ObjectInstances getInstance(Context context) {
        if (null == INSTANCE){
            INSTANCE = new  ObjectInstances(context);
        }
        return INSTANCE;
    }

    public PedometerRepository getPedometerRepository(){
        return PedometerManager.getInstance(context);
    }

    public PedometerRunnerController getPedometerController() {
        return PedometerRunnerController.getInstance(getPedometerRepository());
    }



}
