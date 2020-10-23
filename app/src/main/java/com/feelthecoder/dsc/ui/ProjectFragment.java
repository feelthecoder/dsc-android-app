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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.feelthecoder.dsc.ActivityPDF;
import com.feelthecoder.dsc.Model.ConstantHelperProject;
import com.feelthecoder.dsc.Model.ProjectUpload;
import com.feelthecoder.dsc.MyProjects;
import com.feelthecoder.dsc.NotificationActivity;
import com.feelthecoder.dsc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

public class ProjectFragment extends Fragment {

    DatabaseReference dRef, dRead;
    StorageReference mRef;
    final static int PICK_PDF_CODE = 2342;
    ProgressBar progressBar;
    TextView tx;
    String Name;



    private static final String CHANNEL_ID="PROJECT_UPLOAD";
    private static final String CHANNEL_NAME="Project";
    private static final String CHANNEL_DESC="This Channel shows notifications about upload of project";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project, container, false);
        Button btn = view.findViewById(R.id.btupload);
        Button downS = view.findViewById(R.id.btdown);
        tx = view.findViewById(R.id.txt_file_project);
        progressBar = view.findViewById(R.id.ppp);
        mRef = FirebaseStorage.getInstance().getReference();
        dRef = FirebaseDatabase.getInstance().getReference("Projects").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        dRead = FirebaseDatabase.getInstance().getReference("Projects");

        SharedPreferences mPrefs=getActivity().getSharedPreferences("MyGuide",0);
        String is=mPrefs.getString("pro","no");
        if(is.equals("no")){
            new GuideView.Builder(getActivity())
                        .setTitle("View Sample Project")
                        .setContentText("Click to view sample project file.")
                        .setTargetView(downS)
                        .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                        .build()
                        .show();

            new GuideView.Builder(getActivity())
                        .setTitle("Upload Project")
                        .setContentText("Click to upload your project in pdf form. It's format must be similar to the sample project.")
                        .setTargetView(btn)
                        .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                        .build()
                        .show();


            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("pro","yes");
            editor.apply();
        }






        //Creating Notification Channel for below oreo versions
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager= getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        btn.setOnClickListener(new View.OnClickListener() {
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
                alertDialog.setTitle("Project Name");
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

                alertDialog.show();
            }
        });
        downS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dRead.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intent=new Intent(getContext(), ActivityPDF.class);
                        String getsample = dataSnapshot.child("sample").getValue().toString();
                        intent.putExtra("link",getsample);
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        return view;

}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                uploadFile(data.getData(),Name);
            }else{
                Toast.makeText(getActivity(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
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

    private void uploadFile(Uri data, final String Name) {
        progressBar.setVisibility(View.VISIBLE);
        final StorageReference sRef = mRef.child(ConstantHelperProject.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        tx.setVisibility(View.GONE);
                        final String[] pdfLink = new String[1];
                        sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pdfLink[0] = uri.toString();
                                ProjectUpload upload = new ProjectUpload(Name, pdfLink[0],"0");
                                dRef.child(Objects.requireNonNull(dRef.push().getKey())).setValue(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getActivity(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            displayNotification(Name);
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

    private void displayNotification(String name) {


        Intent intent= new Intent(getContext(), NotificationActivity.class);
        intent.putExtra("title","Project Submitted Successfully");
        intent.putExtra("message","Your project "+name+" has successfully uploaded for further development. You can check status of you project in Project-> My Projects in upper right corner.");
        intent.putExtra("activity","MyProjects.class");

        PendingIntent pendingIntent=PendingIntent.getActivity(getContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(),CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("Project Submitted Successfully")
                .setContentText("Your project "+name+" has successfully uploaded for further development. You can check status of you project in Project-> My Projects in upper right corner.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(getContext());
        managerCompat.notify(1,mBuilder.build());

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.project_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sub_pro: {
                startActivity(new Intent(getContext(), MyProjects.class));
                break;

            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }



}

