package com.raizlabs.jonathan_cole.imprivatatestbed.manager

import android.app.Activity
import android.content.Context
import android.hardware.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityRecognitionResult
import com.raizlabs.jonathan_cole.imprivatatestbed.detection.ActivityDataHistories
import com.raizlabs.jonathan_cole.imprivatatestbed.detection.ActivityReading
import com.raizlabs.jonathan_cole.imprivatatestbed.detection.EventReading

class ActivityDetectorManager(val context: Context) : SensorEventListener, TriggerEventListener() {

    companion object {
        const val TAG = "ActivityDetectorManager"
    }

    private val maxReportLatencyUs: Int = 0 // Setting to 0 will disable batching for sensors that support it.
    private val samplingPeriodUs: Int = SensorManager.SENSOR_DELAY_FASTEST
    private val broadcastManager = BroadcastManager.getInstance(context)
    private var mStepCounterHistory = mutableListOf<EventReading>()
    private var mStepDetectorHistory = mutableListOf<EventReading>()
    private var mSignificantMotionDetectorHistory = mutableListOf<EventReading>()
    private var mActivityRecognitionHistory = mutableListOf<ActivityReading>()
    private var mProximityDetectorHistory = mutableListOf<EventReading>()
    private var mActivityRecognitionClient: ActivityRecognitionClient? = null

    lateinit var mSensorManager: SensorManager
    lateinit var mStepCounter: Sensor
    lateinit var mStepDetector: Sensor
    lateinit var mSignificantMotionDetector: Sensor
    lateinit var mProximityDetector: Sensor
    private var dispatchCallback: DispatchNewDataHistoryCallback? = null

    private var mHandler: Handler? = null


    var windowSizeMS = 10 * 1000 // 10 seconds

    fun register() {
        registerListeners()
        beginAutomaticRefreshing(1000)
        registerActivityRecognitionListeners()
    }

    fun registerWithDispatch(callback:DispatchNewDataHistoryCallback?){
        dispatchCallback = callback
        register()
    }

    private fun registerActivityRecognitionListeners() {
        // Set up the activity recognition API, which will communicate with the ActivityDetectorService.
        mActivityRecognitionClient = ActivityRecognitionClient(context)
        mActivityRecognitionClient?.requestActivityUpdates(0, broadcastManager.getActivityDetectionPendingIntent())
        broadcastManager.registerForActivityRecognitionUpdates { onRegisterNewActivityData(it) }
    }

    private fun registerListeners() {
        Log.i(TAG, "Registering Listeners...")
        // Get the default sensor for the sensor type from the SenorManager
        mSensorManager = context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager

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
            Toast.makeText(context, logOutput, Toast.LENGTH_SHORT).show()
        }

        if (!detectorIsBatched) {
            val logOutput = "Could not register step detector in batch mode. Falling back to continuous mode."
            Log.w(TAG, logOutput)
            Toast.makeText(context, logOutput, Toast.LENGTH_SHORT).show()
        }

        mSensorManager.registerListener(this, mProximityDetector, samplingPeriodUs, maxReportLatencyUs)
        mSensorManager.requestTriggerSensor(this, mSignificantMotionDetector)
    }

    fun unregisterListeners() {
        mSensorManager.unregisterListener(this)
        mActivityRecognitionClient?.removeActivityUpdates(broadcastManager.getActivityDetectionPendingIntent())
        broadcastManager.unregisterFromActivityRecognitionUpdates()
        endAutomaticRefreshing()
    }

    private fun logSensorInfo(sensor: Sensor, name: String) {
        var output = """
            _
            -------- $name --------
            Internal name: ${sensor.name}
            FIFO max event count: ${sensor.fifoMaxEventCount} ${if (sensor.fifoMaxEventCount == 0) "(batching disabled)" else ""}
            FIFO reserved event count: ${sensor.fifoReservedEventCount}
            Is wake up sensor: ${sensor.isWakeUpSensor} ${if (!sensor.isWakeUpSensor) "(will need to register partial wake lock to receive events while screen is off)" else ""}
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
                mStepDetectorHistory.add(EventReading(event.sensor, event.values.clone()))
                update()
            }
            Sensor.TYPE_STEP_COUNTER -> {
                mStepCounterHistory.add(EventReading(event.sensor, event.values.clone()))
                update()
            }
            Sensor.TYPE_PROXIMITY -> {
                mProximityDetectorHistory.add(EventReading(event.sensor, event.values.clone()))
                update()
            }
        }
    }

    // Trigger event listener
    override fun onTrigger(event: TriggerEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_SIGNIFICANT_MOTION) {

            mSignificantMotionDetectorHistory.add(EventReading(event.sensor, event.values.clone()))
            update()

            // Re-register the trigger sensor (required for one-shot)
            mSensorManager.requestTriggerSensor(this, mSignificantMotionDetector)
        }

    }

    // Activity Recognition API listener
    private fun onRegisterNewActivityData(result: ActivityRecognitionResult) {
        mActivityRecognitionHistory.add(ActivityReading(result))
        update()
    }

    private fun update() {
//        prune()
//        dispatch()
    }

    private fun updateAll() {
        prune()
        dispatch()
    }

    /**
     * Remove readings that are outside the time window specified by `windowSizeMS`.
     */
    private fun prune() {
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

    private fun dispatch() {
        // Give the model to the context to be broadcasted to listeners.
        val model = ActivityDataHistories(
                mStepCounterHistory,
                mStepDetectorHistory,
                mSignificantMotionDetectorHistory,
                mActivityRecognitionHistory,
                mProximityDetectorHistory
        )
        broadcastManager.dispatchNewDataHistory(model)
        dispatchCallback?.onNewData(model)
    }

    /**
     * Begins a repeating prune/dispatch at an interval of `frequencyMS` milliseconds.
     */
    private fun beginAutomaticRefreshing(frequencyMS: Long) {
        mHandler = Handler(Looper.getMainLooper())

        mHandler?.postDelayed(object : Runnable {
            override fun run() {
                updateAll()
                mHandler?.postDelayed(this, frequencyMS)
            }
        }, frequencyMS)
    }

    /**
     * Stops the automatic prune/dispatch loop.
     */
    private fun endAutomaticRefreshing() {
        mHandler?.removeCallbacksAndMessages(null)
    }

    interface DispatchNewDataHistoryCallback {
        fun onNewData(action: ActivityDataHistories)
    }
}