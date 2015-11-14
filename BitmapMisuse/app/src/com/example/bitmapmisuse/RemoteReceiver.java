package com.example.bitmapmisuse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RemoteReceiver extends BroadcastReceiver {
    private static final String TAG = "RemoteReceiver";
    public static final String ACTION = "com.example.bitmapmisuse.BROADCAST_REMOTE";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, action);
    }
}
