package com.example.misuse;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SharedPreferencesActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedpreferences);
        Button sharedPreferences_put = (Button)findViewById(R.id.SharedPreferences_put);
        Button sharedPreferences_get = (Button)findViewById(R.id.SharedPreferences_get);
        Button sharedPreferences_rm = (Button)findViewById(R.id.SharedPreferences_rm);

        sharedPreferences_put.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("name",5);
                editor.putString("URL","baidu.com");
                editor.commit();
            }
        });

        sharedPreferences_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = settings.edit();
                Integer name = settings.getInt("name", 0);
                String url = settings.getString("URL","default");
                editor.commit();
            }
        });

        sharedPreferences_rm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("name");
                editor.commit();
            }
        });
    }

}
