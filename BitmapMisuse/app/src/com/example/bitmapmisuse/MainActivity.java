package com.example.bitmapmisuse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * We have only one activity, so we'll never leak memory.
 */
@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {
    private static final String TAG = "BITMAP_MISUSE";
    private static final int NUM = 5;
   
    private String timings = "";
    private byte[] imgData;
    private File imgFile;
    private ImageView[] ivs;
    private volatile static int c = 0;
    
    private volatile Bitmap dataBmp = null;
    private volatile Bitmap fileBmp = null;
    private volatile Bitmap resourceBmp = null;
    private volatile Bitmap streamBmp = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepare(this, this);

        ImageView preDefIv = (ImageView) findViewById(R.id.myImageView);
        long startTime0 = System.currentTimeMillis();
        preDefIv.setImageResource(R.drawable.mypic);
        long difference0 = System.currentTimeMillis() - startTime0;
        String s0 = "preDefIv.setImageResource() takes " + difference0 + " ms";
        timings += s0 + "\n";
        Log.d(TAG, s0);

        ivs = new ImageView[NUM];
        for (int i = 0; i < NUM; ++i) {
            ivs[i] = new ImageView(this);
        }
        decodeDataThread.start();
        decodeFileThread.start();
        decodeResourceThread.start();
        decodeStreamThread.start();

    }

    private void prepare(MainActivity m, MainActivity n) {
        AssetManager assetManager = getAssets();
        try {
            InputStream fis = assetManager.open("fromfile.jpg");
            imgData = new byte[fis.available()];
            fis.read(imgData);
            fis.close();
            imgFile = new File(getFilesDir(), "image.jpg");
            if (!imgFile.exists()) {
                FileOutputStream fos = new FileOutputStream(imgFile);
                fos.write(imgData);
                fos.close();
            }
        } catch (IOException e) {
            return;
        }
    }

    private void finishTask() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.myLayout);
        long startTime = System.currentTimeMillis();
        ivs[4].setImageResource(R.drawable.mypic);
        long difference = System.currentTimeMillis() - startTime;
        String s = "setImageResource() takes " + difference + " ms";
        timings += s + "\n";
        Log.d(TAG, s);

        TextView msg = new TextView(this);
        msg.setText(timings);
        ll.addView(msg);
        for (ImageView iv : ivs) {
            ll.addView(iv);
        }
    }

    Handler myHandler1_impl = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0x01:
                ivs[0].setImageBitmap(dataBmp);
                c++;
                break;
            case 0x02:
                ivs[1].setImageBitmap(fileBmp);
                c++;
                break;
            case 0x03:
                ivs[2].setImageBitmap(resourceBmp);
                c++;
                break;
            case 0x04:
                ivs[3].setImageBitmap(streamBmp);
                c++;
                break;
            default:
                break;
            }
            if (c == 4) {
                Message message = new Message();
                message.what = 0x77;
                myHandler2.sendMessage(message);
            }
        }
    };

    Handler myHandler1_callback = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    ivs[0].setImageBitmap(dataBmp);
                    c++;
                    break;
                case 0x02:
                    ivs[1].setImageBitmap(fileBmp);
                    c++;
                    break;
                case 0x03:
                    ivs[2].setImageBitmap(resourceBmp);
                    c++;
                    break;
                case 0x04:
                    ivs[3].setImageBitmap(streamBmp);
                    c++;
                    break;
                default:
                    break;
            }
            if (c == 4) {
                Message message = new Message();
                message.what = 0x77;
                myHandler2.sendMessage(message);
            }
            return true;
        }
    });

    Handler myHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0x77:
                finishTask();
                break;
            default:
                break;
            }
        }
    };
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // do nothing
        }
    }

    MyHandler myHandler3 = new MyHandler();

    Thread decodeDataThread = new Thread(new Runnable() {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            Bitmap bm = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            long difference = System.currentTimeMillis() - startTime;
            String s = "myDecodeData() takes " + difference + " ms";
            timings += s + "\n";
            dataBmp = bm;
            Message message = new Message();
            message.what = 0x01;
            myHandler1_impl.sendMessage(message);
        }
    });

    Thread decodeFileThread = new Thread(new Runnable() {

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            long difference = System.currentTimeMillis() - startTime;
            String s = "myDecodeFile() takes " + difference + " ms";
            timings += s + "\n";
            fileBmp = bm;
            Message message = new Message();
            message.what = 0x02;
            myHandler1_callback.sendMessage(message);
        }
    });

    Thread decodeResourceThread = new Thread(new Runnable() {

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            Resources res = getResources();
            int id = R.drawable.mypic;
            Bitmap bm = BitmapFactory.decodeResource(res, id);
            long difference = System.currentTimeMillis() - startTime;
            String s = "myDecodeResource() takes " + difference + " ms";
            timings += s + "\n";
            resourceBmp = bm;
            Message message = new Message();
            message.what = 0x03;
            myHandler1_impl.sendMessage(message);
        }
    });

    Thread decodeStreamThread = new Thread(new Runnable() {

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            AssetManager assetManager = getAssets();
            try {
                InputStream fis = assetManager.open("fromfile.jpg");
                Bitmap b = BitmapFactory.decodeStream(fis);
                fis.close();
                long difference = System.currentTimeMillis() - startTime;
                String s = "myDecodeStream() takes " + difference + " ms";
                timings += s + "\n";
                streamBmp = b;
                Message message = Message.obtain(myHandler1_impl, 0x04);
                message.sendToTarget();

                // Test asyncEntries
                Thread.sleep(1000);
                Message msg = Message.obtain(myHandler1_impl, new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TEST", "Message.callback");
                    }
                });
                msg.sendToTarget();

                Thread.sleep(500);
                myHandler3.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                Thread.sleep(500);
                myHandler3.postAtFrontOfQueue(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                Thread.sleep(500);
                class MyRunnable implements Runnable {
                    @Override
                    public void run() {

                    }
                }
                myHandler3.postAtTime(new MyRunnable(), SystemClock.uptimeMillis() + 500);
                Thread.sleep(500);
                myHandler3.postAtTime(new MyRunnable() {
                    @Override
                    public void run() {

                    }
                }, "tag", SystemClock.uptimeMillis() + 500);
                Thread.sleep(500);
                myHandler3.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 500);
                Thread.sleep(500);
                myHandler3.sendEmptyMessage(1);
                Thread.sleep(500);
                myHandler3.sendEmptyMessageDelayed(2, 500);
                Thread.sleep(500);
                myHandler3.sendEmptyMessageAtTime(3, SystemClock.uptimeMillis() + 500);
                Thread.sleep(500);
                myHandler3.sendMessage(Message.obtain());
                Thread.sleep(500);
                myHandler3.sendMessageAtFrontOfQueue(Message.obtain());
                Thread.sleep(500);
                myHandler3.sendMessageAtTime(Message.obtain(), SystemClock.uptimeMillis() + 500);
                Thread.sleep(500);
                myHandler3.sendMessageDelayed(Message.obtain(), 500);
            } catch (IOException e) {
            } catch (InterruptedException e) {
            }
        }
    });
}
