package com.example.bitmapmisuse;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

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

    private BroadcastReceiver receiverWorkerThread = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "receiver running on worker thread, result code: " +
                    Integer.toString(getResultCode()));
        }
    };

    private HandlerThread workerThread;
    private ArrayList<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);
        workerThread = new HandlerThread("Receiver");
        workerThread.start();

        findViewById(R.id.btn_start_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntentActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_broadcast_to_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent intent = new Intent(LocalReceiver.REGULAR);
                            sendBroadcast(intent);
                            Thread.sleep(100);
                            sendBroadcast(intent, Manifest.permission.CAMERA);
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_broadcast_to_remote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent intent = new Intent(RemoteReceiver.REGULAR);
                            sendBroadcast(intent);
                            Thread.sleep(100);
                            sendBroadcast(intent, Manifest.permission.CAMERA);
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_ordered_broadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent intent = new Intent(LocalReceiver.ORDERED);
                            sendOrderedBroadcast(intent, Manifest.permission.CAMERA);
                            Thread.sleep(100);
                            sendOrderedBroadcast(intent, Manifest.permission.CAMERA,
                                    receiverWorkerThread, new Handler(workerThread.getLooper()),
                                    0, null, null);
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_sticky_broadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent intent = new Intent(LocalReceiver.REGULAR);
                            sendStickyBroadcast(intent);
                            Thread.sleep(100);
                            intent.setAction(LocalReceiver.ORDERED);
                            sendStickyOrderedBroadcast(intent, receiverWorkerThread,
                                    new Handler(workerThread.getLooper()),
                                    0, null, null);
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_register_receiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BroadcastReceiver receiver1 = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.v(TAG, "dynamic registered receiver");
                    }
                };
                BroadcastReceiver receiver2 = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.v(TAG, "dynamic registered receiver on workerThread");
                    }
                };
                receivers.add(receiver1);
                receivers.add(receiver2);
                IntentFilter filter = new IntentFilter(LocalReceiver.ORDERED);
                filter.addAction(LocalReceiver.REGULAR);
                registerReceiver(receiver1, filter);
                registerReceiver(receiver2, filter, null, new Handler(workerThread.getLooper()));
            }
        });

        findViewById(R.id.btn_local_broadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager.getInstance(IntentActivity.this).
                        sendBroadcast(new Intent("com.example.bitmapmisuse.LOCAL_BROADCAST"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LocalBroadcastManager.getInstance(IntentActivity.this).sendBroadcastSync(
                                new Intent("com.example.bitmapmisuse.LOCAL_BROADCAST_SYNC"));
                    }
                }).start();
            }
        });

        findViewById(R.id.btn3_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService(new Intent(ServiceDemo.ACTION), conn, BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.btn3_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(ServiceDemo.ACTION));
            }
        });

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0x77);
            }
        });

        findViewById(R.id.btn6_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "startPollingService");
                PollingUtils.startPollingService(IntentActivity.this, 5, PollingService.class, PollingService.ACTION);
            }
        });

        findViewById(R.id.btn6_2).setOnClickListener(new View.OnClickListener() {
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
        //Log.v(TAG, "onDestroy unbindService");
        //unbindService(conn);
        for (BroadcastReceiver receiver : receivers)
            unregisterReceiver(receiver);
        removeStickyBroadcast(new Intent(LocalReceiver.REGULAR));
        removeStickyBroadcast(new Intent(LocalReceiver.ORDERED));
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