package com.raizlabs.jonathan_cole.imprivatatestbed.utility

import com.google.android.gms.location.DetectedActivity
import com.raizlabs.jonathan_cole.imprivatatestbed.detection.ActivityDataHistories


class ActivityDetectionModelAdapter(private val data: ActivityDataHistories) {
    init {

    }
    // Calculate an overall "yes" or "no" for each sensor based on history data
    private val SPACE = ", "
    private val NEW_LINE = "\n"
    fun getOverallEvaluation(): Boolean {
        return getConfirmFromActivityRecognition() || getConfirmFromStepCounter() ||
                getConfirmFromStepDetector() || getConfirmFromSignificantMotion()
    }

    fun getStepCounterString(): String {
        var output = ""
        output += "Counter: " + (if (getConfirmFromStepCounter()) "Yes" else "No ") + " - ${data.stepCounter.size} values"
        output += data.stepCounter.map { it.values[0].toInt() }.toString()
        return output
    }

    fun getStepDetectorString(): String {
        var output = ""
        output += "Detector: " + (if (getConfirmFromStepDetector()) "Yes" else "No ") + " - ${data.stepDetector.size} values"
        output += data.stepDetector.map { it.values[0].toInt() }.toString()
        return output
    }

    fun getProximityString(): String {
        var output = ""
        output += "Proximity: " + (if (getConfirmFromProximityDetector()) "Yes" else "No ") + " - ${data.proximityDetector.size} values"
        output += data.proximityDetector.map { it.values[0] }.toString()
        return output
    }

    fun getSignificantMotionString(): String {
        var output = ""
        output += "Significant Motion: " + (if (getConfirmFromSignificantMotion()) "Yes" else "No ") + " - ${data.significantMotion.size} values"
        output += data.significantMotion.map { it.values[0] }.toString()
        return output
    }

    fun getActivityRecognitionString(): String {
        var output = ""
        output += "Activity Recognition: " + (if (getConfirmFromActivityRecognition()) "Yes" else "No ") + " - ${data.activityRecognition.size} values"
        output += data.activityRecognition.map { it.event.getActivityConfidence(DetectedActivity.ON_FOOT) }
        return output
    }


    fun formatHistoryData() =
            StringBuilder()
                    .append(if (getOverallEvaluation()) "Walking" else "No movement")
                    .append(SPACE)
                    .append(getStepCounterString())
                    .append(SPACE)
                    .append(getStepDetectorString())
                    .append(SPACE)
                    .append(getSignificantMotionString())
                    .append(SPACE)
                    .append(getActivityRecognitionString())
                    .append(SPACE)
                    .append(getProximityString())
                    .toString()

    private fun getConfirmFromProximityDetector() =
            data.proximityDetector.any {
                it.values[0] < 2.0f
            }

    private fun getConfirmFromStepCounter(): Boolean {
        return data.stepCounter.count() >= 2
    }

    private fun getConfirmFromStepDetector(): Boolean {
        return data.stepDetector.isNotEmpty()
    }

    private fun getConfirmFromActivityRecognition() =
            data.activityRecognition.any {
                it.event.getActivityConfidence(DetectedActivity.ON_FOOT) > 50

            }

    private fun getConfirmFromSignificantMotion(): Boolean {
        return data.significantMotion.isNotEmpty()
    }
}