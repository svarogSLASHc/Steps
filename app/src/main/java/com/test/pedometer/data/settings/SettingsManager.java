package com.test.pedometer.data.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager implements SettingsFields {
    private static SettingsManager INSTANCE;
    private final SharedPreferences preferences;

    private SettingsManager(Context context) {
        this.preferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    public static SettingsManager getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new SettingsManager(context.getApplicationContext());
        }
        return INSTANCE;
    }


    public int getSteps() {
        return preferences.getInt(FIELD_STEPS, DEFAULT_STEPS);
    }

    public void setSteps(int steps) {
        preferences
                .edit()
                .putInt(FIELD_STEPS, steps)
                .apply();
    }

    public int getRounds() {
        return preferences.getInt(FIELD_ROUNDS, DEFAULT_ROUNDS);
    }

    public void setRounds(int rounds) {
        preferences
                .edit()
                .putInt(FIELD_ROUNDS, rounds)
                .apply();
    }

    public int getRoundTime() {
        return preferences.getInt(FIELD_ROUND_TIME, DEFAULT_ROUND_TIME);
    }

    public void setRoundTime(int seconds) {
        preferences
                .edit()
                .putInt(FIELD_ROUND_TIME, seconds)
                .apply();
    }

    public String getPocket() {
        return preferences.getString(FIELD_POCKET, "");
    }

    public void setPocket(String pocket) {
        preferences
                .edit()
                .putString(FIELD_POCKET, pocket)
                .apply();
    }

}
