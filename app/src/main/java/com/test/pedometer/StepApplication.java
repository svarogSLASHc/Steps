package com.test.pedometer;

import android.app.Application;
import android.content.Context;

import com.test.pedometer.data.ObjectInstances;

public class StepApplication extends Application {
    private static ObjectInstances objectInstances;

    public static StepApplication getInstance(Context context){
        return (StepApplication)context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        objectInstances = ObjectInstances.getInstance(this);
    }

    public ObjectInstances getObjectInstances(){
        return objectInstances;
    }
}
