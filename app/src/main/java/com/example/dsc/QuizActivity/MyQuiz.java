package com.example.dsc.QuizActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsc.Model.HistoryModel;
import com.example.dsc.R;
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

public class MyQuiz extends AppCompatActivity {
    
    DatabaseReference hRef;
    RecyclerView recyclerView;
   public static Activity fa;
   RelativeLayout not_avail;
   ProgressBar quiz_proogress;
   Boolean x=true;

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
        setContentView(R.layout.activity_my_quiz);
        recyclerView=findViewById(R.id.record_quiz_pre);
        quiz_proogress=findViewById(R.id.quiz_progress);
        not_avail=findViewById(R.id.not_avail);
        quiz_proogress.setVisibility(View.VISIBLE);
        fa=this;
        hRef= FirebaseDatabase.getInstance().getReference("QuizDataHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Toolbar toolbar=findViewById(R.id.tool_rec_quiz);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Quiz Played");
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager li= new LinearLayoutManager(getApplicationContext());
        li.setReverseLayout(true);
        li.setStackFromEnd(true);
        recyclerView.setLayoutManager(li);

            not_avail.setVisibility(View.VISIBLE);
            getMyQuiz();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void getMyQuiz() {

        FirebaseRecyclerOptions<HistoryModel> options =
                new FirebaseRecyclerOptions.Builder<HistoryModel>()
                        .setQuery(hRef, HistoryModel.class).build();

        FirebaseRecyclerAdapter<HistoryModel, MyQuiz.QuizViewHolder> adapter = new FirebaseRecyclerAdapter<HistoryModel, MyQuiz.QuizViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final MyQuiz.QuizViewHolder quizViewHolder, int i, @NonNull HistoryModel HistoryModel) {
                final String quizID = getRef(i).getKey();
                assert quizID != null;
                hRef.child(quizID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("quiz")) {
                            quiz_proogress.setVisibility(View.INVISIBLE);
                            not_avail.setVisibility(View.INVISIBLE);
                             String quiz = Objects.requireNonNull(dataSnapshot.child("quiz").getValue()).toString();
                            String score = Objects.requireNonNull(dataSnapshot.child("correct").getValue()).toString();
                            String date = Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();
                            String victory= Objects.requireNonNull(dataSnapshot.child("victory").getValue()).toString();


                            quizViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(MyQuiz.this, ResultQuizActivity.class);
                                    intent.putExtra("quiz",quiz);
                                    intent.putExtra("victory",victory);
                                    intent.putExtra("date",date);
                                    intent.putExtra("score",score);
                                    startActivity(intent);
                                }
                            });

                            quizViewHolder.title.setText(quiz+" Challenge");
                            quizViewHolder.victory.setText(victory);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public MyQuiz.QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_list_layout, parent, false);
                return new QuizViewHolder(view);
            }
        };

        quiz_proogress.setVisibility(View.INVISIBLE);
        x=false;
        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }


    private static class QuizViewHolder extends RecyclerView.ViewHolder {

        TextView title,victory;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.qTitle);
            victory=itemView.findViewById(R.id.vTitle);

        }


    }


}
