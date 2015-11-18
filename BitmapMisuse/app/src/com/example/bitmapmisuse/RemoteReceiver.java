package com.example.bitmapmisuse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RemoteReceiver extends BroadcastReceiver {
    private static final String TAG = "RemoteReceiver";
    public static final String REGULAR = "com.example.bitmapmisuse.BROADCAST_REMOTE";
    public static final String ORDERED = "com.example.bitmapmisuse.BROADCAST";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ORDERED))
            setResultCode(2);
        Log.v(TAG, action);
    }
}
