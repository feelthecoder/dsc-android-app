package com.example.dsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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
            startActivity(new Intent(this,Main1Activity.class));
            finish();
        }
    }
}