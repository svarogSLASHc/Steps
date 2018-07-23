package com.test.pedometer.data.whitelist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.test.pedometer.domain.whitelist.PopupRepository;

public class PopupRepositoryImpl implements PopupRepository {
    private static PopupRepositoryImpl INSTANCE;
    private final SharedPreferences preferences;

    private PopupRepositoryImpl(Context context) {
        this.preferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    public static PopupRepositoryImpl getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new PopupRepositoryImpl(context);
        }
        return INSTANCE;
    }

    @Override
    public boolean showed() {
        return preferences.getBoolean(FIELD_SHOWED, DEFAULT_FIELD_SHOWED);
    }

    @Override
    public boolean isSumsung(Context context) {
        return hasBatteryMonitor(context);
    }

    @Override
    public void setShowed() {
        preferences.edit()
                .putBoolean(FIELD_SHOWED, true)
                .apply();
    }


    private boolean hasBatteryMonitor(Context context) {
        final Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity"));


        final Intent intentSecond = new Intent(Intent.ACTION_MAIN);
        intentSecond.setClassName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity");
        intentSecond.addCategory(Intent.CATEGORY_LAUNCHER);
        intentSecond.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return (isAvailable(context, intent) || isAvailable(context, intentSecond));
    }

    private boolean isAvailable(Context context, Intent intent) {
        final PackageManager mgr = context.getPackageManager();
        return mgr.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

}

