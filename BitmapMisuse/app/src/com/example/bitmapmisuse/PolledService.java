package com.example.bitmapmisuse;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PolledService extends Service {
    private static final String TAG = "PolledService" ;
    public static final String ACTION = "com.example.bitmapmisuse.ACTION";

    private Notification.Builder mNotifiBuilder;
    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        mNotifiBuilder = new Notification.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle("New Message")
                .setContentText("You have new message!")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new PollingThread().start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification() {
        Intent i = new Intent(this, SecondActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifiBuilder.setContentIntent(pendingIntent);
        mManager.notify(0, mNotifiBuilder.build());
    }

    private int count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
            Log.v(TAG, "Polling...");
            count ++;
            if (count % 5 == 0) {
                showNotification();
                Log.v(TAG, "New message!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }
}