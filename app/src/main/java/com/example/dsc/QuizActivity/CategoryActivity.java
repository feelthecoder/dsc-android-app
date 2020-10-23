package com.example.dsc.QuizActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsc.Model.CatSample;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference dRef;
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
        setContentView(R.layout.activity_category);

        fa=this;

        Toolbar toolbar=findViewById(R.id.tool_categ);
        recyclerView=findViewById(R.id.recategory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Category");

        dRef= FirebaseDatabase.getInstance().getReference("QuizData").child("Category");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);


        getCategory();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getCategory() {
        FirebaseRecyclerOptions<CatSample> options =
                new FirebaseRecyclerOptions.Builder<CatSample>()
                        .setQuery(dRef, CatSample.class).build();

        FirebaseRecyclerAdapter<CatSample, CategoryActivity.CategoryViewHolder> adapter = new FirebaseRecyclerAdapter<CatSample, CategoryActivity.CategoryViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final CategoryActivity.CategoryViewHolder categoryViewHolder, final int i, @NonNull final CatSample catSample) {
                final String catID = getRef(i).getKey();
                assert catID != null;
                dRef.child(catID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("category")) {
                            final String category = Objects.requireNonNull(dataSnapshot.child("category").getValue()).toString();
                            final String nam = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String image_link = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();

                            String name=nam.toLowerCase().replace(" ","").replace("++","plusplus").replace("#","sharp");

                            categoryViewHolder.mCategory.setText(category);
                            Glide.with(CategoryActivity.this).load(image_link).override(200,200).into(categoryViewHolder.img);

                            categoryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(CategoryActivity.this,StartQuizActivity.class);
                                    intent.putExtra("qa_database",name);
                                    intent.putExtra("link",image_link);
                                    intent.putExtra("category",category);
                                    startActivity(intent);

                                }
                            });



                        }
                        else{
                            Toast.makeText(CategoryActivity.this, "Quiz Not available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public CategoryActivity.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_layout,parent,false);
                return new CategoryActivity.CategoryViewHolder(view);
            }
        };


        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }


    private static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView mCategory;
        ImageView img;



        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mCategory = itemView.findViewById(R.id.cat_quiz_it);
            img=itemView.findViewById(R.id.image_cat);


        }

    }
}
