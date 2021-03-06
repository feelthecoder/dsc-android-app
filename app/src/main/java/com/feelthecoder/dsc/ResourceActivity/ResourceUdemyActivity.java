package com.feelthecoder.dsc.ResourceActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feelthecoder.dsc.ActivityWeb;
import com.feelthecoder.dsc.Model.ResultGet;
import com.feelthecoder.dsc.R;
import com.feelthecoder.dsc.ViewHolder.ViewSpace;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResourceUdemyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textView;
    DatabaseReference dRef;
    String title;
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
        setContentView(R.layout.activity_resource_udemy);
        Toolbar toolbar = findViewById(R.id.tool_resource_udemy);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Free Udemy Courses");
        recyclerView = findViewById(R.id.recycler_resource_udemy);
        progressBar=findViewById(R.id.udemy_progress);
        progressBar.setVisibility(View.VISIBLE);
        textView = findViewById(R.id.resourceudemy_text);
        title = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("title")).toString();
        dRef = FirebaseDatabase.getInstance().getReference("Education").child(title).child("udemy");
        textView.setVisibility(View.VISIBLE);
        LinearLayoutManager li= new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);

        getUdemyDataFromFirebase();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getUdemyDataFromFirebase() {

        FirebaseRecyclerOptions<ResultGet> options =
                new FirebaseRecyclerOptions.Builder<ResultGet>()
                        .setQuery(dRef, ResultGet.class).build();

        FirebaseRecyclerAdapter<ResultGet,ResourceUdemyActivity.UdemyViewHolder> adapter = new FirebaseRecyclerAdapter<ResultGet,ResourceUdemyActivity.UdemyViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final ResourceUdemyActivity.UdemyViewHolder udemyViewHolder, int i, @NonNull ResultGet resultGet) {
                final String udemyID = getRef(i).getKey();
                assert udemyID != null;
                dRef.child(udemyID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("link")) {
                            textView.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            final String caption = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("link").getValue()).toString();


                            udemyViewHolder.mTitle.setText(caption);
                            udemyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ResourceUdemyActivity.this, ActivityWeb.class);
                                    intent.putExtra("link", link);
                                    startActivity(intent);
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
            public ResourceUdemyActivity.UdemyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.udemy_resourcelist, parent, false);
                ResourceUdemyActivity.UdemyViewHolder udemyViewHolder = new ResourceUdemyActivity.UdemyViewHolder(view);
                return udemyViewHolder;
            }
        };

        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }

    private static class UdemyViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;

        public UdemyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.udemy_resource_name);
        }

    }
}

