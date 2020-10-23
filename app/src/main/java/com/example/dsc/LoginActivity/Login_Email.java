package com.example.dsc.LoginActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsc.Main1Activity;
import com.example.dsc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class Login_Email extends AppCompatActivity {
    EditText edt, ed2;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    String email,pass;
    TextView forText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences mPrefs=getSharedPreferences("MyPrefs",0);
        String is=mPrefs.getString("mode","not");
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES|| Objects.requireNonNull(is).equals("dark")){
            setTheme(R.style.DarkTheme);

        }
        else
        {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_phone_login);

        Window window = Login_Email.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(Login_Email.this, R.color.colorWhite));
        window.setNavigationBarColor(ContextCompat.getColor(Login_Email.this,R.color.colorWhite));
        TextView textView = findViewById(R.id.no_account);
        progressBar = findViewById(R.id.progressBar);
        Button btn = findViewById(R.id.log_in);
        forText=findViewById(R.id.forgot_password);
        mAuth = FirebaseAuth.getInstance();
        forText.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                          forgotScreen();
                                       }

            private void forgotScreen() {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login_Email.this);
                alertDialog.setTitle("Forgot Password");
                alertDialog.setMessage("Enter Your Email");
                alertDialog.setIcon(R.drawable.forgot);
                final EditText input = new EditText(Login_Email.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                email = input.getText().toString();
                                if (email.isEmpty()) {
                                    input.setError("Email is required");
                                    input.requestFocus();
                                    return;
                                }
                                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    input.setError("Invalid Email");
                                    input.requestFocus();
                                    return;
                                }
                                progressBar.setVisibility(View.VISIBLE);
                                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Login_Email.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Login_Email.this, "Entered Email does not matches with our records, error.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

            btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_user();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Email.this, Register_User.class);
                startActivity(intent);
            }
        });
    }

    private void login_user() {
        edt = findViewById(R.id.editmail);
        ed2= findViewById(R.id.edtxt2);
        email=edt.getText().toString();
        pass=ed2.getText().toString();
        if (email.isEmpty()) {
            edt.setError("Email is required");
            edt.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            ed2.setError("Password is required");
            ed2.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "Minimum length of password should be 6", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edt.setError("Invalid Email");
            edt.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(Login_Email.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login_Email.this, Main1Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login_Email.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(this,Main1Activity.class));
            finish();
        }
    }

}
