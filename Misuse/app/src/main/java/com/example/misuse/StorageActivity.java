package com.example.misuse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;

public class StorageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        Button storage_write = (Button)findViewById(R.id.storage_write);
        storage_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    internal_storage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    void internal_storage() throws IOException {
        String FILENAME = "hello_file";
        String string = "hello world!";

        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
        fos.write(string.getBytes());
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
