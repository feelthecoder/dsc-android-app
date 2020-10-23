package com.feelthecoder.dsc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feelthecoder.dsc.Model.LikeModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
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

public class PeopleLikedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String postID;
    DatabaseReference likeRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences mPrefs=getSharedPreferences("MyPrefs",0);
        String is=mPrefs.getString("mode","not");
        assert is != null;
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES||is.equals("dark")){
            setTheme(R.style.DarkTheme);

        }
        else
        {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_liked);
        Toolbar toolbar = findViewById(R.id.tool_liked);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });
        postID= Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("post")).toString();
        recyclerView = findViewById(R.id.liked_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        likeRef = FirebaseDatabase.getInstance().getReference("MainData").child("TechPosts").child(postID).child("LIKES");
        getLikesFromFirebase();
}

    private void getLikesFromFirebase() {
        FirebaseRecyclerOptions<LikeModel> options =
                new FirebaseRecyclerOptions.Builder<LikeModel>()
                        .setQuery(likeRef, LikeModel.class).build();

        FirebaseRecyclerAdapter<LikeModel,PeopleLikedActivity.LikeViewHolder> adapter= new FirebaseRecyclerAdapter<LikeModel,PeopleLikedActivity.LikeViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final LikeViewHolder likeViewHolder, int i, @NonNull final LikeModel likeModel) {
                final String likeID = getRef(i).getKey();
                assert likeID != null;
                likeRef.child(likeID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("Image")) {
                            String namee= Objects.requireNonNull(dataSnapshot.child("Name").getValue()).toString();
                            String profile= Objects.requireNonNull(dataSnapshot.child("Image").getValue()).toString();


                            likeViewHolder.name.setText(namee);
                            Glide.with(getApplicationContext()).load(profile).into(likeViewHolder.profile);

                            likeViewHolder.parent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Snackbar snackbar = Snackbar
                                            .make(likeViewHolder.parent, "This is Liked"+ " by "+namee, Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public PeopleLikedActivity.LikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_list_layout, parent, false);
                PeopleLikedActivity.LikeViewHolder likeViewHolder= new LikeViewHolder(view);
                return likeViewHolder;
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


private static class LikeViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    CircularImageView profile;
    RelativeLayout parent;


    LikeViewHolder(@NonNull View itemView) {
        super(itemView);

        parent = itemView.findViewById(R.id.card_like);
        name = itemView.findViewById(R.id.like_name);
        profile = itemView.findViewById(R.id.like_profile);
    }

}

    @Override
    public void onBackPressed() {
        this.finish();
    }
}




