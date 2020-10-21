package com.example.dsc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsc.Model.ProjectUpload;
import com.example.dsc.ViewHolder.ViewSpace;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;

public class MyProjects extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference dRef;
    RelativeLayout connect;
    TextView txtProject;
    ProgressBar project_progress;


    private static final String CHANNEL_ID="PROJECT";
    private static final String CHANNEL_NAME="Project";
    private static final String CHANNEL_DESC="This Channel shows notifications about the status of project.";


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
        setContentView(R.layout.activity_view_project);
        Toolbar toolbar=findViewById(R.id.tool_project);
        connect=findViewById(R.id.no_connect_project);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        txtProject=findViewById(R.id.no_project_here);
        project_progress=findViewById(R.id.project_progress);
        txtProject.setVisibility(View.VISIBLE);
        dRef = FirebaseDatabase.getInstance().getReference("Projects").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        recyclerView = findViewById(R.id.projectlistView);
        LinearLayoutManager li= new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if(isOnline()) {
            project_progress.setVisibility(View.VISIBLE);
            getMyProjets();
            connect.setVisibility(View.INVISIBLE);
        }
        else
        {
            connect.setVisibility(View.VISIBLE);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getMyProjets() {

        FirebaseRecyclerOptions<ProjectUpload> options =
                new FirebaseRecyclerOptions.Builder<ProjectUpload>()
                        .setQuery(dRef, ProjectUpload.class).build();

        FirebaseRecyclerAdapter<ProjectUpload, ProjectViewHolder> adapter = new FirebaseRecyclerAdapter<ProjectUpload, ProjectViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final ProjectViewHolder projectViewHolder, int i, @NonNull ProjectUpload projectUpload) {
                final String postID = getRef(i).getKey();
                assert postID != null;
                dRef.child(postID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("status")) {
                            txtProject.setVisibility(View.INVISIBLE);
                            project_progress.setVisibility(View.INVISIBLE);
                            final String projectCaption = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString();
                            String status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();


                            if(status.equals("2")){
                                displayNotification(projectCaption);
                            }


                            projectViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(MyProjects.this, ActivityPDF.class);
                                    intent.putExtra("link",link);
                                    startActivity(intent);
                                }
                            });


                            projectViewHolder.mTitle.setText(projectCaption);
                            if(status.equals("0")) {
                                projectViewHolder.mStatus.setText("Pending");
                                projectViewHolder.mStatus.setTextColor(Color.RED);
                            }
                            else if(status.equals("1")) {
                                projectViewHolder.mStatus.setText("Under Review");
                                projectViewHolder.mStatus.setTextColor(Color.BLUE);
                            }
                            else if(status.equals("2")) {
                                projectViewHolder.mStatus.setText("Confirmed");
                                projectViewHolder.mStatus.setTextColor(Color.BLACK);
                            }
                            else if(status.equals("3")) {
                                projectViewHolder.mStatus.setText("Under Development");
                                projectViewHolder.mStatus.setTextColor(Color.GRAY);
                            }
                            else if(status.equals("4")) {
                                projectViewHolder.mStatus.setText("Completed");
                                projectViewHolder.mStatus.setTextColor(Color.GREEN);
                            }
                        }

                        setGuide("Projects","Swipe down to see your submitted projects & Click to view your project.",projectViewHolder.itemView,"projM");
                        setGuide("Status","This is your project status, It changes whenever your project accepted or rejected.",projectViewHolder.mStatus,"projS");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list_layout, parent, false);
                ProjectViewHolder projectViewHolder=new ProjectViewHolder(view);
                return projectViewHolder;
            }
        };

        project_progress.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

}

    private void displayNotification(String projectCaption) {
        Intent intent= new Intent(getApplicationContext(),NotificationActivity.class);
        intent.putExtra("title","Project Confirmed");
        intent.putExtra("message","Your project "+projectCaption+" has confirmed by our team. Our team will contact you shortly, if not contacted within 5 days please mail us at dscrecbijnor@gmail.com");
        intent.putExtra("activity","MyProjects.class");

        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("Project Confirmed")
                .setContentText("Your project "+projectCaption+" has confirmed by our team. Our team will contact you shortly, if not contacted within 5 days please mail us at dscrecbijnor@gmail.com")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        NotificationManagerCompat managerCompat= NotificationManagerCompat.from(this);
        managerCompat.notify(1,mBuilder.build());
    }


    private static class ProjectViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mStatus;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.project_name);
            mStatus = itemView.findViewById(R.id.project_status);
        }


    }
    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;

    }
    private void setGuide(String name,String msg,View view,String key) {
        SharedPreferences mPrefs = getSharedPreferences("MyGuide", 0);
        String is = mPrefs.getString(key, "no");
        if (is.equals("no")) {
            new GuideView.Builder(this)
                    .setTitle(name)
                    .setContentText(msg)
                    .setTargetView(view)
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build()
                    .show();

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(key, "yes");
            editor.apply();
        }
    }
}


