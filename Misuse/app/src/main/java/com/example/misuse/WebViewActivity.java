package com.example.misuse;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

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

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // OR, you can also load from an HTML string:
                String summary = "<html>" +
                        "<body>You scored <b>192</b> points.</body>" +
                        "<img src=\"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_ca79a146.png\"></img>" +
                        "</html>";
                webview.loadData(summary, "text/html", null);
                // ... although note that there are restrictions on what this HTML can do.
                // See the JavaDocs for loadData() and loadDataWithBaseURL() for more info.

            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webview.getSettings().setJavaScriptEnabled(false);
                webview.loadUrl(tv.getText().toString().trim());
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

                webview.loadUrl(tv.getText().toString().trim());
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.loadUrl(tv.getText().toString().trim());
            }
        });
    }
}
