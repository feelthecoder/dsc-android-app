package com.feelthecoder.dsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feelthecoder.dsc.Model.ImageGet;
import com.feelthecoder.dsc.ResourceActivity.FileDownloader;
import com.feelthecoder.dsc.ViewHolder.ViewSpace;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewSpandanActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference dRef;
    String addLink;
    TextView txt_spandan;

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
        setContentView(R.layout.activity_view_spandan);
        addLink=getIntent().getExtras().get("link").toString();
        Toolbar toolbar =findViewById(R.id.tool_prev);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(addLink);
        txt_spandan=findViewById(R.id.txt_spandan);
        txt_spandan.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.recycler_view_prev);
        dRef= FirebaseDatabase.getInstance().getReference("SpandanResources").child(addLink);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        getImagesFromFirebase();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getImagesFromFirebase() {

        FirebaseRecyclerOptions<ImageGet> options =
                new FirebaseRecyclerOptions.Builder<ImageGet>()
                        .setQuery(dRef, ImageGet.class).build();

        FirebaseRecyclerAdapter<ImageGet, ViewSpandanActivity.ImageViewHolder> adapter = new FirebaseRecyclerAdapter<ImageGet, ViewSpandanActivity.ImageViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final ViewSpandanActivity.ImageViewHolder imageViewHolder, int i, @NonNull ImageGet imageGet) {
                final String imageID = getRef(i).getKey();
                assert imageID != null;
                dRef.child(imageID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("title")) {
                            txt_spandan.setVisibility(View.INVISIBLE);
                            final String title = Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString();
                            final String pic = Objects.requireNonNull(dataSnapshot.child("pic").getValue()).toString();


                            imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(ViewSpandanActivity.this,ViewImageActivity.class);
                                    intent.putExtra("link",pic);
                                    startActivity(intent);
                                }
                            });
                            imageViewHolder.download.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(ViewSpandanActivity.this, "Downloading image...", Toast.LENGTH_SHORT).show();
                                    new ViewSpandanActivity.DownloadFile().execute(pic, title+".jpg");
                                    Toast.makeText(ViewSpandanActivity.this, "Image Downloaded and saved to gallery (DSC/Spandan Images).", Toast.LENGTH_SHORT).show();
                                }
                            });
                            imageViewHolder.mTitle.setText(title);
                            Glide.with(getApplicationContext()).load(pic).into(imageViewHolder.img);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ViewSpandanActivity.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_layout, parent, false);
                ViewSpandanActivity.ImageViewHolder imageViewHolder=new ViewSpandanActivity.ImageViewHolder(view);
                return imageViewHolder;
            }
        };


        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();
        
    }
    private static class ImageViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;
        ImageView img;
        Button download;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.name_spa);
            download=itemView.findViewById(R.id.down_image);
            img=itemView.findViewById(R.id.image_spa);
        }


    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   //
            String fileName = strings[1];  //
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "DSC/Spandan Images");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }


}
