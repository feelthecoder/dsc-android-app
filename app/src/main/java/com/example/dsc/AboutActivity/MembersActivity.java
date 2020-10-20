package com.example.dsc.AboutActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsc.Model.MembersInfo;
import com.example.dsc.R;
import com.example.dsc.ViewHolder.ViewSpace;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;

public class MembersActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference dRef;
    TextView connect;
    ProgressBar members_progress;


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
        setContentView(R.layout.activity_members);
        Toolbar toolbar = findViewById(R.id.tool_members);
        members_progress=findViewById(R.id.member_progress);


        recyclerView = findViewById(R.id.members_recycler_form);
        LinearLayoutManager li= new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);

        connect = findViewById(R.id.no_connect_members_ee);





        dRef = FirebaseDatabase.getInstance().getReference("About").child("Members");
        if (isOnline()) {
            members_progress.setVisibility(View.VISIBLE);
            getBoardInfo();
            connect.setVisibility(View.INVISIBLE);
        } else {
            connect.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Members");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getBoardInfo() {
        FirebaseRecyclerOptions<MembersInfo> options =
                new FirebaseRecyclerOptions.Builder<MembersInfo>()
                        .setQuery(dRef, MembersInfo.class).build();

        FirebaseRecyclerAdapter<MembersInfo, MembersViewHolder> adapter = new FirebaseRecyclerAdapter<MembersInfo, MembersActivity.MembersViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final MembersActivity.MembersViewHolder membersViewHolder, int i, @NonNull MembersInfo membersInfo) {
                final String memID = getRef(i).getKey();
                assert memID != null;
                dRef.child(memID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("name")) {
                            members_progress.setVisibility(View.INVISIBLE);
                            final String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String post = Objects.requireNonNull(dataSnapshot.child("post").getValue()).toString();
                            final String skills = Objects.requireNonNull(dataSnapshot.child("skills").getValue()).toString();
                            final String sskills = Objects.requireNonNull(dataSnapshot.child("specia").getValue()).toString();
                            final String git = Objects.requireNonNull(dataSnapshot.child("git").getValue()).toString();
                            final String fb = Objects.requireNonNull(dataSnapshot.child("fb").getValue()).toString();
                            final String linkdin = Objects.requireNonNull(dataSnapshot.child("linkdin").getValue()).toString();
                            final String hackerrank = Objects.requireNonNull(dataSnapshot.child("hackerrank").getValue()).toString();
                            final String insta = Objects.requireNonNull(dataSnapshot.child("insta").getValue()).toString();
                            final String bio = Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString();
                            final String profile_pic= Objects.requireNonNull(dataSnapshot.child("dp").getValue()).toString();
                            final String projects= Objects.requireNonNull(dataSnapshot.child("projects").getValue()).toString();


                            membersViewHolder.mName.setText(name);
                            membersViewHolder.mPost.setText(sskills);
                            Glide.with(getApplicationContext()).load(profile_pic).into(membersViewHolder.mProfile);
                            membersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(MembersActivity.this, ProfileActivity.class);
                                    intent.putExtra("name",name);
                                    intent.putExtra("post",post);
                                    intent.putExtra("skills",skills);
                                    intent.putExtra("special",sskills);
                                    intent.putExtra("github",git);
                                    intent.putExtra("fb",fb);
                                    intent.putExtra("linkdin",linkdin);
                                    intent.putExtra("hackerrank",hackerrank);
                                    intent.putExtra("insta",insta);
                                    intent.putExtra("bio",bio);
                                    intent.putExtra("profile",profile_pic);
                                    intent.putExtra("projects",projects);
                                    startActivity(intent);
                                }
                            });



                            setGuide("Members","You can visit profile, Click to see.",membersViewHolder.itemView,"MEMBERS");
                        }else
                        {
                            Toast.makeText(MembersActivity.this, "There are no members", Toast.LENGTH_SHORT).show();
                            members_progress.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public MembersActivity.MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memers_list_layout, parent, false);
                MembersActivity.MembersViewHolder membersViewHolder = new MembersActivity.MembersViewHolder(view);
                return membersViewHolder;
            }
        };


        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }

    private static class MembersViewHolder extends RecyclerView.ViewHolder {

        TextView mName,mPost;
        CircularImageView mProfile;

        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfile = itemView.findViewById(R.id.img_1_mem);
            mName = itemView.findViewById(R.id.text_1_mem);
            mPost = itemView.findViewById(R.id.post_1_mem);
        }

    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;

    }

    private void setGuide(String name,String msg,View view,String key){
        SharedPreferences mPrefs=getSharedPreferences("MyGuide",0);
        String is=mPrefs.getString(key,"no");
        if(is.equals("no")){
            new GuideView.Builder(this)
                    .setTitle(name)
                    .setContentText(msg)
                    .setTargetView(view)
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build()
                    .show();

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(key,"yes");
            editor.apply();
        }
    }
}
