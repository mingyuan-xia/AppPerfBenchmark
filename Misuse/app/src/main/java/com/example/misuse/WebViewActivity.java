package com.example.misuse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import static java.lang.Thread.sleep;

public class WebViewActivity extends Activity {
private WebView webview;
private EditText tv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webview = (WebView)findViewById(R.id.webview);
        tv = (EditText)findViewById(R.id.WebView_tv);
        Button enter = (Button)findViewById(R.id.WebView_enter);
        Button bt1 = (Button)findViewById(R.id.WebView_bt1);
        Button bt2 = (Button)findViewById(R.id.WebView_bt2);
        Button bt3 = (Button)findViewById(R.id.WebView_bt3);

        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("http://www.example.com/");

        getResources();

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data = "<html><body>" +
                        "<img src=\"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_ca79a146.png\"></img>" +
                        "</body></html><br>" +
                        "<input type=\"button\" value=\"Say hello\" onClick=\"showAndroidToast('Hello Android!')\" />\n" +
                        "\n" +
                        "<script type=\"text/javascript\">\n" +
                        "    function showAndroidToast(toast) {\n" +
                        "        Android.showToast(toast);\n" +
                        "    }\n" +
                        "</script>";

                webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        //sleep in main thread
                        try {
                            webview.loadUrl("http://www.sina.com/");
                            sleep(5);
                            webview.loadUrl("http://www.163.com/");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });

                webview.addJavascriptInterface(new WebAppInterface(WebViewActivity.this), "Android");

                webview.loadData(data, "text/html", null);
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webview.getSettings().setJavaScriptEnabled(false);
                webview.setWebViewClient(new WebViewClient());
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.getSettings().setJavaScriptEnabled(true);
                final Activity activity = WebViewActivity.this;
                webview.setWebViewClient(new WebViewClient() {
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.loadUrl(tv.getText().toString().trim());
            }
        });
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }
}
