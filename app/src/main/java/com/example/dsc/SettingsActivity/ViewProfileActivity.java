package com.example.dsc.SettingsActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class ViewProfileActivity extends AppCompatActivity {
    TextView tname,tmail,tusername,tskills,tdob,tmobile,tgender,tDP;
    CircularImageView pImage;
    DatabaseReference dRef,aRef;
    TextView edit;

    String name,mail,username,skills,dob,mobile,gender,DP,link;

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
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar=findViewById(R.id.tool_view_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Profile");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tname=findViewById(R.id.profile_name_view);
        tmail=findViewById(R.id.profile_email_view);
        tusername=findViewById(R.id.profile_username_view_a);
        tskills=findViewById(R.id.profile_skills_view_a);
        tdob=findViewById(R.id.profile_dob_view_a);
        tmobile=findViewById(R.id.profile_phone_view_a);
        tgender=findViewById(R.id.profile_gender_view_a);
        tDP=findViewById(R.id.profile_score_view_a);
        pImage=findViewById(R.id.profile_image_view);

        edit=findViewById(R.id.edit_profile_view);

        dRef= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        aRef=FirebaseDatabase.getInstance().getReference("Achievment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loadProfile();
    }

    private void loadProfile() {
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("profileImageUrl")){

                    name= Objects.requireNonNull(dataSnapshot.child("displayName").getValue()).toString();
                    username= Objects.requireNonNull(dataSnapshot.child("user").getValue()).toString();
                    mobile= Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString();
                    dob= Objects.requireNonNull(dataSnapshot.child("dob").getValue()).toString();
                    mail= Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                    gender= Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString();
                    link= Objects.requireNonNull(dataSnapshot.child("profileImageUrl").getValue()).toString();

                    tname.setText(name);
                    tmail.setText(mail);
                    tusername.setText(username);
                    tdob.setText(dob);
                    tgender.setText(gender);
                    tmobile.setText(mobile);
                    Glide.with(ViewProfileActivity.this).load(link).into(pImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        aRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("DP")) {
                    skills = dataSnapshot.child("skills").getValue().toString();
                    DP = dataSnapshot.child("DP").getValue().toString();
                    tDP.setText(DP + " Developer Points");
                    tskills.setText(skills);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewProfileActivity.this,EditProfileActivity.class);
                startActivity(intent);
            }
        });


    }
}
