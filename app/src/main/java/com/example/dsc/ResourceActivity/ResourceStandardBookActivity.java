package com.example.dsc.ResourceActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsc.ActivityPDF;
import com.example.dsc.Model.ResultGet;
import com.example.dsc.NotificationActivity;
import com.example.dsc.R;
import com.example.dsc.ViewHolder.ViewSpace;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResourceStandardBookActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textView;
    DatabaseReference dRef;
    String title;
    ProgressBar progressBar;


    private static final String CHANNEL_ID="BOOK";
    private static final String CHANNEL_NAME="EDUCATIONB";
    private static final String CHANNEL_DESC="This Channel will show notifications when a book download completed.";


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
        setContentView(R.layout.activity_resource_standard_book);
        Toolbar toolbar = findViewById(R.id.tool_resource_book);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Standard Books");

        recyclerView = findViewById(R.id.recycler_resource_book);
        progressBar=findViewById(R.id.book_progress);
        progressBar.setVisibility(View.VISIBLE);

        //Creating Notification Channel for below oreo versions
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        textView = findViewById(R.id.resourcebook_text);
        title = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("title")).toString();
        dRef = FirebaseDatabase.getInstance().getReference("Education").child(title).child("books");
        LinearLayoutManager li = new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);
        textView.setVisibility(View.VISIBLE);

        getBooksDataFromFirebase();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getBooksDataFromFirebase() {

        FirebaseRecyclerOptions<ResultGet> options =
                new FirebaseRecyclerOptions.Builder<ResultGet>()
                        .setQuery(dRef, ResultGet.class).build();

        FirebaseRecyclerAdapter<ResultGet, ResourceStandardBookActivity.BooksViewHolder> adapter = new FirebaseRecyclerAdapter<ResultGet, ResourceStandardBookActivity.BooksViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final ResourceStandardBookActivity.BooksViewHolder booksViewHolder, int i, @NonNull ResultGet resultGet) {
                final String bookID = getRef(i).getKey();
                assert bookID != null;
                dRef.child(bookID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("link")) {
                            textView.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            final String caption = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("link").getValue()).toString();


                            booksViewHolder.mTitle.setText(caption);
                            booksViewHolder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(ResourceStandardBookActivity.this, ActivityPDF.class);
                                intent.putExtra("link", link);
                                startActivity(intent);
                            });
                            booksViewHolder.look.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ResourceStandardBookActivity.this, ActivityPDF.class);
                                    intent.putExtra("link", link);
                                    startActivity(intent);
                                }
                            });

                            booksViewHolder.down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (ContextCompat.checkSelfPermission(ResourceStandardBookActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                                            ContextCompat.checkSelfPermission(ResourceStandardBookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(ResourceStandardBookActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                                        ActivityCompat.requestPermissions(ResourceStandardBookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                                        Toast.makeText(ResourceStandardBookActivity.this, "DSC Needs Permission to download this book.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ResourceStandardBookActivity.this, "Downloading book...", Toast.LENGTH_SHORT).show();
                                        new DownloadFile().execute(link, caption+".pdf");
                                        Toast.makeText(ResourceStandardBookActivity.this, "Book downloaded to /DSC/PDF", Toast.LENGTH_SHORT).show();
                                        displayNotification();
                                    }
                                }
                            });
                        }else
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(ResourceStandardBookActivity.this, "There are no resources available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ResourceStandardBookActivity.BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_resourcelist, parent, false);
                ResourceStandardBookActivity.BooksViewHolder booksViewHolder = new ResourceStandardBookActivity.BooksViewHolder(view);
                return booksViewHolder;
            }
        };


        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }

    private static class BooksViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;
        Button look, down;

        public BooksViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.books_resource_name);
            look = itemView.findViewById(R.id.view_book_resource);
            down = itemView.findViewById(R.id.download_book_resource);
        }

    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   //
            String fileName = strings[1];  //
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "DSC/PDF");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

    private void displayNotification() {

        Intent intent= new Intent(getApplicationContext(), NotificationActivity.class);
        intent.putExtra("title","Book Downloaded");
        intent.putExtra("message","Your book downloaded. Click to view");
        intent.putExtra("activity","ResourceStandardBookActivity.class");

        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("Book Downloaded")
                .setContentText("Your book downloaded. Click to view")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(1,mBuilder.build());
    }
}