package com.test.pedometer.ui.acitivty_recognition;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;
import com.test.pedometer.R;

import java.util.List;

/**
 * Utility methods used in this sample.
 */
public class UtilsRecognition {

    private UtilsRecognition() {}

    public static String mapRawActivityToString(Context context, String text, List<DetectedActivity> activities) {
        final StringBuilder result = new StringBuilder();
        for (DetectedActivity activity : activities) {
            result.append(String.format(text,
                    UtilsRecognition.getActivityString(context, activity.getType()), activity.getConfidence()));
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
   public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }
}