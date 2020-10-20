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

import com.example.dsc.Model.RegistrationForm;
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

public class FormsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference dRef;
    TextView connect;
    TextView txtForm;

    ProgressBar formsProgress;



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
        setContentView(R.layout.activity_forms);
        Toolbar toolbar = findViewById(R.id.tool_formss);

        formsProgress=findViewById(R.id.progress_forms);
        formsProgress.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.forms_recycler_form);
       LinearLayoutManager li= new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);

        connect = findViewById(R.id.no_connect_form_ee);
        txtForm=findViewById(R.id.no_form_here);
        txtForm.setVisibility(View.VISIBLE);


        dRef = FirebaseDatabase.getInstance().getReference("UserFormHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (isOnline()) {
            getFormsHistory();
            connect.setVisibility(View.INVISIBLE);
        } else {
            txtForm.setVisibility(View.GONE);
            connect.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getFormsHistory() {
        FirebaseRecyclerOptions<RegistrationForm> options =
                new FirebaseRecyclerOptions.Builder<RegistrationForm>()
                        .setQuery(dRef, RegistrationForm.class).build();

        FirebaseRecyclerAdapter<RegistrationForm, FormsActivity.FormsViewHolder> adapter = new FirebaseRecyclerAdapter<RegistrationForm, FormsActivity.FormsViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final FormsActivity.FormsViewHolder formsViewHolder, int i, @NonNull RegistrationForm registrationForm) {
                final String formID = getRef(i).getKey();
                assert formID != null;
                dRef.child(formID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("email_")) {
                            txtForm.setVisibility(View.INVISIBLE);
                            formsProgress.setVisibility(View.INVISIBLE);
                            final String category = Objects.requireNonNull(dataSnapshot.child("category").getValue()).toString();
                            final String date = Objects.requireNonNull(dataSnapshot.child("date_").getValue()).toString();
                            final String tName = Objects.requireNonNull(dataSnapshot.child("tName").getValue()).toString();
                            final String branch = Objects.requireNonNull(dataSnapshot.child("branch_").getValue()).toString();
                            final String college = Objects.requireNonNull(dataSnapshot.child("college_").getValue()).toString();
                            final String course = Objects.requireNonNull(dataSnapshot.child("course_").getValue()).toString();
                            final String email = Objects.requireNonNull(dataSnapshot.child("email_").getValue()).toString();
                            final String mobile = Objects.requireNonNull(dataSnapshot.child("mobile_").getValue()).toString();
                            final String name = Objects.requireNonNull(dataSnapshot.child("name_").getValue()).toString();
                            final String year = Objects.requireNonNull(dataSnapshot.child("year_").getValue()).toString();
                            final String code= Objects.requireNonNull(dataSnapshot.child("code_").getValue()).toString();



                            formsViewHolder.mTitle.setText(tName);
                            formsViewHolder.mType.setText(category);
                            formsViewHolder.mDate.setText(date);
                            formsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(FormsActivity.this,ShowFormActivity.class);
                                    intent.putExtra("name",name);
                                    intent.putExtra("college",college);
                                    intent.putExtra("branch",branch);
                                    intent.putExtra("eventName",tName);
                                    intent.putExtra("year",year);
                                    intent.putExtra("mobile",mobile);
                                    intent.putExtra("course",course);
                                    intent.putExtra("email",email);
                                    intent.putExtra("category",category);
                                    intent.putExtra("date",date);
                                    intent.putExtra("code",code);
                                    startActivity(intent);
                                }
                            });

                        }
                        else
                        {
                            formsProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(FormsActivity.this, "You have not submitted any form", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FormsActivity.FormsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_list_layout, parent, false);
                FormsActivity.FormsViewHolder formsViewHolder = new FormsActivity.FormsViewHolder(view);
                return formsViewHolder;
            }
        };


        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }

    private static class FormsViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mType, mDate;

        public FormsViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.form_name_submitted);
            mType = itemView.findViewById(R.id.form_type);
            mDate = itemView.findViewById(R.id.date_submitted);
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
}
