package com.example.bitmapmisuse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocalReceiver extends BroadcastReceiver {
    private static final String TAG = "LocalReceiver";
    public static final String ACTION = "com.example.bitmapmisuse.BROADCAST";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, action);
    }
}
