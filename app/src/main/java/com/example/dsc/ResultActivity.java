package com.example.dsc;

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

import com.example.dsc.Model.ResultGet;
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

import static android.view.View.GONE;

public class ResultActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference dRef;
     TextView connect;
     TextView txtResult;
     ProgressBar resultProgress;


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
        setContentView(R.layout.activity_result);

        resultProgress=findViewById(R.id.result_progress);
        Toolbar toolbar=findViewById(R.id.tool_result);
        connect=findViewById(R.id.co_rel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        txtResult=findViewById(R.id.no_result_here);
        txtResult.setVisibility(View.VISIBLE);
        dRef = FirebaseDatabase.getInstance().getReference("Results");
        recyclerView = findViewById(R.id.result_recycler);
        LinearLayoutManager li= new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);

        if(isOnline()) {
            resultProgress.setVisibility(View.VISIBLE);

            getMyResults();
            connect.setVisibility(View.INVISIBLE);
        }
        else
        {
            txtResult.setVisibility(GONE);
            connect.setVisibility(View.VISIBLE);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void getMyResults() {

        FirebaseRecyclerOptions<ResultGet> options =
                new FirebaseRecyclerOptions.Builder<ResultGet>()
                        .setQuery(dRef, ResultGet.class).build();

        FirebaseRecyclerAdapter<ResultGet, ResultActivity.ResultViewHolder> adapter = new FirebaseRecyclerAdapter<ResultGet, ResultActivity.ResultViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final ResultActivity.ResultViewHolder resultViewHolder, int i, @NonNull ResultGet resultGet) {
                final String resultID = getRef(i).getKey();
                assert resultID != null;
                dRef.child(resultID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("link")) {
                            txtResult.setVisibility(View.INVISIBLE);
                            final String resultCaption = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("link").getValue()).toString();

                            resultProgress.setVisibility(View.INVISIBLE);

                            resultViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(ResultActivity.this, ActivityPDF.class);
                                    intent.putExtra("link",link);
                                    startActivity(intent);
                                }
                            });
                            resultViewHolder.mTitle.setText(resultCaption);
                        }
                        else
                        {
                            resultProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(ResultActivity.this, "Results data not available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ResultActivity.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_layout, parent, false);
                ResultActivity.ResultViewHolder resultViewHolder=new ResultActivity.ResultViewHolder(view);
                return resultViewHolder;
            }
        };


        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }


    private static class ResultViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.result_name);
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
}
