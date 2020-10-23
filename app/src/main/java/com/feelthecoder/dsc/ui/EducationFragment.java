package com.feelthecoder.dsc.ui;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feelthecoder.dsc.ActivityResources;
import com.feelthecoder.dsc.Model.ProjectUpload;
import com.feelthecoder.dsc.NotificationActivity;
import com.feelthecoder.dsc.R;
import com.feelthecoder.dsc.ResourceActivity.eResourceActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;

import static android.app.Activity.RESULT_OK;
import static android.view.View.INVISIBLE;

public class EducationFragment extends Fragment implements View.OnClickListener {

    ImageView resource;
    final static int PICK_PDF_CODE = 2342;
    String Name;
    Button upload;
    ProgressBar progressBar;
    StorageReference mRef;
    DatabaseReference dRef;


    private static final String CHANNEL_ID="RESOURCE";
    private static final String CHANNEL_NAME="Education";
    private static final String CHANNEL_DESC="This Channel shows notifications whenever user submit some resources.";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CircularImageView s1 = getView().findViewById(R.id.c);
        CircularImageView s2 =getView().findViewById(R.id.android);
        CircularImageView s3 =getView().findViewById(R.id.ios);
        CircularImageView s4 =getView().findViewById(R.id.nodejs);
        CircularImageView s5 =getView().findViewById(R.id.reactnative);
        CircularImageView s6 =getView().findViewById(R.id.jquery);
        CircularImageView s7 =getView().findViewById(R.id.cplus);
        CircularImageView s8 =getView().findViewById(R.id.java);
        CircularImageView s9 =getView().findViewById(R.id.python);
        CircularImageView s10 =getView().findViewById(R.id.sql);
        CircularImageView s11 =getView().findViewById(R.id.kotlin);
        CircularImageView s12 =getView().findViewById(R.id.javascript);
        CircularImageView s13 =getView().findViewById(R.id.html);
        CircularImageView s14 = getView().findViewById(R.id.css);
        CircularImageView s15 =getView().findViewById(R.id.bootstrap);
        CircularImageView s16=getView().findViewById(R.id.dart);
        CircularImageView s17=getView().findViewById(R.id.objective_c);
        CircularImageView s18=getView().findViewById(R.id.swift);
        CircularImageView s19=getView().findViewById(R.id.iot);
        CircularImageView s20=getView().findViewById(R.id.ml);
        CircularImageView s21 =getView().findViewById(R.id.cloud);
        CircularImageView s22=getView().findViewById(R.id.ai);
        CircularImageView s23 =getView().findViewById(R.id.ruby);
        CircularImageView s24 =getView().findViewById(R.id.R);
        CircularImageView s25 =getView().findViewById(R.id.php);
        CircularImageView s26 =getView().findViewById(R.id.haskell);
        CircularImageView s27= getView().findViewById(R.id.unity);
        CircularImageView s28=getView().findViewById(R.id.csharp);
        CircularImageView s29=getView().findViewById(R.id.techother);
        CircularImageView s30=getView().findViewById(R.id.non_tech);


