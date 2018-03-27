package com.test.pedometer.ui.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

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

    public void onDestroy(){
        if (textToSpeech != null ){
            if (textToSpeech.isSpeaking()){
                textToSpeech.stop();
            }
            textToSpeech.shutdown();
        }
    }
}
