package com.example.dsc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dsc.Model.ResUpload;
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

public class ActivityResources extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference dRef;
    TextView txtProject;


    private static final String CHANNEL_ID = "LIBRARY";
    private static final String CHANNEL_NAME = "Resources";
    private static final String CHANNEL_DESC = "This Channel shows notifications about the status of submitted resources.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences mPrefs = getSharedPreferences("MyPrefs", 0);
        String is = mPrefs.getString("mode", "not");
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || is.equals("dark")) {
            setTheme(R.style.DarkTheme);

        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);
        Toolbar toolbar = findViewById(R.id.tool_res);

        txtProject = findViewById(R.id.no_res_here);
        txtProject.setVisibility(View.VISIBLE);
        dRef = FirebaseDatabase.getInstance().getReference("SubmittedResources").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        recyclerView = findViewById(R.id.reslistView);
        LinearLayoutManager li = new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Submitted Resources");

        getMyResources();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getMyResources()
    {

        FirebaseRecyclerOptions<ResUpload> options =
                new FirebaseRecyclerOptions.Builder<ResUpload>()
                        .setQuery(dRef, ResUpload.class).build();

        FirebaseRecyclerAdapter<ResUpload, ActivityResources.ResViewHolder> adapter = new FirebaseRecyclerAdapter<ResUpload, ActivityResources.ResViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final ActivityResources.ResViewHolder ResViewHolder, int i, @NonNull ResUpload ResUpload) {
                final String postID = getRef(i).getKey();
                assert postID != null;
                dRef.child(postID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("status")) {
                            txtProject.setVisibility(View.INVISIBLE);
                            final String titleCaption = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString();
                            String status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();


                            if (status.equals("2")) {
                                displayNotification(titleCaption);
                            }


                            ResViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ActivityResources.this, ActivityPDF.class);
                                    intent.putExtra("link", link);
                                    startActivity(intent);
                                }
                            });


                            ResViewHolder.mTitle.setText(titleCaption);
                            if (status.equals("0")) {
                                ResViewHolder.mStatus.setText("Pending");
                                ResViewHolder.mStatus.setTextColor(Color.RED);
                            } else if (status.equals("1")) {
                                ResViewHolder.mStatus.setText("Rejected");
                                ResViewHolder.mStatus.setTextColor(Color.BLACK);
                            } else if (status.equals("2")) {
                                ResViewHolder.mStatus.setText("Accepted");
                                ResViewHolder.mStatus.setTextColor(Color.GREEN);
                            }
                        }

                        setGuide("Resources", "Swipe down to see your submitted resources & Click to view your resource.", ResViewHolder.itemView, "projR");
                        setGuide("Status", "This is your resource status, It changes whenever your resource accepted or rejected.", ResViewHolder.mStatus, "projL");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ActivityResources.ResViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.res_list_layout, parent, false);
                ActivityResources.ResViewHolder ResViewHolder = new ActivityResources.ResViewHolder(view);
                return ResViewHolder;
            }
        };


        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }

    private void displayNotification(String title) {
        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.putExtra("title","Resource Confirmed");
        intent.putExtra("message","Your resource " + title + " has confirmed by our team.");
        intent.putExtra("activity","ActivityResources.class");

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("Resource Confirmed")
                .setContentText("Your resource " + title + " has confirmed by our team.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1, mBuilder.build());
    }



    private static class ResViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mStatus;

        public ResViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.resource_name);
            mStatus = itemView.findViewById(R.id.resource_status);
        }


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
