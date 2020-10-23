package com.example.dsc.LoginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.dsc.Main1Activity;
import com.example.dsc.R;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class login extends AppCompatActivity {
    private  FirebaseAuth mAuth;
    Button ttn,btn;
    private  SignInButton ltn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences mPrefs=getSharedPreferences("MyPrefs",0);
        String is=mPrefs.getString("mode","not");
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES||is.equals("dark")){
            setTheme(R.style.DarkTheme);

        }
        else
        {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_login);

        Window window = login.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(login.this, R.color.colorWhite));
        window.setNavigationBarColor(ContextCompat.getColor(login.this,R.color.colorWhite));
        btn=findViewById(R.id.bty5);
        ttn=findViewById(R.id.bty6);
        ltn=findViewById(R.id.bty1);
        mAuth=FirebaseAuth.getInstance();



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,Login_Email.class);
                startActivity(intent);
            }
        });
        ttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,Phone_Login_Main.class);
                startActivity(intent);
            }
        });

        ltn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,Google_Login_Activity.class);
               startActivity(intent);
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(this, Main1Activity.class));
            finish();
        }
    }
}