        s1.setOnClickListener(this);
        s2.setOnClickListener(this);
        s3.setOnClickListener(this);
        s4.setOnClickListener(this);
        s5.setOnClickListener(this);
        s6.setOnClickListener(this);
        s7.setOnClickListener(this);
        s8.setOnClickListener(this);
        s9.setOnClickListener(this);
        s10.setOnClickListener(this);
        s11.setOnClickListener(this);
        s12.setOnClickListener(this);
        s13.setOnClickListener(this);
        s14.setOnClickListener(this);
        s15.setOnClickListener(this);
        s16.setOnClickListener(this);
        s17.setOnClickListener(this);
        s18.setOnClickListener(this);
        s19.setOnClickListener(this);
        s20.setOnClickListener(this);
        s21.setOnClickListener(this);
        s22.setOnClickListener(this);
        s23.setOnClickListener(this);
        s24.setOnClickListener(this);
        s25.setOnClickListener(this);
        s26.setOnClickListener(this);
        s27.setOnClickListener(this);
        s28.setOnClickListener(this);
        s29.setOnClickListener(this);
        s30.setOnClickListener(this);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_education, container, false);
        resource=view.findViewById(R.id.submit_resource);
        upload=view.findViewById(R.id.submit_button_r);
        progressBar=view.findViewById(R.id.submit_r);
        mRef= FirebaseStorage.getInstance().getReference();
        dRef= FirebaseDatabase.getInstance().getReference("SubmittedResources").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

        SharedPreferences mPrefs=getActivity().getSharedPreferences("MyGuide",0);

        String is=mPrefs.getString("smee","no");
        if(is.equals("no")){

            new GuideView.Builder(getActivity())
                    .setTitle("Submit Resource")
                    .setContentText("Click here to submit any resource to help other students, you can check submitted resource by clicking upper right corner button.")
                    .setTargetView(resource)
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build()
                    .show();


            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("smee","yes");
            editor.apply();
        }


        resource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextThemeWrapper ctw;
                SharedPreferences mPrefs = getContext().getSharedPreferences("MyPrefs", 0);
                String is = mPrefs.getString("mode", "not");
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || is.equals("dark")) {
                    ctw = new ContextThemeWrapper(getContext(), R.style.DarkTheme);
                } else {
                    ctw = new ContextThemeWrapper(getContext(), R.style.AppTheme);
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctw);
                alertDialog.setTitle("Resource Name");
                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String name = input.getText().toString().trim();
                                if (name.isEmpty()) {
                                    input.setError("Name is required");
                                    input.requestFocus();
                                    return;
                                }
                                getPDF(name);

                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription(CHANNEL_DESC);
                    NotificationManager manager= getActivity().getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }

                alertDialog.show();
            }
        });
        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edu_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_sub_resource: {
                Intent i = new Intent(getActivity(), ActivityResources.class);
                startActivity(i);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.c:
                Intent c=new Intent(getActivity(), eResourceActivity.class);
                c.putExtra("title","C");
                startActivity(c);
                break;
            case R.id.android:
                Intent android=new Intent(getActivity(), eResourceActivity.class);
                android.putExtra("title","Android");
                startActivity(android);
                break;
            case R.id.ios:
                Intent ios=new Intent(getActivity(), eResourceActivity.class);
                ios.putExtra("title","iOS");
                startActivity(ios);
                break;
            case R.id.nodejs:
                Intent nodejs=new Intent(getActivity(), eResourceActivity.class);
                nodejs.putExtra("title","Node Js");
                startActivity(nodejs);
                break;
            case R.id.reactnative:
                Intent reactnative=new Intent(getActivity(), eResourceActivity.class);
                reactnative.putExtra("title","React");
                startActivity(reactnative);
                break;
            case R.id.jquery:
                Intent jquery=new Intent(getActivity(), eResourceActivity.class);
                jquery.putExtra("title","jQuery");
                startActivity(jquery);
                break;
            case R.id.cplus:
                Intent cplus=new Intent(getActivity(), eResourceActivity.class);
                cplus.putExtra("title","C++");
                startActivity(cplus);
                break;
            case R.id.java:
                Intent java=new Intent(getActivity(), eResourceActivity.class);
                java.putExtra("title","Java");
                startActivity(java);
                break;
            case R.id.python:
                Intent python=new Intent(getActivity(), eResourceActivity.class);
                python.putExtra("title","Python");
                startActivity(python);
                break;
            case R.id.sql:
                Intent sql=new Intent(getActivity(), eResourceActivity.class);
                sql.putExtra("title","SQL");
                startActivity(sql);
                break;
            case R.id.kotlin:
                Intent kotlin=new Intent(getActivity(), eResourceActivity.class);
                kotlin.putExtra("title","Kotlin");
                startActivity(kotlin);
                break;
            case R.id.javascript:
                Intent javascript=new Intent(getActivity(), eResourceActivity.class);
                javascript.putExtra("title","JavaScript");
                startActivity(javascript);
                break;
            case R.id.html:
                Intent html=new Intent(getActivity(), eResourceActivity.class);
                html.putExtra("title","HTML");
                startActivity(html);
                break;
            case R.id.css:
                Intent css=new Intent(getActivity(), eResourceActivity.class);
                css.putExtra("title","CSS");
                startActivity(css);
                break;
            case R.id.bootstrap:
                Intent bootstrap=new Intent(getActivity(), eResourceActivity.class);
                bootstrap.putExtra("title","Bootstrap");
                startActivity(bootstrap);
                break;
            case R.id.dart:
                Intent dart=new Intent(getActivity(), eResourceActivity.class);
                dart.putExtra("title","Dart");
                startActivity(dart);
                break;
            case R.id.objective_c:
                Intent objective=new Intent(getActivity(), eResourceActivity.class);
                objective.putExtra("title","Objective C");
                startActivity(objective);
                break;
            case R.id.swift:
                Intent swift=new Intent(getActivity(), eResourceActivity.class);
                swift.putExtra("title","Swift");
                startActivity(swift);
                break;
            case R.id.iot:
                Intent iot=new Intent(getActivity(), eResourceActivity.class);
                iot.putExtra("title","Internet Of Things");
                startActivity(iot);
                break;
            case R.id.ml:
                Intent ml=new Intent(getActivity(), eResourceActivity.class);
                ml.putExtra("title","Machine Learning");
                startActivity(ml);
                break;
            case R.id.cloud:
                Intent cc=new Intent(getActivity(), eResourceActivity.class);
                cc.putExtra("title","Cloud Computing");
                startActivity(cc);
                break;
            case R.id.ai:
                Intent ai=new Intent(getActivity(), eResourceActivity.class);
                ai.putExtra("title","Artificial Intelligence");
                startActivity(ai);
                break;
            case R.id.ruby:
                Intent ruby=new Intent(getActivity(), eResourceActivity.class);
                ruby.putExtra("title","Ruby");
                startActivity(ruby);
                break;
            case R.id.R:
                Intent Re=new Intent(getActivity(), eResourceActivity.class);
                Re.putExtra("title","R Programming");
                startActivity(Re);
                break;
            case R.id.php:
                Intent php=new Intent(getActivity(), eResourceActivity.class);
                php.putExtra("title","PHP");
                startActivity(php);
                break;
            case R.id.haskell:
                Intent hask=new Intent(getActivity(), eResourceActivity.class);
                hask.putExtra("title","Haskell");
                startActivity(hask);
                break;
            case R.id.unity:
                Intent unity=new Intent(getActivity(), eResourceActivity.class);
                unity.putExtra("title","Unity");
                startActivity(unity);
                break;
            case R.id.csharp:
                Intent sharp=new Intent(getActivity(), eResourceActivity.class);
                sharp.putExtra("title","C Sharp");
                startActivity(sharp);
                break;
            case R.id.techother:
                Intent techother=new Intent(getActivity(), eResourceActivity.class);
                techother.putExtra("title","Other Tech Courses");
                startActivity(techother);
                break;
            case R.id.non_tech:
                Intent non_tech=new Intent(getActivity(), eResourceActivity.class);
                non_tech.putExtra("title","Non-Technical Courses");
                startActivity(non_tech);
                break;

        }
    }
    private void getPDF(String name) {
        Name=name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivity(intent);
            return;
        }

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                upload.setVisibility(View.VISIBLE);
                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upload.setVisibility(INVISIBLE);
                        uploadFile(data.getData(),Name);
                    }
                });

            }else{
                Toast.makeText(getActivity(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadFile(Uri data, final String Name) {
        progressBar.setVisibility(View.VISIBLE);
        final StorageReference sRef = mRef.child("Submit Resources/"+ System.currentTimeMillis() + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final String[] pdfLink = new String[1];
                        sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pdfLink[0] = uri.toString();
                                final ProjectUpload upload = new ProjectUpload(Name, pdfLink[0],"0");
                                dRef.child(Objects.requireNonNull(dRef.push().getKey())).setValue(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            progressBar.setVisibility(View.GONE);
                                            sendNotification(Name);
                                            Toast.makeText(getActivity(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(getActivity(), "unable to upload!", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                });
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                    }
                });

    }

    private void sendNotification(String name) {

        Intent intent= new Intent(getContext(), NotificationActivity.class);
        intent.putExtra("title","Resource Submitted Successfully");
        intent.putExtra("message","Your resource "+name+" has successfully uploaded for further process. You can check status of you resource in Education-> Submitted Resource in upper right corner.");
        intent.putExtra("activity","ActivityResources.class");

        PendingIntent pendingIntent=PendingIntent.getActivity(getContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(),CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("Resource Submitted Successfully")
                .setContentText("Your resource "+name+" has successfully uploaded for further process. You can check status of you resource in Education-> Submitted Resource in upper right corner.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(getContext());
        managerCompat.notify(1,mBuilder.build());
    }


}