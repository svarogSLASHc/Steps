package com.test.pedometer.data.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.test.pedometer.domain.StepCountListener;

import java.util.concurrent.TimeUnit;

import static android.content.Context.SENSOR_SERVICE;

public class PedometerManager implements SensorEventListener {
    private static String TAG = "PedometerManager";
    private final static long MICROSECONDS_IN_ONE_MINUTE = 60000000;
    private SensorManager sensorManager;
    private StepCountListener stepCountListener;
    private Sensor stepCounter;
    private int lastCount = -1;

    public static PedometerManager getInstance(Context context) {
        return new PedometerManager(context);
    }

    private PedometerManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI,(int) TimeUnit.SECONDS.toMicros(10));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final int type = event.sensor.getType();
        if (type == Sensor.TYPE_STEP_COUNTER) {
            if (lastCount == -1) {
                Log.i(TAG, "Initializing initial steps count: " + (int) event.values[0]);
                lastCount = (int) event.values[0];
            }
            final int stepsRegistered = (int) event.values[0] - lastCount;
            Log.v(TAG, String.format("Since start: %d. since last: %d" ,(int) event.values[0], stepsRegistered));
            Log.v(TAG, "Events count:" + event.values.length);
            postSensorChange(stepsRegistered);
            lastCount = (int) event.values[0];
        }
    }

    public void registerStepListener(StepCountListener listener) {
        this.stepCountListener = listener;
    }

    public void onDestroy() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.v(TAG, "onAccuracyChanged:" + accuracy);
    }

    public void reset() {
        lastCount =  -1;
    }

    /**
     * Posts a step sensor event, both for the counter and the detector,
     * to the registered listeners.
     *
     * @param value The event value, if any
     */
    private void postSensorChange(final int value) {
        if ( null != stepCountListener) {
            stepCountListener.onStepCount(value);
        }
    }
}
