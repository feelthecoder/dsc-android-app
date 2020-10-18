package com.example.dsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class dsc_splash extends AppCompatActivity {
    Handler handler;
    Boolean mBool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsc_splash);
        SharedPreferences mPrefs = getSharedPreferences("MyPref", 0);
        mBool = mPrefs.getBoolean("isit", true);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBool) {
                    SharedPreferences mPref = getSharedPreferences("MyPref", 0);
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putBoolean("isit", false);
                    editor.apply();
                    Intent intent = new Intent(dsc_splash.this, welcome_main.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent in = new Intent(dsc_splash.this, slide_layout_4.class);
                    startActivity(in);
                    finish();
                }
            }
        }, 3000);


    }

}