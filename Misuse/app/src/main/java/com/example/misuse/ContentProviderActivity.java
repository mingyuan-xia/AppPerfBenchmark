package com.example.misuse;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ContentProviderActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contentprovider);

        Button insert = (Button)findViewById(R.id.ContentProviderInsert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentResolver().delete(NotePad.Notes.CONTENT_URI, null, null);

                ContentValues values = new ContentValues();
                values.put(NotePad.Notes.TITLE, "title1");
                values.put(NotePad.Notes.NOTE, "NOTENOTE1");
                getContentResolver().insert(NotePad.Notes.CONTENT_URI, values);

                values.clear();
                values.put(NotePad.Notes.TITLE, "title2");
                values.put(NotePad.Notes.NOTE, "NOTENOTE2");
                getContentResolver().insert(NotePad.Notes.CONTENT_URI, values);

            }
        });

        Button display = (Button)findViewById(R.id.ContentProviderDisplay);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNote();
            }
        });
    }

    private void displayNote(){
        String columns[] = new String[] {
                NotePad.Notes.TITLE,
                NotePad.Notes.NOTE,
                NotePad.Notes.CREATEDDATE,
                NotePad.Notes.MODIFIEDDATE};

        Uri myUri = NotePad.Notes.CONTENT_URI;
        Cursor cur = getContentResolver().query(myUri, columns, null, null, null);
        if (cur.moveToFirst()) {
            String title = null;
            String note = null;
            do {
                title = cur.getString(cur.getColumnIndex(NotePad.Notes.TITLE));
                note = cur.getString(cur.getColumnIndex(NotePad.Notes.NOTE));
                Toast toast = Toast.makeText(this, "TITLE:"+ title + "\t" + "NOTE:" + note, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 40);
                toast.show();
            } while (cur.moveToNext());
        }
    }
}
