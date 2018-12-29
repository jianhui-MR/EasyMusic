package com.bobby.musiczone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bobby.musiczone.MainActivity;


/**
 * 闪屏Activity
 */
public class Splash_screenActivity extends AppCompatActivity {
    public static final String MainActivity_ACTION="com.bobby.main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(1000);
            Intent intent=new Intent(this, MainActivity.class);
            intent.setAction(MainActivity_ACTION);
            startActivity(intent);
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
