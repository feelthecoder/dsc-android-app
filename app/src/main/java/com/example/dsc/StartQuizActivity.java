package com.example.dsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class StartQuizActivity extends AppCompatActivity {
    String database_name,category,link;
    Button start;
    ImageView imageView;
    DatabaseReference database;
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
        setContentView(R.layout.activity_start_quiz);


        Toolbar toolbar=findViewById(R.id.tool_start_quiz);
        start=findViewById(R.id.start_quiz_in);
        imageView=findViewById(R.id.quiz_image);

        category= getIntent().getExtras().get("category").toString();
        link= getIntent().getExtras().get("link").toString();
        database_name= getIntent().getExtras().get("qa_database").toString();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(category+" Quiz Challenge");


        database= FirebaseDatabase.getInstance().getReference("QuizData").child("QA");


        Glide.with(getApplicationContext()).load(link).override(200,200).into(imageView);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               database.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if(dataSnapshot.child(database_name).exists()){
                           CategoryActivity.fa.finish();
                           Intent i=new Intent(StartQuizActivity.this,QuizMainActivity.class);
                           i.putExtra("database",database_name);
                           i.putExtra("name",category);
                           startActivity(i);
                           finish();
                       }
                       else
                       {
                           Toast.makeText(StartQuizActivity.this, "This quiz is not available right now, try again after some time.", Toast.LENGTH_SHORT).show();
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
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
