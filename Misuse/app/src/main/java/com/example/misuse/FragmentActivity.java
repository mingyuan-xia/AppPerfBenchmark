package com.example.misuse;

import android.app.*;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FragmentActivity extends Activity{
    private FirstFragment fragment1 = null;
    private SecondFragment fragment2 = null;
    private int current_label = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        setDefaultFragment();

        TextView tv1 = (TextView)findViewById(R.id.fragment_one);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                if (current_label != 1){
                    if (fragment1 == null)
                        fragment1 = new FirstFragment();
                    transaction.replace(R.id.id_content, fragment1);
                    current_label = 1;
                    transaction.commit();
                }
            }
        });

        TextView tv2 = (TextView)findViewById(R.id.fragment_two);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                if (current_label != 2){
                    if (fragment2 == null)
                        fragment2 = new SecondFragment();
                    transaction.replace(R.id.id_content, fragment2);
                    current_label = 2;
                    transaction.commit();
                }
            }
        });

    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        current_label = 1;
        fragment1 = new FirstFragment();
        transaction.replace(R.id.id_content, fragment1);
        transaction.commit();
    }
}
