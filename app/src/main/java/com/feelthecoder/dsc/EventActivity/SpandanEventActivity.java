package com.feelthecoder.dsc.EventActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feelthecoder.dsc.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class SpandanEventActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_spandan_event);
        Toolbar toolbar=findViewById(R.id.tool_spandan_id);
        register=findViewById(R.id.s_spandan_button);
        img=findViewById(R.id.s_image_detail);
        head=findViewById(R.id.s_dsc_name);
        Date=findViewById(R.id.s_spandan_date);
        Short=findViewById(R.id.s_spandan_short);
        Long=findViewById(R.id.s_spandan_description);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        title=getIntent().getExtras().get("title").toString();
        image=getIntent().getExtras().get("image").toString();
        shortd=getIntent().getExtras().get("short").toString();
        date=getIntent().getExtras().get("date").toString();
        description=getIntent().getExtras().get("description").toString();
        getSupportActionBar().setTitle(title);
        Glide.with(getApplicationContext()).load(image).into(img);
        head.setText(title);
        Date.setText(date);
        Short.setText(shortd);
        Long.setText(description);

        getSupportActionBar().setTitle(title);





        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SpandanEventActivity.this,SpandanRegistrationActivity.class);
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
