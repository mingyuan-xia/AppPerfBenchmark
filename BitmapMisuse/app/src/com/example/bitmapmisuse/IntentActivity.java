package com.example.bitmapmisuse;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class IntentActivity extends Activity {
    private static final String TAG = "IntentActivity";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "dynamic registered receiver (through LocalBroadcastManager)");
        }
    };

    private BroadcastReceiver receiverBlock = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "dynamic registered long-time running receiver (through LocalBroadcastManager)");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);

        TextView tv1 = (TextView)findViewById(R.id.textview1);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntentActivity.this, HelloActivity.class);
                startActivity(intent);
            }
        });

        TextView tvBroadcastToLocal = (TextView)findViewById(R.id.tv_broadcast_tolocal);
        tvBroadcastToLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LocalReceiver.ACTION);
                        sendBroadcast(intent);
                    }
                }).start();
            }
        });

        TextView tvBroadcastToRemote = (TextView)findViewById(R.id.tv_broadcast_toremote);
        tvBroadcastToRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(RemoteReceiver.ACTION);
                        sendBroadcast(intent);
                    }
                }).start();
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

        TextView tv5 = (TextView)findViewById(R.id.textview5);
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0x77);
            }
        });

        TextView tv6 = (TextView)findViewById(R.id.textview6_1);
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "startPollingService");
                PollingUtils.startPollingService(IntentActivity.this, 5, PollingService.class, PollingService.ACTION);
            }
        });

        TextView tv7 = (TextView)findViewById(R.id.textview6_2);
        tv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "stopPollingService");
                PollingUtils.stopPollingService(IntentActivity.this, PollingService.class, PollingService.ACTION);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("com.example.bitmapmisuse.LOCAL_BROADCAST"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverBlock,
                new IntentFilter("com.example.bitmapmisuse.LOCAL_BROADCAST_SYNC"));
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy unbindService");
        unbindService(conn);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverBlock);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0x77:
            {
                if (resultCode == RESULT_OK)
                {
                    Uri photoUri = data.getData();
                    if (photoUri != null)
                    {
                        Log.v(TAG, photoUri.toString());
                    }
                }
            }
        }
    }
}