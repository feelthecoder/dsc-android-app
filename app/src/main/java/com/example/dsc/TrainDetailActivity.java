package com.example.dsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class TrainDetailActivity extends AppCompatActivity {

    String title,image,description,date,shortd;
    Button register;
    ImageView img;
    TextView head,Date,Short,Long;

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
        setContentView(R.layout.activity_train_detail);
        Toolbar toolbar=findViewById(R.id.tool_train_id);
        register=findViewById(R.id.w_work_button);
        img=findViewById(R.id.t_image_detail);
        head=findViewById(R.id.t_dsc_name);
        Date=findViewById(R.id.t_train_date);
        Short=findViewById(R.id.t_train_short);
        Long=findViewById(R.id.w_workshop_description);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        title=getIntent().getExtras().get("title").toString();
        image=getIntent().getExtras().get("image").toString();
        shortd=getIntent().getExtras().get("short").toString();
        date=getIntent().getExtras().get("date").toString();
        description=getIntent().getExtras().get("description").toString();

        Glide.with(getApplicationContext()).load(image).into(img);
        head.setText(title);
        Date.setText(date);
        Short.setText(shortd);
        Long.setText(description);

        getSupportActionBar().setTitle(title);





        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TrainDetailActivity.this,TRegistrationActivity.class);
                intent.putExtra("title",title);
                intent.putExtra("event","Training");
                intent.putExtra("date",date);
                startActivity(intent);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
