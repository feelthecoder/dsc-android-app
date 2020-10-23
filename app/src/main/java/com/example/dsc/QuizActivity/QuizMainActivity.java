package com.example.dsc.QuizActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsc.Model.QA;
import com.example.dsc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class QuizMainActivity extends AppCompatActivity {
    String category, database_name;
    DatabaseReference dRef;
    public ArrayList<QA> new_q = new ArrayList<QA>();
    public ArrayList<QA> old_q = new ArrayList<QA>();
    ProgressBar progressBar;
    TextView question, question_no;
    RadioGroup radioGroup;
    Button btn;
    RadioButton opt1, opt2, opt3, opt4;
    String[] answers = new String[5];
    int currentQuestion = 0;
    int totalQuestions = 5;
    boolean answered = false;
    long timeLeftInMillis;
    ProgressDialog progressDialog;
    int count_correct = 0;

    CountDownTimer countDownTimer;

    boolean pressed = false;

    ArrayList<Integer> list = new ArrayList<Integer>();


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
        setContentView(R.layout.activity_quiz_main);

        Toolbar toolbar = findViewById(R.id.tool_main_quiz);
        progressBar = findViewById(R.id.progress_ques);
        btn = findViewById(R.id.sub_ans);
        question_no = findViewById(R.id.no_question_sample);
        question = findViewById(R.id.question_sample);
        radioGroup = findViewById(R.id.radio_option);
        opt1 = findViewById(R.id.option1);
        opt2 = findViewById(R.id.option2);
        opt3 = findViewById(R.id.option3);
        opt4 = findViewById(R.id.option4);

        old_q.clear();
        progressDialog = new ProgressDialog(QuizMainActivity.this);

        category = Objects.requireNonNull(getIntent().getExtras().get("name")).toString();
        database_name = Objects.requireNonNull(getIntent().getExtras().get("database")).toString();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(category + " Quiz Challenge");

        dRef = FirebaseDatabase.getInstance().getReference("QuizData").child("QA").child(database_name);

        startQuiz();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (opt1.isChecked() || opt2.isChecked() || opt3.isChecked() || opt4.isChecked()) {
                        storeAnswer();
                    } else {
                        Toast.makeText(QuizMainActivity.this, "Select a option.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    loadNextQuestionWithTimer();
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void storeAnswer() {
        answered = true;
        countDownTimer.cancel();
        int id = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(id);
        if (id == -1) {
            radioButton = opt1;
        }
        answers[currentQuestion - 1] = (String) radioButton.getText();
        if (answers[currentQuestion - 1].equals(new_q.get(list.get(currentQuestion - 1)).getCorrect_answer()))
            count_correct++;
        loadNextQuestionWithTimer();

    }

    private void startQuiz() {
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new_q.clear();
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    QA question = ds.getValue(QA.class);
                    new_q.add(question);
                    list.add(i++);
                }
                Collections.shuffle(list);
                loadNextQuestionWithTimer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadNextQuestionWithTimer() {
        radioGroup.clearCheck();
        if (currentQuestion < totalQuestions) {
            question.setText(new_q.get(list.get(currentQuestion)).getQuestion());
            opt1.setText(new_q.get(list.get(currentQuestion)).getOption1());
            opt2.setText(new_q.get(list.get(currentQuestion)).getOption2());
            opt3.setText(new_q.get(list.get(currentQuestion)).getOption3());
            opt4.setText(new_q.get(list.get(currentQuestion)).getOption4());
            question_no.setText(currentQuestion + 1 + "/5");
            old_q.add(new_q.get(list.get(currentQuestion)));
            answered = false;
            currentQuestion++;
            timeLeftInMillis = 15000;
            startTimer();
        } else {
            finishQuiz();
        }


    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateSlider();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateSlider();
                storeAnswer();
            }
        }.start();
    }

    private void updateSlider() {
        progressBar.setProgress((int) ((timeLeftInMillis / 100)));
        if (timeLeftInMillis < 500) {
            progressBar.setBackgroundColor(Color.RED);
        }

    }

    private void finishQuiz() {
        progressDialog.show(QuizMainActivity.this, "Submitting Quiz", "Please wait..", true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();

                Intent intent = new Intent(QuizMainActivity.this, QuizResultActivity.class);
                intent.putExtra("score", count_correct);
                intent.putExtra("name",category);

                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (pressed) {
            super.onBackPressed();
            return;
        }
        this.pressed=true;
        Toast.makeText(this, "Quiz got cancelled on exiting.Click again to exit.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                pressed=false;
            }
        }, 2000);
    }


}

