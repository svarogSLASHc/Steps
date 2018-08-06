package com.raizlabs.jonathan_cole.imprivatatestbed.service

import android.app.Activity
import android.hardware.*
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionResult

class ActivityDetectorManager(val service: ActivityDetectorService): SensorEventListener, TriggerEventListener() {

    companion object {
        const val TAG = "ActivityDetectorManager"
    }

    var mStepCounterHistory = mutableListOf<EventReading>()
    var mStepDetectorHistory = mutableListOf<EventReading>()
    var mSignificantMotionDetectorHistory = mutableListOf<EventReading>()
    var mActivityRecognitionHistory = mutableListOf<ActivityReading>()
    var mProximityDetectorHistory = mutableListOf<EventReading>()

    lateinit var mSensorManager: SensorManager
    lateinit var mStepCounter: Sensor
    lateinit var mStepDetector: Sensor
    lateinit var mSignificantMotionDetector: Sensor
    lateinit var mProximityDetector: Sensor

    private var mHandler: Handler? = null

    private val maxReportLatencyUs: Int = 0 // Setting to 0 will disable batching for sensors that support it.
    private val samplingPeriodUs: Int = SensorManager.SENSOR_DELAY_FASTEST

    var windowSizeMS = 10 * 1000 // 10 seconds

    init {
        registerListeners()
        beginAutomaticRefreshing(1000)
    }

    fun registerListeners() {
        Log.i(TAG, "Registering Listeners...")
        // Get the default sensor for the sensor type from the SenorManager
        mSensorManager = service.getSystemService(Activity.SENSOR_SERVICE) as SensorManager

        // sensorType is either Sensor.TYPE_STEP_COUNTER or Sensor.TYPE_STEP_DETECTOR
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        logSensorInfo(mStepCounter, "Step Counter")

        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        logSensorInfo(mStepDetector, "Step Detector")

        mSignificantMotionDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)
        logSensorInfo(mSignificantMotionDetector, "Significant Motion")

        mProximityDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        logSensorInfo(mProximityDetector, "Proximity Detector")

        // https://developer.android.com/reference/android/hardware/SensorManager.html#registerListener(android.hardware.SensorEventListener,%20android.hardware.Sensor,%20int,%20int)
        val counterIsBatched = mSensorManager.registerListener(this, mStepCounter, samplingPeriodUs, maxReportLatencyUs)
        val detectorIsBatched = mSensorManager.registerListener(this, mStepDetector, samplingPeriodUs, maxReportLatencyUs)

        if (!counterIsBatched) {
            val logOutput = "Could not register step counter in batch mode. Falling back to continuous mode."
            Log.w(TAG, logOutput)
            Toast.makeText(service, logOutput, Toast.LENGTH_SHORT).show()
        }

        if (!detectorIsBatched) {
            val logOutput = "Could not register step detector in batch mode. Falling back to continuous mode."
            Log.w(TAG, logOutput)
            Toast.makeText(service, logOutput, Toast.LENGTH_SHORT).show()
        }

        mSensorManager.registerListener(this, mProximityDetector, samplingPeriodUs, maxReportLatencyUs)
        mSensorManager.requestTriggerSensor(this, mSignificantMotionDetector)

    }

    fun unregisterListeners() {
        mSensorManager.unregisterListener(this)
    }

    private fun logSensorInfo(sensor: Sensor, name: String) {
        var output = """
            _
            -------- $name --------
            Internal name: ${sensor.name}
            FIFO max event count: ${sensor.fifoMaxEventCount} ${ if (sensor.fifoMaxEventCount == 0) "(batching disabled)" else ""}
            FIFO reserved event count: ${sensor.fifoReservedEventCount}
            Is wake up sensor: ${sensor.isWakeUpSensor} ${ if (!sensor.isWakeUpSensor) "(will need to register partial wake lock to receive events while screen is off)" else "" }
            Reporting mode: ${sensor.reportingMode}
            Type: ${sensor.stringType}
            Power: ${sensor.power}mA

        """.trimIndent()
        Log.d(TAG, output)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        Log.d(TAG, "Accuracy is now $accuracy.")
    }

    // Sensor event listener
    override fun onSensorChanged(event: SensorEvent?) {

        if (event == null) return

//        Log.d(TAG, "Received a sensor event from ${event.sensor.stringType}")

        // Add the event to its corresponding history list
        when (event.sensor.type) {
            Sensor.TYPE_STEP_DETECTOR -> {
                prune()
                mStepDetectorHistory.add(EventReading(event.sensor, event.values.clone()))
                dispatch()
            }
            Sensor.TYPE_STEP_COUNTER -> {
                prune()
                mStepCounterHistory.add(EventReading(event.sensor, event.values.clone()))
                dispatch()
            }
            Sensor.TYPE_PROXIMITY -> {
                prune()
                mProximityDetectorHistory.add(EventReading(event.sensor, event.values.clone()))
                dispatch()

            }
        }
    }

    // Trigger event listener
    override fun onTrigger(event: TriggerEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_SIGNIFICANT_MOTION) {

            prune()
            mSignificantMotionDetectorHistory.add(EventReading(event.sensor, event.values.clone()))
            dispatch()

            // Re-register the trigger sensor (required for one-shot)
            mSensorManager.requestTriggerSensor(this, mSignificantMotionDetector)
        }

    }

    // Activity Recognition API listener
    fun onRegisterNewActivityData(result: ActivityRecognitionResult) {
        prune()
        mActivityRecognitionHistory.add(ActivityReading(result))
        dispatch()
    }

    /**
     * Remove readings that are outside the time window specified by `windowSizeMS`.
     */
    fun prune() {
        val currentTimeMS = System.currentTimeMillis()

        mStepDetectorHistory = mStepDetectorHistory.filter {
            currentTimeMS - it.timestampMS < windowSizeMS
        } as MutableList

        // Keeping only individual events, _not_ individual steps (a reading
        // may represent many steps at once, esp. if batched)
        mStepCounterHistory = mStepCounterHistory.filter {
            currentTimeMS - it.timestampMS < windowSizeMS
        } as MutableList

        mSignificantMotionDetectorHistory = mSignificantMotionDetectorHistory.filter {
            currentTimeMS - it.timestampMS < windowSizeMS
        } as MutableList

        mActivityRecognitionHistory = mActivityRecognitionHistory.filter {
            currentTimeMS - it.timestampMS < windowSizeMS
        } as MutableList

        mProximityDetectorHistory = mProximityDetectorHistory.filter {
            currentTimeMS - it.timestampMS < windowSizeMS
        } as MutableList

    }

    fun dispatch() {
        // Give the model to the service to be broadcasted to listeners.
        val model = ActivityDataHistories(
            mStepCounterHistory,
            mStepDetectorHistory,
            mSignificantMotionDetectorHistory,
            mActivityRecognitionHistory,
            mProximityDetectorHistory
        )
        service.dispatchNewDataHistory(model)
    }

    /**
     * Begins a repeating prune/dispatch at an interval of `frequencyMS` milliseconds.
     */
    fun beginAutomaticRefreshing(frequencyMS: Long) {
        mHandler = Handler()

        mHandler?.postDelayed(object : Runnable {
            override fun run() {
                prune()
                dispatch()
                mHandler?.postDelayed(this, frequencyMS)
            }
        }, frequencyMS)
    }

    /**
     * Stops the automatic prune/dispatch loop.
     */
    fun endAutomaticRefreshing() {
        mHandler?.removeCallbacksAndMessages(null)
    }

}