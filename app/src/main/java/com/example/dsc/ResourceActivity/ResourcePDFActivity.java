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

public class ResourcePDFActivity extends AppCompatActivity {
    String title;
    DatabaseReference dRef;
    RecyclerView recyclerView;
    TextView textView;
ProgressBar progressBar;

    private static final String CHANNEL_ID="PDF";
    private static final String CHANNEL_NAME="EDUCATION";
    private static final String CHANNEL_DESC="This Channel will show notifications when a pdf download completed.";




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
        setContentView(R.layout.activity_resource_p_d_f);
        Toolbar toolbar=findViewById(R.id.tool_resource_pdf);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("PDF Books");
progressBar=findViewById(R.id.pdf_progress);
progressBar.setVisibility(View.VISIBLE);

        //Creating Notification Channel for below oreo versions
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        recyclerView=findViewById(R.id.recycler_resource_pdf);
        LinearLayoutManager li= new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);
        textView=findViewById(R.id.resourcepdf_text);
        title=getIntent().getExtras().get("title").toString();
        dRef= FirebaseDatabase.getInstance().getReference("Education").child(title).child("pdf");
        textView.setVisibility(View.VISIBLE);
        
        getPDFDataFromFirebase();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getPDFDataFromFirebase() {

        FirebaseRecyclerOptions<ResultGet> options =
                new FirebaseRecyclerOptions.Builder<ResultGet>()
                        .setQuery(dRef, ResultGet.class).build();

        FirebaseRecyclerAdapter<ResultGet,ResourcePDFActivity.PDFViewHolder> adapter = new FirebaseRecyclerAdapter<ResultGet,ResourcePDFActivity.PDFViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final ResourcePDFActivity.PDFViewHolder pDFViewHolder, int i, @NonNull ResultGet resultGet) {
                final String pdfID = getRef(i).getKey();
                assert pdfID != null;
                dRef.child(pdfID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("link")) {
                            textView.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            final String caption = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("link").getValue()).toString();


                            pDFViewHolder.mTitle.setText(caption);
                            pDFViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(ResourcePDFActivity.this, ActivityPDF.class);
                                    intent.putExtra("link",link);
                                    startActivity(intent);
                                }
                            });
                            pDFViewHolder.view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(ResourcePDFActivity.this, ActivityPDF.class);
                                    intent.putExtra("link",link);
                                    startActivity(intent);
                                }
                            });
                            pDFViewHolder.down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ContextCompat.checkSelfPermission(ResourcePDFActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                                            ContextCompat.checkSelfPermission(ResourcePDFActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(ResourcePDFActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                                        ActivityCompat.requestPermissions(ResourcePDFActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                                        Toast.makeText(ResourcePDFActivity.this, "DSC Needs Permission to download this book.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ResourcePDFActivity.this, "Downloading pdf...", Toast.LENGTH_SHORT).show();
                                        new DownloadFile().execute(link, caption+".pdf");
                                        Toast.makeText(ResourcePDFActivity.this, "PDF downloaded to /DSC/PDF", Toast.LENGTH_SHORT).show();
                                        displayNotification();

                                    }
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
            public ResourcePDFActivity.PDFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_resourcelist, parent, false);
                ResourcePDFActivity.PDFViewHolder pDFViewHolder = new ResourcePDFActivity.PDFViewHolder(view);
                return pDFViewHolder;
            }
        };

        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }

    private static class PDFViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;
        Button view,down;

        public PDFViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.pdf_resource_name);
            view=itemView.findViewById(R.id.view_pdf_resource);
            down=itemView.findViewById(R.id.download_pdf_resource);
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

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

    private void displayNotification() {

        Intent intent= new Intent(getApplicationContext(), NotificationActivity.class);
        intent.putExtra("title","PDF Downloaded");
        intent.putExtra("message","Your pdf downloaded. Click to view");
        intent.putExtra("activity","ResourcePDFActivity.class");

        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("PDF Downloaded")
                .setContentText("Your pdf downloaded. Click to view")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(1,mBuilder.build());
    }

}
