package com.test.pedometer.ui.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.test.pedometer.R;
import com.test.pedometer.domain.runner.StepDetectorTestRunner;

public class FStepService extends Service {
    private static String TAG = "FStepService";
    private static final String SERVICE_ACTION_STOP = "Stop_Action";
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private final int NOTIFICATION_ID = 1011;
    private StepDetectorTestRunner stepDetectorTestRunner;

    @Override
    public void onCreate() {
        Log.v(TAG, "Creating the context");
        super.onCreate();
        stepDetectorTestRunner = StepDetectorTestRunner.getInstance(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent.getAction() && SERVICE_ACTION_STOP.equals(intent.getAction())) {
            stop();
        } else {
            startForegroundNotify();
            stepDetectorTestRunner.start();
            stepDetectorTestRunner.isRunning()
                    .subscribe(isRunning -> {
                        if (!isRunning) {
                            stop();
                        }
                    });
        }
        return Service.START_NOT_STICKY;
    }

    private void startForegroundNotify() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

                .setContentTitle("Step Counter is running")
                .setContentText("App is actively counting steps")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroying the context");
        super.onDestroy();
        stepDetectorTestRunner.stop();
    }

    private void stop() {
        stopForeground(true);
        stopSelf();
    }
}
