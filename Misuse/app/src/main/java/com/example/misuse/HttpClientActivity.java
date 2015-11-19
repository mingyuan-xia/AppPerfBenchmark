package com.example.misuse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class HttpClientActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_httpclient);

        Button HttpClientGet = (Button)findViewById(R.id.HttpClientGet);

        HttpClientGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void get() throws IOException {
        BufferedReader in = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(URI.create("http://127.0.0.1:8000"));
        HttpResponse response = client.execute(request);
        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while((line = in.readLine()) != null) {
            sb.append(line + NL);
        }
        in.close();
        String page = sb.toString();
    }

}