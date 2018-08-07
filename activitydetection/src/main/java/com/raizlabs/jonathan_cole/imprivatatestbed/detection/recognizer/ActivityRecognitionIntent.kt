package com.raizlabs.jonathan_cole.imprivatatestbed.detection.recognizer

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.raizlabs.jonathan_cole.imprivatatestbed.manager.BroadcastManager

// Created when the Activity Recognition API dispatches an update. Broadcasts the data
// locally and then destroys itself.
class ActivityRecognitionIntent(name: String? = "ActivityRecognitionIntent") : IntentService(name) {

    override fun onHandleIntent(intent: Intent?) {
        intent.let {
            if (ActivityRecognitionResult.hasResult(it)) {
                val result = ActivityRecognitionResult.extractResult(it)
                BroadcastManager.getInstance(this).broadcastActivityRecognitionResult(result)
            }
        }
    }
}