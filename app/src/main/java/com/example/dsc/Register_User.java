package com.example.dsc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsc.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Register_User extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;

    String firstName, lastName, email, user, pass, confirmPass;
    String gender;
    String phone, dob;
    EditText fName, lName, mail, mob, uName, nPass, cPass, dateOfBirth;
    RadioGroup radioSexGroup;
    RadioButton sex;
    String profileImageUrl;
    CircularImageView imageView;
    private FirebaseAuth mAuth;
    ProgressBar ppBar;
    ProgressDialog progressBarWheel;
    private Uri uriProfileImage;
    String displayName;
 public static Activity fa;
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
        setContentView(R.layout.register_user);
        TextView txt = findViewById(R.id.already_have_account);
        Button btn = findViewById(R.id.sign_up_btn);
        ppBar = findViewById(R.id.ppBaar);
        imageView = findViewById(R.id.imagelogo1);
        mAuth = FirebaseAuth.getInstance();
        fa=this;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_user();

            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoalllogin = new Intent(Register_User.this, Login_Email.class);
                startActivity(gotoalllogin);
                finish();
            }
        });
    }

    private void register_user() {
        mail = findViewById(R.id.edtx4);
        email = mail.getText().toString();
        nPass = findViewById(R.id.edtx6);
        pass = nPass.getText().toString();
        fName = findViewById(R.id.edtx1);
        firstName = fName.getText().toString();
        lName = findViewById(R.id.edtx2);
        lastName = lName.getText().toString();
        cPass = findViewById(R.id.edtx7);
        confirmPass = cPass.getText().toString();
        mob = findViewById(R.id.edtx3);
        phone = mob.getText().toString();
        uName = findViewById(R.id.edtx5);
        user = uName.getText().toString();
        dateOfBirth = findViewById(R.id.datepick);
        dob = dateOfBirth.getText().toString();
        radioSexGroup = findViewById(R.id.radiog);

        int selectedID=radioSexGroup.getCheckedRadioButtonId();
        sex=findViewById(selectedID);
        gender=sex.getText().toString();


        if (firstName.isEmpty()) {
            fName.setError("First Name is Required");
            fName.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            lName.setError("Last Name is Required");
            lName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            mail.setError("Email is Required");
            mail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mail.setError("Invalid Email");
            mail.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            mob.setError("Phone number required");
            mob.requestFocus();
            return;
        }
        if (phone.length() != 10) {
            mob.setError("Invalid phone");
            mob.requestFocus();
            return;
        }
        if (user.isEmpty()) {
            uName.setError("Username is Required");
            uName.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            nPass.setError("Password is Required");
            nPass.requestFocus();
            return;
        }
        if (confirmPass.isEmpty()) {
            cPass.setError("Confirmation is Required");
            cPass.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "Minimum length of password should be 6", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(pass.equals(confirmPass))) {
            cPass.setError("Password not Matched");
            cPass.requestFocus();

            return;
        }
        if (dob.isEmpty()) {
            dateOfBirth.setError("Birth Date is Required!");
            dateOfBirth.requestFocus();
            return;
        }
        if (!isValidDate(dob)) {
            dateOfBirth.setError("Invalid format");
            dateOfBirth.requestFocus();
            return;
        }

        if (gender.isEmpty()) {
            return;
        }

        if(profileImageUrl.isEmpty()){
            Toast.makeText(this, "Please add profile to continue", Toast.LENGTH_SHORT).show();
            return;
        }
        displayName = firstName + " " + lastName;
        ProgressDialog.show(Register_User.this, "Creating your account", "Please wait..", true);
        mAuth.createUserWithEmailAndPassword(email, confirmPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    DatabaseReference aRef=FirebaseDatabase.getInstance().getReference("Achievment").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    aRef.child("DP").setValue("150");
                    aRef.child("quizwon").setValue("0");
                    aRef.child("skills").setValue("None");
                    aRef.child("name").setValue(displayName);
                    aRef.child("image").setValue(profileImageUrl);
                    User aUser = new User(displayName, email, user, gender, dob, phone, profileImageUrl);
                    FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(aUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Handler handle = new Handler();
                                handle.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Register_User.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                                        saveUserProfileImage();
                                        sendVerificationEmail();
                                        Intent intent = new Intent(Register_User.this, Main1Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    progressBarWheel.dismiss();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(Register_User.this, "Email is already registered.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Register_User.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    public boolean isValidDate(String DateOFBirth) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date testDate = null;
        try {
            testDate = sdf.parse(DateOFBirth);
        } catch (ParseException e) {
            return false;
        }
        return sdf.format(testDate).equals(DateOFBirth);
    }


    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(Register_User.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register_User.this, "Verification Email not sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserProfileImage() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile;
            profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse((profileImageUrl))).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Register_User.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImage() {
        final StorageReference profileReference = FirebaseStorage.getInstance().getReference(String.format("profilepics/%d.jpg", Calendar.getInstance().getTimeInMillis()));
        if (uriProfileImage != null) {
            ppBar.setVisibility(View.VISIBLE);
            profileReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ppBar.setVisibility(View.GONE);
                    profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileImageUrl = uri.toString();
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ppBar.setVisibility(View.GONE);
                            Toast.makeText(Register_User.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    @Override
    public void onBackPressed() {
        if(profileImageUrl!=null) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(profileImageUrl);
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   finish();
                }
            });
        }
        else{
            finish();
        }

    }
}
