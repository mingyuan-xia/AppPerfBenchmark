package com.example.bitmapmisuse;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zzzkky on 15-10-20.
 */
public class IntentActivity extends Activity {
    private static final String TAG = "IntentActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);

        TextView tv1 = (TextView)findViewById(R.id.textview1);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IntentActivity.this, HelloActivity.class);
                startActivity(intent);
            }
        });

        TextView tv2 = (TextView)findViewById(R.id.textview2);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.BroadcastReceiver");
                intent.putExtra("key", "value");
                sendBroadcast(intent);
            }
        });

        TextView tv3 = (TextView)findViewById(R.id.textview3_1);
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService(new Intent(ServiceDemo.ACTION), conn, BIND_AUTO_CREATE);
            }
        });

        TextView tv4 = (TextView)findViewById(R.id.textview3_2);
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(ServiceDemo.ACTION));
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy unbindService");
        unbindService(conn);
        super.onDestroy();
    };

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
        }
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected");
        }
    };
}