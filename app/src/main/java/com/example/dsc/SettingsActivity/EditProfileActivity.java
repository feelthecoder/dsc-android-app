package com.example.dsc.SettingsActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsc.Model.User;
import com.example.dsc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import static android.view.View.VISIBLE;

public class EditProfileActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 0;
    EditText tname,tmail,tusername,tskills,tdob,tmobile,tgender;
    TextView tDP;
    CircularImageView pImage;
    DatabaseReference dRef,aRef;
    Button submit;
    Uri uriProfileImage;
    String providerId = null;



    String name,mail,username,skills,dob,mobile,gender,DP,link;
    ProgressBar progressBar;


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
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar=findViewById(R.id.tool_edit_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dRef= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        aRef=FirebaseDatabase.getInstance().getReference("Achievment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        tname=findViewById(R.id.profile_name_edit);
        tmail=findViewById(R.id.profile_email_edit);
        tusername=findViewById(R.id.profile_username_edit_a);
        tskills=findViewById(R.id.profile_skills_edit_a);
        progressBar=findViewById(R.id.edit_progress_profile);
        tdob=findViewById(R.id.profile_dob_edit_a);
        tmobile=findViewById(R.id.profile_phone_edit_a);
        tgender=findViewById(R.id.profile_gender_edit_a);
        tDP=findViewById(R.id.profile_score_edit_a);
        pImage=findViewById(R.id.profile_image_edit);

        submit=findViewById(R.id.button_save_profile);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        for (UserInfo profile : user.getProviderData()) {
            providerId = profile.getProviderId();
        }




        loadData();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateToFirebaseUser();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateToFirebaseUser() {
        name=tname.getText().toString();
        username=tusername.getText().toString();
        dob=tdob.getText().toString();
        gender=tgender.getText().toString();
        skills=tskills.getText().toString();
        aRef.child("skills").setValue(skills);

        if(!providerId.equals("phone")){
            mobile=tmobile.getText().toString();
            if (mobile.isEmpty()) {
                tmobile.setError("Phone number required");
                tmobile.requestFocus();
                return;
            }
            if (mobile.length() != 10) {
                tmobile.setError("Invalid phone");
                tmobile.requestFocus();
                return;
            }
        }
        else{
            tmobile.setInputType(InputType.TYPE_NULL);
        }

        if(!providerId.equals("password")){
            mail=tmail.getText().toString();
            if (mail.isEmpty()) {
                tmail.setError("Email is Required");
                tmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                tmail.setError("Invalid Email");
                tmail.requestFocus();
                return;
            }
        }
        else {
            tmail.setInputType(InputType.TYPE_NULL);
        }


        if (name.isEmpty()) {
            tname.setError("Name is Required");
            tname.requestFocus();
            return;
        }



        if (username.isEmpty()) {
            tusername.setError("Username is Required");
            tusername.requestFocus();
            return;
        }
        if (dob.isEmpty()) {
            tdob.setError("Birth Date is Required!");
            tdob.requestFocus();
            return;
        }
        if (!isValidDate(dob)) {
            tdob.setError("Invalid format");
            tdob.requestFocus();
            return;
        }
        if (gender.isEmpty()) {
            tgender.setError("It Can't be empty");
            tgender.requestFocus();
            return;
        }
        if (skills.isEmpty()){
            tskills.setError("Enter your skills");
            tskills.requestFocus();
            return;
        }
        progressBar.setVisibility(VISIBLE);
        User usss=new User(name,mail,username,gender,dob,mobile,link);

        dRef.setValue(usss).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    aRef.child("name").setValue(name);
                    aRef.child("image").setValue(link);
                    loadData();
                    Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadData() {
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("displayName")){

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
                    pImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageChooser();
                        }
                    });
                    Glide.with(EditProfileActivity.this).load(link).into(pImage);

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

    }
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                pImage.setImageBitmap(bitmap);
                uploadImage();
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(VISIBLE);
                        updateToFirebaseUser();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private void uploadImage() {
        final StorageReference profileReference = FirebaseStorage.getInstance().getReference("profilepics/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + ".jpg");
        if (uriProfileImage != null) {
            progressBar.setVisibility(VISIBLE);
            Toast.makeText(EditProfileActivity.this, "Please wait, Your picture is uploading...", Toast.LENGTH_LONG).show();
            profileReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            link = uri.toString();
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

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

}
