package com.example.dsc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class ShowFormActivity extends AppCompatActivity {

    TextView name,email,mobile,course,year,branch,college,training,cate,date,refer;
    String name_,email_,mobile_,course_,year_,branch_,college_,date_,eventName,category,code;
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
        setContentView(R.layout.activity_show_form);

        name=findViewById(R.id.fullnametxt);
        email=findViewById(R.id.emailtxt);
        mobile=findViewById(R.id.phonetxt);
        course=findViewById(R.id.coursetxt);
        year=findViewById(R.id.yeartxt);
        branch=findViewById(R.id.branchtxt);
        college=findViewById(R.id.collegetxt);
        training=findViewById(R.id.event_nametxt);
        cate=findViewById(R.id.eventtxt);
        date=findViewById(R.id.datetxt);
        refer=findViewById(R.id.refertxt);
        Toolbar toolbar=findViewById(R.id.tool_submitted);


        name_= getIntent().getExtras().get("name").toString();
        email_= getIntent().getExtras().get("email").toString();
        code=getIntent().getExtras().get("code").toString();
        mobile_=getIntent().getExtras().get("mobile").toString();
        course_= getIntent().getExtras().get("course").toString();
        year_= getIntent().getExtras().get("branch").toString();
        branch_=getIntent().getExtras().get("mobile").toString();
        college_= getIntent().getExtras().get("college").toString();
        date_= getIntent().getExtras().get("date").toString();
        mobile_=getIntent().getExtras().get("mobile").toString();
        eventName= getIntent().getExtras().get("eventName").toString();
        category= getIntent().getExtras().get("category").toString();

        name.setText(name_);
        email.setText(email_);
        mobile.setText(mobile_);
        course.setText(course_);
        year.setText(year_);

        branch.setText(branch_);
        college.setText(college_);
        training.setText(eventName);
        cate.setText(category);
        date.setText(date_);
        refer.setText(code);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }
}
