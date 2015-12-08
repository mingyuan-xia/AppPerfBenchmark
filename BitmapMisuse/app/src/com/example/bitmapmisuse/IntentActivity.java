package com.example.bitmapmisuse;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
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
import java.util.Stack;

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
    private Stack<MyServiceConnection> conns = new Stack<MyServiceConnection>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);
        workerThread = new HandlerThread("Receiver");
        workerThread.start();

        // activity tests

        findViewById(R.id.btn_start_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntentActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_start_activity_for_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntentActivity.this, SecondActivity.class);
                startActivityForResult(intent, 0x76);
            }
        });

        findViewById(R.id.btn_start_activities).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent[] intents = new Intent[2];
                intents[0] = new Intent(IntentActivity.this, SecondActivity.class);
                intents[1] = new Intent(IntentActivity.this, ThirdActivity.class);
                startActivities(intents);
            }
        });

        findViewById(R.id.btn_start_pick_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0x77);
            }
        });

        // broadcast tests

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
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                        LocalBroadcastManager.getInstance(IntentActivity.this).sendBroadcastSync(
                                new Intent("com.example.bitmapmisuse.LOCAL_BROADCAST_SYNC"));
                    }
                }).start();
            }
        });

        // service tests

        findViewById(R.id.btn_start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(DemoService.ACTION));
            }
        });

        findViewById(R.id.btn_stop_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(DemoService.ACTION));
            }
        });

        findViewById(R.id.btn_bind_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyServiceConnection conn = new MyServiceConnection();
                conns.push(conn);
                bindService(new Intent(DemoService.ACTION), conn, BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.btn_unbind_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!conns.empty()) {
                    MyServiceConnection conn = conns.pop();
                    unbindService(conn);
                }
            }
        });

        findViewById(R.id.btn_start_polling_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "start polling service");
                PollingUtils.startPollingService(IntentActivity.this, 1,
                        PolledService.class, PolledService.ACTION);
            }
        });

        findViewById(R.id.btn_stop_polling_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "stop polling service");
                PollingUtils.stopPollingService(IntentActivity.this,
                        PolledService.class, PolledService.ACTION);
            }
        });

        // PendingIntent and IntentSender tests

        findViewById(R.id.btn_start_activity_intent_sender)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(IntentActivity.this, SecondActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(IntentActivity.this,
                                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        IntentSender sender = pendingIntent.getIntentSender();
                        try {
                            pendingIntent.send(IntentActivity.this, 0, null);
                            Thread.sleep(100);
                            IntentActivity.this.startIntentSender(sender, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                        } catch (PendingIntent.CanceledException e) {
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_start_activity_for_result_intent_sender)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(IntentActivity.this, SecondActivity.class);
                        IntentSender sender = PendingIntent.getActivity(IntentActivity.this, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT).getIntentSender();
                        try {
                            IntentActivity.this.startIntentSenderForResult(sender, 0x76, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                        }
                    }
                });

        findViewById(R.id.btn_start_service_intent_sender)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DemoService.ACTION);
                        PendingIntent pendingIntent = PendingIntent.getService(IntentActivity.this,
                                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        IntentSender sender = pendingIntent.getIntentSender();
                        try {
                            pendingIntent.send(1);
                            Thread.sleep(100);
                            IntentActivity.this.startIntentSender(sender, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                        } catch (PendingIntent.CanceledException e) {
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_broadcast_to_local_intent_sender)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LocalReceiver.REGULAR);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(IntentActivity.this,
                                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        IntentSender sender = pendingIntent.getIntentSender();
                        try {
                            pendingIntent.send();
                            Thread.sleep(100);
                            IntentActivity.this.startIntentSender(sender, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                        } catch (PendingIntent.CanceledException e) {
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_ordered_broadcast_intent_sender)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LocalReceiver.ORDERED);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(IntentActivity.this,
                                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        IntentSender sender = pendingIntent.getIntentSender();
                        try {
                            pendingIntent.send(IntentActivity.this, 0, null,
                                    new PendingIntent.OnFinished() {
                                @Override
                                public void onSendFinished(PendingIntent pendingIntent,
                                                           Intent intent, int resultCode,
                                                           String resultData, Bundle resultExtras) {
                                    Log.v(TAG, "PendingIntent.OnFinished running on worker thread, " +
                                            "result code: " + Integer.toString(resultCode));
                                }
                            }, new Handler(workerThread.getLooper()), Manifest.permission.CAMERA);
                            Thread.sleep(100);
                            sender.sendIntent(IntentActivity.this, 0, null,
                                    new IntentSender.OnFinished() {
                                @Override
                                public void onSendFinished(IntentSender IntentSender, Intent intent,
                                                           int resultCode, String resultData,
                                                           Bundle resultExtras) {
                                    Log.v(TAG, "IntentSender.OnFinished running on worker thread, " +
                                            "result code: " + Integer.toString(resultCode));
                                }
                            }, new Handler(workerThread.getLooper()), Manifest.permission.CAMERA);
                        } catch (IntentSender.SendIntentException e) {
                        } catch (PendingIntent.CanceledException e) {
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("com.example.bitmapmisuse.LOCAL_BROADCAST"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverBlock,
                new IntentFilter("com.example.bitmapmisuse.LOCAL_BROADCAST_SYNC"));
    }

    @Override
    protected void onDestroy() {
        for (BroadcastReceiver receiver : receivers)
            unregisterReceiver(receiver);
        removeStickyBroadcast(new Intent(LocalReceiver.REGULAR));
        removeStickyBroadcast(new Intent(LocalReceiver.ORDERED));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverBlock);
        super.onDestroy();
    }

    class MyServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
        }
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0x76: {
                if (resultCode == 0x77) {
                    Log.v(TAG, "result from second activity");
                }
            }
            case 0x77: {
                if (resultCode == RESULT_OK) {
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        Log.v(TAG, photoUri.toString());
                    }
                }
            }
        }
    }
}