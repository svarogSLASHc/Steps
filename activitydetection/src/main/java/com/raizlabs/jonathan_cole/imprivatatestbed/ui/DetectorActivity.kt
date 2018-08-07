package com.raizlabs.jonathan_cole.imprivatatestbed.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.android.gms.location.DetectedActivity
import com.raizlabs.jonathan_cole.imprivatatestbed.R
import com.raizlabs.jonathan_cole.imprivatatestbed.detection.ActivityDataHistories
import com.raizlabs.jonathan_cole.imprivatatestbed.detection.ActivityDetectorService
import com.raizlabs.jonathan_cole.imprivatatestbed.manager.BroadcastManager
import com.raizlabs.jonathan_cole.imprivatatestbed.manager.BuzzManager
import com.raizlabs.jonathan_cole.imprivatatestbed.manager.TTSManager
import com.raizlabs.jonathan_cole.imprivatatestbed.utility.ActivityDetectionModelAdapter
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
        val adaptedData = ActivityDetectionModelAdapter(data)

        fun setOverallText() {
            overallEvaluationText.apply {
                val str = if (adaptedData.getOverallEvaluation()) "User is moving" else "No movement"
                if (adaptedData.getOverallEvaluation()) {
                    this.setTextColor(Color.parseColor("#17D650")) // Green
                } else {
                    this.setTextColor(Color.parseColor("#D62A17")) // Red
                }
                this.text = str
            }
        }

        counterText.text = adaptedData.getStepCounterString()
        detectorText.text = adaptedData.getStepDetectorString()
        proximityText.text = adaptedData.getProximityString()
        significantMotionText.text = adaptedData.getSignificantMotionString()
        activityRecognitionText.text = adaptedData.getActivityRecognitionString()

        setOverallText()

        // If the overall state has changed, read it out loud
        val newOverallState = adaptedData.getOverallEvaluation()
//        if (newOverallState != lastOverallState) {
            // Read it out loud
            val spokenString = if (newOverallState) "User is moving" else "No movement"
            TTSManager.getInstance(this).speak(spokenString)
            BuzzManager.getInstance(this).buzz(100)
//        }
        lastOverallState = newOverallState
    }
}
