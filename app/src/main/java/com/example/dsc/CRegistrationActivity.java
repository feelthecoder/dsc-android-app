package com.example.dsc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsc.Model.RegistrationForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class CRegistrationActivity extends AppCompatActivity {

    String tName, category;
    EditText name, email, mobile, course, year, branch, college, refer;
    TextView training, cate;
    String name_, email_, mobile_, course_, year_, branch_, college_, date_, code_;
    Button submit;
    ProgressBar progressBar;
    String DATE;
    DatabaseReference dRef, hRef;
    String checkIfAlreadyFilled;
    Date d1, d2;
    int dp;

    private static final String CHANNEL_ID="COMPETE";
    private static final String CHANNEL_NAME="Competition";
    private static final String CHANNEL_DESC="This Channel shows notifications of registered competitions.";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences mPrefs = getSharedPreferences("MyPrefs", 0);
        String is = mPrefs.getString("mode", "not");
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || is.equals("dark")) {
            setTheme(R.style.DarkTheme);

        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_c);
        Toolbar toolbar = findViewById(R.id.tool_register_c);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Date date = Calendar.getInstance().getTime();

        DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        checkIfAlreadyFilled = formatter.format(date);

        //Creating Notification Channel for below oreo versions
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //find ID'd of different View
        progressBar = findViewById(R.id.c_progress);
        submit = findViewById(R.id.id_button_c);
        name = findViewById(R.id.fullname_c);
        email = findViewById(R.id.email_c);
        mobile = findViewById(R.id.phone_c);
        course = findViewById(R.id.course_c);
        year = findViewById(R.id.year_c);
        branch = findViewById(R.id.branch_c);
        refer = findViewById(R.id.refer_c);
        college = findViewById(R.id.college_c);
        training = findViewById(R.id.event_name_c);
        cate = findViewById(R.id.compete_c);

        tName = getIntent().getExtras().get("title").toString();
        category = getIntent().getExtras().get("event").toString();
        DATE = getIntent().getExtras().get("date").toString();
        training.setText(tName);
        cate.setText(category);
        refer.setText("DSCBIJNOR");

        hRef = FirebaseDatabase.getInstance().getReference("UserFormHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        try {
            d1 = formatter.parse(checkIfAlreadyFilled);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            d2 = formatter.parse(DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        dRef = FirebaseDatabase.getInstance().getReference("Forms").child(category);
        hRef = FirebaseDatabase.getInstance().getReference("UserFormHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        if (d1.compareTo(d2) > 0) {
            submit.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Registrations are closed now. Please look our other competitions.", Toast.LENGTH_SHORT).show();
        } else {

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storeRecordToFirebase();
                }
            });
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void storeRecordToFirebase() {
        name_ = name.getText().toString();
        email_ = email.getText().toString();
        mobile_ = mobile.getText().toString();
        course_ = course.getText().toString();
        code_ = refer.getText().toString();
        year_ = year.getText().toString();
        branch_ = branch.getText().toString();
        college_ = college.getText().toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        date_ = simpleDateFormat.format(new Date());

        if (name_.isEmpty()) {
            name.setError("Name is Required");
            name.requestFocus();
            return;
        }
        if (email_.isEmpty()) {
            email.setError("Email is Required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email_).matches()) {
            email.setError("Invalid Email");
            email.requestFocus();
            return;
        }
        if (mobile_.isEmpty()) {
            mobile.setError("Phone number required");
            mobile.requestFocus();
            return;
        }
        if (mobile_.length() != 10) {
            mobile.setError("Invalid phone");
            mobile.requestFocus();
            return;
        }
        if (year_.isEmpty()) {
            year.setError("Year is Required");
            year.requestFocus();
            return;
        }
        if (branch_.isEmpty()) {
            branch.setError("Branch is Required");
            branch.requestFocus();
            return;
        }
        if (college_.isEmpty()) {
            college.setError("College is Required");
            year.requestFocus();
            return;
        }
        if (course_.isEmpty()) {
            course.setError("Course is Required");
            course.requestFocus();
            return;
        }
        if (code_.isEmpty()) {
            refer.setError("Refferal code is Required");
            refer.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference aRef = FirebaseDatabase.getInstance().getReference("Achievment").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        aRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("DP")) {
                    String dpPoints = Objects.requireNonNull(dataSnapshot.child("DP").getValue()).toString();
                    dp = Integer.parseInt(dpPoints);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RegistrationForm tForm = new RegistrationForm(name_, email_, mobile_, course_, year_, branch_, college_, date_, category, tName, code_);
        hRef.child(hRef.push().getKey()).setValue(tForm);
        dRef.child(tName).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dRef.push().getKey()).setValue(tForm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    aRef.child("DP").setValue("" + (dp + 10));
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CRegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    name.getText().clear();
                    email.getText().clear();
                    mobile.getText().clear();
                    course.getText().clear();
                    year.getText().clear();
                    branch.getText().clear();
                    college.getText().clear();
                    refer.getText().clear();
                    displayNotification(name_,date_,category,tName);
                } else {
                    Toast.makeText(CRegistrationActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayNotification(String name, String date_, String category, String tName) {

        Intent intent= new Intent(getApplicationContext(),NotificationActivity.class);
        intent.putExtra("title","Registered for "+tName);
        intent.putExtra("message","Dear \"+name+\" you have successfully registered for \"+category+\" named as \"+tName+\", which will be organized by DSC on \"+date_+\".You just got 10 DP.");
        intent.putExtra("activity","FormsActivity.class");


        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("Registered for "+tName)
                .setContentText("Dear "+name+" you have successfully registered for "+category+" named as "+tName+", which will be organized by DSC on "+date_+".You just got 10 DP.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(1,mBuilder.build());
    }

}
