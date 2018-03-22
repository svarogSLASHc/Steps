package com.test.pedometer.data.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.test.pedometer.domain.StepCountListener;

import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;

public class PedometerManager implements SensorEventListener {
    private static String TAG = "PedometerManager";
    private SensorManager mSensorManager;
    private Sensor mStepCounter, mStepDetector;
    private Handler mUiHandler;
    private ArrayList<StepCountListener> mListeners = new ArrayList<>();
    private int mLastCount, mInitialCount;
    private boolean mInitialCountInitialized;
    private int mLastDetectorCount;

    public static PedometerManager getInstance(Context context) {
        return new PedometerManager(context);
    }

    private PedometerManager(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    public void onCreate() {
        // Batching for the step counter doesn't make sense (the buffer holds
        // just one step counter event anyway, as it's not a continuous event)
        mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);

        // We do instead use batching for the step detector sensor
        final int reportInterval = calcSensorReportInterval(mStepDetector);
        mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL,
                reportInterval * 1000 /*  micro seconds */);
    }

    /**
     * Calculates the maximum sensor report interval, based on the
     * hardware sensor events buffer size, to avoid dropping steps.
     *
     * @param stepCounter The Step Counter sensor
     * @return Returns the optimal update interval, in milliseconds
     */
    private static int calcSensorReportInterval(Sensor stepCounter) {
        // We assume that, normally, a person won't do more than
        // two steps in a second (worst case: running)
        final int fifoSize = stepCounter.getFifoReservedEventCount();
        if (fifoSize > 1) {
            return (fifoSize / 2) * 1000;
        }

        // In this case, the device seems not to have an HW-backed
        // sensor events buffer. We're assuming that there's no
        // batching going on, so we don't really need the alarms.
        return 0;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        final int type = event.sensor.getType();
        if (type == Sensor.TYPE_STEP_COUNTER) {
            Log.v(TAG, "New step counter event. Value: " + (int) event.values[0]);

            if (!mInitialCountInitialized) {
                Log.i(TAG, "Initializing initial steps count: " + (int) event.values[0]);
                mInitialCount = (int) event.values[0];
                mInitialCountInitialized = true;
            }

            mLastCount = (int) event.values[0] - mInitialCount;

            postSensorChange(mLastCount, Sensor.TYPE_STEP_COUNTER);
        } else if (type == Sensor.TYPE_STEP_DETECTOR) {
            mLastDetectorCount++;
            Log.v(TAG, "New step detector event. Updated count: " + mLastDetectorCount);

            postSensorChange(mLastDetectorCount, Sensor.TYPE_STEP_DETECTOR);
        }
    }

    /**
     * Posts a step sensor event, both for the counter and the detector,
     * to the registered listeners.
     *
     * @param value The event value, if any
     * @param type  The sensor type
     */
    private void postSensorChange(final int value, final int type) {
        if (Looper.getMainLooper().equals(Looper.myLooper())) {
            // UI thread
            for (StepCountListener l : mListeners) {
                if (type == Sensor.TYPE_STEP_COUNTER) {
                    l.onStepDataUpdate(value);
                } else if (type == Sensor.TYPE_STEP_DETECTOR) {
                    l.onStep(value);
                }
            }
        } else {
            // Non-UI thread
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (StepCountListener listener : mListeners) {
                        if (type == Sensor.TYPE_STEP_COUNTER) {
                            listener.onStepDataUpdate(value);
                        } else if (type == Sensor.TYPE_STEP_DETECTOR) {
                            listener.onStep(value);
                        }
                    }
                }
            });
        }
    }

    public void registerListener(StepCountListener listener) {
        mListeners.add(listener);
        listener.onStepDataUpdate(mLastCount);
        mSensorManager.flush(this);
    }

    public void unregisterListener(StepCountListener listener) {
        mListeners.remove(listener);
    }


    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        mLastCount = mInitialCount = mLastDetectorCount = 0;
        mInitialCountInitialized = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
