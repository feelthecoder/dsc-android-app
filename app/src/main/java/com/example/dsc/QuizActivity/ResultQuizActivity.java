package com.example.dsc.QuizActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dsc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class ResultQuizActivity extends AppCompatActivity {


    String victory,date,quiz;
    int total_score=5;
    int score;

    PieChartView pieChartView;
    List<SliceValue> pieData = new ArrayList<>();
    PieChartData pieChartData;


    TextView congo;
    TextView points;
    Button btn;

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
        setContentView(R.layout.activity_result_quiz);

        score= Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("score")).toString());
        victory= Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("victory")).toString();
        date= Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("date")).toString();
        quiz= Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("quiz")).toString();



        pieChartView = findViewById(R.id.pro_result_pre);
        btn = findViewById(R.id.play_it_again);
        congo = findViewById(R.id.pro_cong);
        points = findViewById(R.id.pro_scor);

        Toolbar toolbar=findViewById(R.id.tool_result_pre);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(quiz+ " "+date);

        pieData.add(new SliceValue(score, Color.GREEN).setLabel("Correct"));
        pieData.add(new SliceValue(total_score-score, Color.RED).setLabel("Wrong"));
        pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);
        pieChartData.setHasCenterCircle(true);
        pieChartView.setPieChartData(pieChartData);

            congo.setText("You "+victory+" on "+date );
            congo.setTextSize(14);
            if(victory.equals("Won")){
                points.setText("+100 DP");
                points.setTextColor(Color.GREEN);
                congo.setTextColor(Color.GREEN);
            }else{
                points.setText("-120 DP");
                points.setTextColor(Color.RED);
                congo.setTextColor(Color.RED);
            }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(ResultQuizActivity.this, CategoryActivity.class);
                startActivity(in);
                finish();
                MyQuiz.fa.finish();

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}
