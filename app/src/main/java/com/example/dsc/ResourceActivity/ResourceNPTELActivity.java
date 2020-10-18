package com.example.dsc.ResourceActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dsc.ActivityWeb;
import com.example.dsc.Model.ResultGet;
import com.example.dsc.R;
import com.example.dsc.ViewHolder.ViewSpace;
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

public class ResourceNPTELActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textView;
    DatabaseReference dRef;
    String title;

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
        setContentView(R.layout.activity_resource_n_p_t_e_l);
        Toolbar toolbar=findViewById(R.id.tool_resource_nptel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("NPTEL Lectures");
        recyclerView=findViewById(R.id.recycler_resource_nptel);
        LinearLayoutManager li= new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);
        textView=findViewById(R.id.resourcenptel_text);
        title=getIntent().getExtras().get("title").toString();
        dRef= FirebaseDatabase.getInstance().getReference("Education").child(title).child("nptel");
        textView.setVisibility(View.VISIBLE);

        getNPTELDataFromFirebase();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getNPTELDataFromFirebase() {

        FirebaseRecyclerOptions<ResultGet> options =
                new FirebaseRecyclerOptions.Builder<ResultGet>()
                        .setQuery(dRef, ResultGet.class).build();

        FirebaseRecyclerAdapter<ResultGet,ResourceNPTELActivity.NPTELViewHolder> adapter = new FirebaseRecyclerAdapter<ResultGet,ResourceNPTELActivity.NPTELViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final ResourceNPTELActivity.NPTELViewHolder nPTELViewHolder, int i, @NonNull ResultGet resultGet) {
                final String nptelID = getRef(i).getKey();
                assert nptelID != null;
                dRef.child(nptelID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("link")) {
                            textView.setVisibility(View.INVISIBLE);
                            final String caption = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("link").getValue()).toString();


                            nPTELViewHolder.mTitle.setText(caption);
                            nPTELViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ResourceNPTELActivity.this, ActivityWeb.class);
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
            public ResourceNPTELActivity.NPTELViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nptel_resourcelist, parent, false);
                ResourceNPTELActivity.NPTELViewHolder nPTELViewHolder = new ResourceNPTELActivity.NPTELViewHolder(view);
                return nPTELViewHolder;
            }
        };


        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();
    }


    class NPTELViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;

        public NPTELViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.nptel_resource_name);
        }

    }
}


