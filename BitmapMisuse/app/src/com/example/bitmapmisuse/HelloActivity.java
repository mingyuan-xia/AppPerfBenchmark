package com.example.bitmapmisuse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

public class HelloActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        TextView tvFinish2ndActivity = (TextView)findViewById(R.id.tv_finish_2ndActivity);
        tvFinish2ndActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv2 = (TextView)findViewById(R.id.tv_local_broadcast);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager.getInstance(HelloActivity.this).
                        sendBroadcast(new Intent("com.example.bitmapmisuse.LOCAL_BROADCAST"));
            }
        });

        TextView tv3 = (TextView)findViewById(R.id.tv_local_broadcast_sync);
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LocalBroadcastManager.getInstance(HelloActivity.this).sendBroadcastSync(
                                new Intent("com.example.bitmapmisuse.LOCAL_BROADCAST_SYNC"));
                    }
                }).start();
            }
        });
    }
}