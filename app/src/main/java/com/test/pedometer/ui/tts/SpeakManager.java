package com.test.pedometer.ui.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Locale;

import rx.Observable;

public class SpeakManager {
    private static SpeakManager INSTANCE;
    private TextToSpeech textToSpeech;

    private SpeakManager(Context context){
        textToSpeech =  new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR){
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setPitch(0.8f);
            }
            else {
                Log.e("SpeakManager", "Error while init TextToSpeech");
            }
        });
    }

    public static SpeakManager getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new SpeakManager(context.getApplicationContext());
        }
        return INSTANCE;
    }

    public void speak(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, text);
    }

    public Observable<String> startSpeak(String pocket) {
        return Observable.fromCallable(() -> {
            String message;
            if (pocket.toLowerCase().contains("looking")) {
                message = "walk while looking at your phone";
            } else {
                message = "put your phone in your " + pocket;
            }
            speak(message);
            Thread.sleep(8000);
            return pocket;
        });
    }

    public Observable<Integer> roundSpeak(int stepsN, Integer currentRound) {
        return Observable.fromCallable(() -> {
            speak(String.format("Round %d. When you hear the word Go, take %d steps",
                    currentRound,
                    stepsN));
            Thread.sleep(4000);
            speak("Ready.");
            Thread.sleep(1000);
            speak("set");
            Thread.sleep(1000);
            speak("go");
            Thread.sleep(500);
            return currentRound;
        });
    }

    @NonNull
    public Observable<String> stopSpeak(int roundsN,int currentRound) {
        return Observable.fromCallable(() -> {
            if (currentRound < roundsN) {
               speak("stop");
            } else {
               speak("done with all rounds");
            }
            Thread.sleep(1000);
            return String.valueOf(currentRound);
        });
    }

}
