package com.example.dsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dsc.Model.Donate;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DonateHistoryActivity extends AppCompatActivity {
    
    TextView not;
    RecyclerView recyclerView;
    ProgressBar donate_progress;
    DatabaseReference dRef;
    

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
        setContentView(R.layout.activity_donate_history);
        Toolbar toolbar=findViewById(R.id.tool_donate);
        donate_progress=findViewById(R.id.donate_progress);
        donate_progress.setVisibility(View.VISIBLE);
        recyclerView=findViewById(R.id.donate_re);
        not=findViewById(R.id.no_donation_here);
        dRef= FirebaseDatabase.getInstance().getReference("Donation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Donation Box");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        not.setVisibility(View.VISIBLE);
        loadDonationFromFirebase();

        LinearLayoutManager lin = new LinearLayoutManager(getApplicationContext());
        lin.setStackFromEnd(true);
         recyclerView.setLayoutManager(lin);
         recyclerView.setHasFixedSize(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadDonationFromFirebase() {
        FirebaseRecyclerOptions<Donate> options =
                new FirebaseRecyclerOptions.Builder<Donate>()
                        .setQuery(dRef, Donate.class).build();

        FirebaseRecyclerAdapter<Donate, DonateHistoryActivity.DonateViewHolder> adapter = new FirebaseRecyclerAdapter<Donate, DonateHistoryActivity.DonateViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final DonateHistoryActivity.DonateViewHolder donateViewHolder, int i, @NonNull Donate donate) {
                final String donateID = getRef(i).getKey();
                assert donateID != null;
                dRef.child(donateID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("payment")) {
                            not.setVisibility(View.INVISIBLE);
                            donate_progress.setVisibility(View.INVISIBLE);
                            final String tID = Objects.requireNonNull(dataSnapshot.child("tID").getValue()).toString();
                            final String rID = Objects.requireNonNull(dataSnapshot.child("rID").getValue()).toString();
                            final String payment = Objects.requireNonNull(dataSnapshot.child("payment").getValue()).toString();
                            final String date = Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();
                            final String status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                            final String approval = Objects.requireNonNull(dataSnapshot.child("approval").getValue()).toString();
                            final String mail = Objects.requireNonNull(dataSnapshot.child("mail_sent").getValue()).toString();



                            donateViewHolder.mDate.setText(date);
                            donateViewHolder.mPayment.setText("₹"+payment);
                            donateViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent= new Intent(DonateHistoryActivity.this,ShowDonation.class);
                                    intent.putExtra("tID",tID);
                                    intent.putExtra("rID",rID);
                                    intent.putExtra("payment",payment);
                                    intent.putExtra("date",date);
                                    intent.putExtra("status",status);
                                    intent.putExtra("aID",approval);
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
            public DonateHistoryActivity.DonateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donate_list_layout, parent, false);
                DonateHistoryActivity.DonateViewHolder donateViewHolder = new DonateHistoryActivity.DonateViewHolder(view);
                return donateViewHolder;
            }
        };

        donate_progress.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }

    private static class DonateViewHolder extends RecyclerView.ViewHolder {

        TextView mDate,mPayment;

        public DonateViewHolder(@NonNull View itemView) {
            super(itemView);
            mDate = itemView.findViewById(R.id.txs_date);
            mPayment=itemView.findViewById(R.id.txs_amount);
        }

    }

}
