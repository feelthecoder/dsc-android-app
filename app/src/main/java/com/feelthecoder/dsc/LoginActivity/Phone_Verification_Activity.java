package com.feelthecoder.dsc.LoginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.feelthecoder.dsc.Main1Activity;
import com.feelthecoder.dsc.Model.User;
import com.feelthecoder.dsc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Phone_Verification_Activity extends AppCompatActivity {

    EditText editText;
    Button button;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    ProgressBar bbBar;
    String mobile;

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
        setContentView(R.layout.otp_phone_verification);
        Window window = Phone_Verification_Activity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(Phone_Verification_Activity.this, R.color.colorWhite));
        window.setNavigationBarColor(ContextCompat.getColor(Phone_Verification_Activity.this,R.color.colorWhite));
        mAuth = FirebaseAuth.getInstance();
        editText = findViewById(R.id.edtxt_otp);
        button = findViewById(R.id.otp_login);
        bbBar=findViewById(R.id.otp_progress_bar);
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        sendVerificationCode(mobile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editText.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editText.setError("Enter valid code");
                    editText.requestFocus();
                    return;
                }
                verifyVerificationCode(code);


            }
        });
    }

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Phone_Verification_Activity.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().toString();
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(user);
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Toast.makeText(Phone_Verification_Activity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Phone_Verification_Activity.this, Main1Activity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        register_data();
                                        Intent intent = new Intent(Phone_Verification_Activity.this, Main1Activity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            bbBar.setVisibility(View.GONE);
                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered.";
                                Toast.makeText(Phone_Verification_Activity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    private void sendVerificationCode(String mobile) {
        Toast.makeText(this, "Verification Code Sent on "+mobile, Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                 mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                bbBar.setVisibility(View.VISIBLE);
                editText.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            bbBar.setVisibility(View.GONE);
            Toast.makeText(Phone_Verification_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };
    private void register_data() {
        String displayName= "Your Name";
        String email="yourmail@xyz.com";
        String phone=mobile;
        String user= "userdsc";
        String gender= "Female";
        String dob= "08/06/2019";
        String profileImageUrl= "https://";
        DatabaseReference aRef=FirebaseDatabase.getInstance().getReference("Achievment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        aRef.child("dp").setValue("150");
        aRef.child("quizwon").setValue("0");
        aRef.child("skills").setValue("None");
        aRef.child("name").setValue(displayName);
        aRef.child("image").setValue(profileImageUrl);
        User userr=new User(displayName,email,user,gender,dob,phone,profileImageUrl);
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(userr).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Phone_Verification_Activity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

