package com.example.misuse;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SQLiteActivity extends Activity {
    private Button createButton;
    private Button insertButton;
    private Button updateButton;
    private Button updateRecordButton;
    private Button queryButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        createButton = (Button)findViewById(R.id.createDatabase);
        updateButton = (Button)findViewById(R.id.updateDatabase);
        insertButton = (Button)findViewById(R.id.insert);
        updateRecordButton = (Button)findViewById(R.id.update);
        queryButton = (Button)findViewById(R.id.query);
        createButton.setOnClickListener(new CreateListener());
        updateButton.setOnClickListener(new UpdateListener());
        insertButton.setOnClickListener(new InsertListener());
        updateRecordButton.setOnClickListener(new UpdateRecordListener());
        queryButton.setOnClickListener(new QueryListener());
    }
    class CreateListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            DatabaseHelper dbHelper = new DatabaseHelper(SQLiteActivity.this,"test_mars_db");
            SQLiteDatabase db = dbHelper.getReadableDatabase();
        }
    }
    class UpdateListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            DatabaseHelper dbHelper = new DatabaseHelper(SQLiteActivity.this,"test_mars_db",2);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
        }

    }

    /**
     *
     *   public long insert(String table, String nullColumnHack, ContentValues values)
     */
    class InsertListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("name","zhangsan");
            DatabaseHelper dbHelper = new DatabaseHelper(SQLiteActivity.this,"test_mars_db",2);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.insert("user", null, values);
            Toast.makeText(SQLiteActivity.this, "insert", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     *
     *   public int update(String table, ContentValues values, String whereClause, String[] whereArgs)
     */
    class UpdateRecordListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            DatabaseHelper dbHelper = new DatabaseHelper(SQLiteActivity.this,"test_mars_db");
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", "zhangsanfeng");
            db.update("user", values, "id=?", new String[]{"1"});
        }
    }

    /**
     *
     *    public Cursor query(String table, String[] columns, String selection,
     *    String[] selectionArgs, String groupBy, String having,
     *    String orderBy)
     */
    class QueryListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            System.out.println("aaa------------------");
            DatabaseHelper dbHelper = new DatabaseHelper(SQLiteActivity.this,"test_mars_db");
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("user", new String[]{"id","name"}, "id=?", new String[]{"1"}, null, null, null);
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex("name"));
                System.out.println("query--->" + name);
            }
            Toast.makeText(SQLiteActivity.this, "query", Toast.LENGTH_SHORT).show();
        }
    }

}
