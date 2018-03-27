package com.test.pedometer.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.test.pedometer.data.sensors.StepDetectorTestRunner;

import java.util.Locale;

public class FStepService extends Service {
    private static String TAG = "FStepService";
    private StepDetectorTestRunner stepDetectorTestRunner;
    private TextToSpeech textToSpeech;


    @Override
    public void onCreate() {
        Log.v(TAG, "Creating the service");
        super.onCreate();
        stepDetectorTestRunner = StepDetectorTestRunner.getInstance(this.getApplicationContext());
        textToSpeech =  new TextToSpeech(getApplicationContext(), status -> {
        });
        textToSpeech.setLanguage(Locale.US);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stepDetectorTestRunner.start();
        stepDetectorTestRunner.isRunning()
                .subscribe(isRunning -> {
                    if (!isRunning){
                        stopSelf();
                    }
                    else{
                        String text = "start";
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, text);
                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroying the service");
        super.onDestroy();
        stepDetectorTestRunner.stop();
        textToSpeech.shutdown();
    }
}
