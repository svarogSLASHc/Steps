package com.test.pedometer.data.activity_recognition;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;
import com.test.pedometer.ui.acitivty_recognition.Constants;

import java.util.ArrayList;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ActivityRecognitionController {
    private static ActivityRecognitionController INSTANCE;
    private final Context context;
    private final ActivityRecognitionClient mActivityRecognitionClient;
    private PublishSubject<ArrayList<DetectedActivity>> activitiesSubject = PublishSubject.create();

    private ActivityRecognitionController(Context context) {
        this.context = context;
        mActivityRecognitionClient = new ActivityRecognitionClient(context);
    }

    public static ActivityRecognitionController getInstance(Context context){
        if (null == INSTANCE){
            INSTANCE = new ActivityRecognitionController(context.getApplicationContext());
        }
        return INSTANCE;
    }


    public void requestActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent());

        task.addOnSuccessListener(result -> {
        });

        task.addOnFailureListener(e -> {

        });
    }

    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(result -> {

        });

        task.addOnFailureListener(e -> {

        });
    }

    public void detectedNew(ArrayList<DetectedActivity> activities) {
        activitiesSubject.onNext(activities);
    }

    public Observable<ArrayList<DetectedActivity>> activitiesObservable() {
        return activitiesSubject;
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(context, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}