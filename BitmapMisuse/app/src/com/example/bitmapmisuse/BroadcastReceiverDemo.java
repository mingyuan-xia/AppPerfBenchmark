package com.example.bitmapmisuse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiverDemo extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiverDemo" ;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, action);
        Log.v(TAG, intent.getExtras().getString("key"));
    }
}
