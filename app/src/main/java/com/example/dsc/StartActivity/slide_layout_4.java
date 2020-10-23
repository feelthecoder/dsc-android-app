package com.example.dsc.StartActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.dsc.LoginActivity.login;
import com.example.dsc.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class slide_layout_4 extends AppCompatActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_layout_4);

        Window window = slide_layout_4.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(slide_layout_4.this, R.color.color_welcome_2));
        window.setNavigationBarColor(ContextCompat.getColor(slide_layout_4.this,R.color.color_welcome_2));


        handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(slide_layout_4.this, login.class);
                startActivity(intent);
                finish();
            }
        },1000);


    }

}


