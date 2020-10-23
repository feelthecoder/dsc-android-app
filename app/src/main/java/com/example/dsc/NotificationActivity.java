package com.example.dsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsc.Adapters.NotificationAdapter;
import com.example.dsc.Model.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationActivity extends AppCompatActivity {

    String title=null;
    String msg=null;
    Class<?> activity=null;

    RelativeLayout not;

    TextView nTitle,nMessage,clear;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;

    ArrayList<NotificationModel> infoList= new ArrayList<>();

    NotificationAdapter notificationAdapter;
    String info;

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
        setContentView(R.layout.activity_notification);

        Toolbar toolbar=findViewById(R.id.tool_note);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notifications");

        not=findViewById(R.id.rel_not);
        nTitle=findViewById(R.id.title_not);
        nMessage=findViewById(R.id.des_not);
        recyclerView=findViewById(R.id.recycler_notifications);
        clear=findViewById(R.id.clear_not);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        title= Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("title")).toString();
        msg= Objects.requireNonNull(getIntent().getExtras().get("message")).toString();
        activity= Objects.requireNonNull(getIntent().getExtras().get("activity")).getClass();

        if(title.equals("x") && msg.equals("x")){
            not.setVisibility(View.GONE);
        }
        else
        {
            not.setVisibility(View.VISIBLE);
            nTitle.setText(title);
            nMessage.setText(msg);
            not.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NotificationActivity.this, activity);
                    startActivity(intent);
                }
            });
        }




        //add notification to SharedPreferences as a oject of NotificationModel using JSON and GSON.
        sharedPreferences = getSharedPreferences("NOTIFICATIONS", MODE_PRIVATE);

        if(sharedPreferences.contains("list")){
            Gson gson = new Gson();
            sharedPreferences = getSharedPreferences("NOTIFICATIONS", MODE_PRIVATE);
            String userInfo = sharedPreferences.getString("list", "");


            Type listType = new TypeToken<ArrayList<NotificationModel>>(){}.getType();
            ArrayList<NotificationModel> data = gson.fromJson(userInfo, listType);
            String info=gson.toJson(data);
            if(!title.equals("x") && !msg.equals("x")) {
                NotificationModel model = new NotificationModel(title, msg, activity.toString());
                assert data != null;
                data.add(model);
                info = gson.toJson(data);
            }


            sharedPreferences = getSharedPreferences("NOTIFICATIONS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("list", info);
            editor.commit();



            notificationAdapter= new NotificationAdapter(NotificationActivity.this,data);
            recyclerView.setAdapter(notificationAdapter);
        }
        else{
            if(!title.equals("x") && !msg.equals("x")) {
                NotificationModel model = new NotificationModel(title, msg, activity.toString());
                infoList.add(model);
            }

            Gson gson = new Gson();
            String info = gson.toJson(infoList);

            sharedPreferences = getSharedPreferences("NOTIFICATIONS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("list", info);
            editor.commit();

            notificationAdapter= new NotificationAdapter(NotificationActivity.this,infoList);
            recyclerView.setAdapter(notificationAdapter);


        }


        //clear all notifications from SharedPreferences and Refresh the recycler view
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().clear().commit();
                Toast.makeText(NotificationActivity.this, "Old Notifications are cleared.", Toast.LENGTH_SHORT).show();
                recyclerView.setVisibility(View.INVISIBLE);
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
