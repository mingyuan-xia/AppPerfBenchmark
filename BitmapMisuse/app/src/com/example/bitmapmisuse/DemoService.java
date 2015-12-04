package com.example.bitmapmisuse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DemoService extends Service {
    private static final String TAG = "DemoService" ;
    public static final String ACTION = "com.example.bitmapmisuse.ACTION";
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
}
