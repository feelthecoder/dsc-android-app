package com.feelthecoder.dsc.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.feelthecoder.dsc.Main1Activity;
import com.feelthecoder.dsc.Model.User;
import com.feelthecoder.dsc.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.view.View.GONE;

public class Google_Login_Activity extends AppCompatActivity {
    private static final int RC_SIGN_IN =234 ;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    SignInButton g_sign;
    public static Activity fa;
    ProgressBar bar;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
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
        setContentView(R.layout.log_it_google);

        Window window = Google_Login_Activity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(Google_Login_Activity.this, R.color.colorWhite));
        window.setNavigationBarColor(ContextCompat.getColor(Google_Login_Activity.this,R.color.colorWhite));
        g_sign=findViewById(R.id.sign_in_button);
        bar=findViewById(R.id.ppp);
        mAuth= FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        g_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            bar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }
            catch (ApiException e) {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(Google_Login_Activity.this, e.getMessage()+" Unable to login", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           final FirebaseUser user1=mAuth.getCurrentUser();
                            String userRef= Objects.requireNonNull(user1).getUid();
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userRef);
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        bar.setVisibility(GONE);
                                        Toast.makeText(Google_Login_Activity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                        Intent intent =new Intent(Google_Login_Activity.this, Main1Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        bar.setVisibility(GONE);
                                        updateUI(user1);
                                        Intent intent =new Intent(Google_Login_Activity.this,Main1Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            Toast.makeText(Google_Login_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user1) {
        String displayName=user1.getDisplayName();
        String email=user1.getEmail();
        String phone= "phonedsc";
        String user= "userdsc";
        String gender= "Male";
        String dob= "08/06/2019";
        String profileImageUrl= String.valueOf(user1.getPhotoUrl());
        DatabaseReference aRef=FirebaseDatabase.getInstance().getReference("Achievment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        aRef.child("dp").setValue("150");
        aRef.child("quizwon").setValue("0");
        aRef.child("skills").setValue("None");
        aRef.child("name").setValue(displayName);
        aRef.child("image").setValue(profileImageUrl);
        User userr=new User(displayName,email,user,gender,dob,phone,profileImageUrl);
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(userr).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Google_Login_Activity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
