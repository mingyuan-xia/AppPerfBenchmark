package com.example.misuse;

import android.app.Activity;
import android.os.Bundle;

import java.io.*;

public class BufferedReaderWriterActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void write() throws IOException {
        FileOutputStream fileOutputStream = openFileOutput("Buffered.txt", 0);
        FileWriter fw = new FileWriter(fileOutputStream.getFD());
        BufferedWriter bufw = new BufferedWriter(fw);

        bufw.write("1 hello world !");
        bufw.newLine();
        bufw.write("2 hello world !");
        bufw.write("3 hello world !");
        bufw.flush();
        bufw.close();
    }

    void read(){

    }
}
