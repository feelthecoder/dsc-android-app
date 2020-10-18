package com.example.dsc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ClaimActivity extends AppCompatActivity {

    EditText hostel_name,room_no,complete_address,pin_code;
    TextView note;
    Button deliver_to_me;
    CheckBox if_hostel;
    String hostel,room,address,pincode;
    DatabaseReference dRef;
    DatabaseReference user;
    String name;
    ProgressBar sur;


    private static final String CHANNEL_ID="CLAIM";
    private static final String CHANNEL_NAME="Leaderboard Prize";
    private static final String CHANNEL_DESC="This Channel shows notifications about prize claim.";


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
        setContentView(R.layout.activity_claim);


        Toolbar toolbar=findViewById(R.id.tool_claim);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Claim Surprise");


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        hostel_name=findViewById(R.id.hostel_name);
        room_no=findViewById(R.id.room_no);
        complete_address=findViewById(R.id.complete_address);
        pin_code=findViewById(R.id.pin_code);
        if_hostel=findViewById(R.id.chk_bx);
        note=findViewById(R.id.note_del);
        sur=findViewById(R.id.sur_pro);
        deliver_to_me=findViewById(R.id.btn_surprise);
        dRef= FirebaseDatabase.getInstance().getReference("LeaderboardWinners");
        user=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("displayName")){
                    name=dataSnapshot.child("displayName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if_hostel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    hostel_name.setVisibility(View.VISIBLE);
                    room_no.setVisibility(View.VISIBLE);
                    complete_address.setVisibility(View.INVISIBLE);
                    pin_code.setVisibility(View.INVISIBLE);
                    note.setVisibility(View.INVISIBLE);
                }
                else
                {
                    hostel_name.setVisibility(View.INVISIBLE);
                    room_no.setVisibility(View.INVISIBLE);
                    complete_address.setVisibility(View.VISIBLE);
                    pin_code.setVisibility(View.VISIBLE);
                    note.setVisibility(View.VISIBLE);

                }
            }
        });
        deliver_to_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(if_hostel.isChecked()){
                    hostel_name.setVisibility(View.VISIBLE);
                    room_no.setVisibility(View.VISIBLE);
                    complete_address.setVisibility(View.INVISIBLE);
                    pin_code.setVisibility(View.INVISIBLE);
                    note.setVisibility(View.INVISIBLE);

                    addRECWinnersToFirebase();

                }
                if(!if_hostel.isChecked()){
                    hostel_name.setVisibility(View.INVISIBLE);
                    room_no.setVisibility(View.INVISIBLE);
                    complete_address.setVisibility(View.VISIBLE);
                    pin_code.setVisibility(View.VISIBLE);
                    note.setVisibility(View.VISIBLE);
                    addOtherCollegeWinnersToFirebase();

                }
            }
        });



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void addOtherCollegeWinnersToFirebase() {
        address=complete_address.getText().toString();
        pincode=pin_code.getText().toString();

        if(address.isEmpty()){
            complete_address.setError("Address can't be empty!");
            complete_address.requestFocus();
            return;
        }
        if(pincode.isEmpty()){
            pin_code.setError("Pin Code can't be empty!");
            pin_code.requestFocus();
            return;
        }
        sur.setVisibility(View.VISIBLE);


        DatabaseReference instant=dRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        instant.child("name").setValue(name);
        instant.child("address").setValue(address);
        instant.child("pinroom").setValue(pincode);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sur.setVisibility(View.INVISIBLE);
                complete_address.getText().clear();
                pin_code.getText().clear();
                displayNotification(name,address,pincode);
            }
        }, 2000);
        Toast.makeText(this, "Your Surprise will be delivered to you soon.", Toast.LENGTH_SHORT).show();
    }

    private void addRECWinnersToFirebase() {
        hostel=hostel_name.getText().toString();
        room=room_no.getText().toString();

        if(hostel.isEmpty()){
            hostel_name.setError("Hostel Name can't be empty!");
            hostel_name.requestFocus();
            return;
        }
        if(room.isEmpty()){
            room_no.setError("Room Number can't be empty!");
            room_no.requestFocus();
            return;
        }
        sur.setVisibility(View.VISIBLE);

        DatabaseReference instant=dRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        instant.child("name").setValue(name);
        instant.child("address").setValue(hostel);
        instant.child("pinroom").setValue(room);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sur.setVisibility(View.INVISIBLE);
                room_no.getText().clear();
                hostel_name.getText().clear();
                displayNotification(name,address,pincode);
            }
        }, 2000);
        Toast.makeText(this, "Your Surprise will be delivered to you soon.", Toast.LENGTH_SHORT).show();

    }

    private void displayNotification(String name, String address, String pincode) {

        Intent intent= new Intent(getApplicationContext(),NotificationActivity.class);
        intent.putExtra("title","Surprise Claim");
        intent.putExtra("message","Dear "+name+" your claim is approved for your address "+address+" "+pincode+" We will deliver it soon.");
        intent.putExtra("activity","ClaimActivity.class");


        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("Surprise Claim")
                .setContentText("Dear "+name+" your claim is approved for your address "+address+" "+pincode+" We will deliver it soon.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(1,mBuilder.build());
    }

}
