package com.example.dsc;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class ShowDonation extends AppCompatActivity {

    TextView tId,rID,aID,amount,date;
    String TID,RID,AID,AMOUNT,DATE;
    TextView copy1,copy2,copy3;

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
        setContentView(R.layout.activity_show_donation);

        DATE=getIntent().getExtras().get("date").toString();
        RID=getIntent().getExtras().get("rID").toString();
        AID=getIntent().getExtras().get("aID").toString();
        TID=getIntent().getExtras().get("tID").toString();
        AMOUNT=getIntent().getExtras().get("payment").toString();


        Toolbar toolbar =findViewById(R.id.tool_result1);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Congratulations");
        toolbar.setTitleTextColor(Color.WHITE);


        tId=findViewById(R.id.txn_done);
        rID=findViewById(R.id.ref_done);
        aID=findViewById(R.id.app_done);
        amount=findViewById(R.id.txn_amount);
        date=findViewById(R.id.date_txn);
        copy1=findViewById(R.id.copy_1);
        copy2=findViewById(R.id.copy_2);
        copy3=findViewById(R.id.copy_3);

        tId.setText(TID);
        rID.setText(RID);
        aID.setText(AID);
        date.setText(DATE);
        amount.setText("₹"+AMOUNT);


        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);




        copy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText("text", tId.getText());
                manager.setPrimaryClip(clipData);
                Toast.makeText(ShowDonation.this, "Transaction ID Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText("text", aID.getText());
                manager.setPrimaryClip(clipData);
                Toast.makeText(ShowDonation.this, "Approval ID Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        copy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText("text", rID.getText());
                manager.setPrimaryClip(clipData);
                Toast.makeText(ShowDonation.this, "Reference ID Copied!", Toast.LENGTH_SHORT).show();
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
