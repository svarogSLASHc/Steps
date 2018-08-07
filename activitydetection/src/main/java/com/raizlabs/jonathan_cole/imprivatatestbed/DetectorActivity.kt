package com.raizlabs.jonathan_cole.imprivatatestbed

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.android.gms.location.DetectedActivity
import com.raizlabs.jonathan_cole.imprivatatestbed.manager.BroadcastManager
import com.raizlabs.jonathan_cole.imprivatatestbed.manager.BuzzManager
import com.raizlabs.jonathan_cole.imprivatatestbed.manager.TTSManager
import com.raizlabs.jonathan_cole.imprivatatestbed.service.ActivityDataHistories
import com.raizlabs.jonathan_cole.imprivatatestbed.service.ActivityDetectorService
import kotlinx.android.synthetic.main.activity_main_detection.*

/**
 * The overall structure is this:
 *
 * MainActivity sets up an ActivityDetectorService. This will listen for sensor events
 * while keeping a windowed history of prior events. When any one sensor updates (and
 * every second by default), all history data will be broadcasted locally and picked up
 * by MainActivity (see ActivityHistoryBroadcastReceiver).
 *
 * The Android Activity Recognition API uses PendingIntents to dispatch events, so an
 * additional step is required for setup in MainActivity, as shown below. Essentially,
 * an ActivityRecognitionClient must be set up, and configured to use the included
 * `ActivityRecognitionIntent`. This will pass the data along to the ActivityDetectorService.
 *
 */
class DetectorActivity : AppCompatActivity() {

    private lateinit var broadcastManager: BroadcastManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ActivityRecognitionAdapter
    private lateinit var mService: Intent

    var lastOverallState = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_detection)

        title = "Walkaway Detector"
        broadcastManager = BroadcastManager.getInstance(this.applicationContext)

        viewAdapter = ActivityRecognitionAdapter(listOf(
                ActivityRecognitionModel(DetectedActivity.STILL, "Still"),
                ActivityRecognitionModel(DetectedActivity.WALKING, "Walking"),
                ActivityRecognitionModel(DetectedActivity.RUNNING, "Running"),
                ActivityRecognitionModel(DetectedActivity.ON_FOOT, "On Foot"),
//                ActivityRecognitionModel(DetectedActivity.IN_VEHICLE, "In Vehicle"),
//                ActivityRecognitionModel(DetectedActivity.ON_BICYCLE, "On Bicycle"),
//                ActivityRecognitionModel(DetectedActivity.TILTING, "Tilting"),
                ActivityRecognitionModel(DetectedActivity.UNKNOWN, "Unknown")
        ))

        recyclerView = activity_recognition_rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DetectorActivity)
            adapter = viewAdapter
        }

        viewAdapter.notifyDataSetChanged()

        broadcastManager.registerForHistoryUpdates { onReceivedNewHistoryData(it) }
        // Set up the service for activity detection (our custom windowed history keeping code)
        mService = Intent(this, ActivityDetectorService::class.java)
        startService(mService)
    }

    override fun onDestroy() {
        TTSManager.getInstance(this).onDestroy()

        stopService(mService)
        broadcastManager.unregisterFromNewDataHistory()
        super.onDestroy()
    }

    /**
     * Update the UI with the new state of the data history given by the service.
     */
    private fun onReceivedNewHistoryData(data: ActivityDataHistories) {

        // There's a lot of optimization that can be done here. Will be revisiting this in
        // the Java rewrite.

        // Update activity recognition sliders with the most recent activity data
        data.activityRecognition.lastOrNull()?.let {
            viewAdapter.updateDataWithResult(it.event)
        }

        // Calculate an overall "yes" or "no" for each sensor based on history data

        fun getConfirmFromProximityDetector() =
                data.proximityDetector.any {
                    it.values[0] < 2.0f
                }

        fun getConfirmFromStepCounter(): Boolean {
            return data.stepCounter.count() >= 2
        }

        fun getConfirmFromStepDetector(): Boolean {
            return data.stepDetector.isNotEmpty()
        }

        fun getConfirmFromActivityRecognition() =
                data.activityRecognition.any {
                    it.event.getActivityConfidence(DetectedActivity.ON_FOOT) > 50

                }

        fun getConfirmFromSignificantMotion(): Boolean {
            return data.significantMotion.isNotEmpty()
        }

        fun getOverallEvaluation(): Boolean {
            return getConfirmFromActivityRecognition() || getConfirmFromStepCounter() ||
                    getConfirmFromStepDetector() || getConfirmFromSignificantMotion()
        }

        fun getStepCounterString(): String {
            var output = ""
            output += "Counter: " + (if (getConfirmFromStepCounter()) "Yes" else "No ") + " - ${data.stepCounter.size} values" + "\n"
            output += data.stepCounter.map { it.values[0].toInt() }.toString()
            return output
        }

        fun getStepDetectorString(): String {
            var output = ""
            output += "Detector: " + (if (getConfirmFromStepDetector()) "Yes" else "No ") + " - ${data.stepDetector.size} values" + "\n"
            output += data.stepDetector.map { it.values[0].toInt() }.toString()
            return output
        }

        fun getProximityString(): String {
            var output = ""
            output += "Proximity: " + (if (getConfirmFromProximityDetector()) "Yes" else "No ") + " - ${data.proximityDetector.size} values" + "\n"
            output += data.proximityDetector.map { it.values[0] }.toString()
            return output
        }

        fun getSignificantMotionString(): String {
            var output = ""
            output += "Significant Motion: " + (if (getConfirmFromSignificantMotion()) "Yes" else "No ") + " - ${data.significantMotion.size} values" + "\n"
            output += data.significantMotion.map { it.values[0] }.toString()
            return output
        }

        fun getActivityRecognitionString(): String {
            var output = ""
            output += "Activity Recognition: " + (if (getConfirmFromActivityRecognition()) "Yes" else "No ") + " - ${data.activityRecognition.size} values" + "\n"
            output += data.activityRecognition.map { it.event.getActivityConfidence(DetectedActivity.ON_FOOT) }
            return output
        }

        fun setOverallText() {
            overallEvaluationText.apply {
                val str = if (getOverallEvaluation()) "User is moving" else "No movement"
                if (getOverallEvaluation()) {
                    this.setTextColor(Color.parseColor("#17D650")) // Green
                } else {
                    this.setTextColor(Color.parseColor("#D62A17")) // Red
                }
                this.text = str
            }
        }

        counterText.text = getStepCounterString()
        detectorText.text = getStepDetectorString()
        proximityText.text = getProximityString()
        significantMotionText.text = getSignificantMotionString()
        activityRecognitionText.text = getActivityRecognitionString()

        setOverallText()

        // If the overall state has changed, read it out loud
        val newOverallState = getOverallEvaluation()
        if (newOverallState != lastOverallState) {
            // Read it out loud
            val spokenString = if (newOverallState) "User is moving" else "No movement"
            TTSManager.getInstance(this).speak(spokenString)
            BuzzManager.getInstance(this).buzz(100)
        }
        lastOverallState = newOverallState
    }
}
