package com.feelthecoder.dsc.QuizActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.feelthecoder.dsc.Model.HistoryModel;
import com.feelthecoder.dsc.NotificationActivity;
import com.feelthecoder.dsc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class QuizResultActivity extends AppCompatActivity {

    PieChartView pieChartView;
    List<SliceValue> pieData = new ArrayList<>();
    PieChartData pieChartData;
    int total = 5;
    int count_correct = 0;
    DatabaseReference dRef;
    DatabaseReference aRef;


    TextView congo;
    TextView points;

    int dp_points;
    int game_won;
    String quiz;
    String victory;
    Button btn;


    private static final String CHANNEL_ID="QUIZ";
    private static final String CHANNEL_NAME="Quiz";
    private static final String CHANNEL_DESC="This Channel will show notifications when a quiz get completed.";




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
        setContentView(R.layout.activity_quiz_result);

        pieChartView = findViewById(R.id.pro_result_quiz);
        btn = findViewById(R.id.play_again);
        congo = findViewById(R.id.pro_congo);
        points = findViewById(R.id.pro_score);
        Toolbar toolbar = findViewById(R.id.tool_result_quiz);





        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Quiz Report");

        dRef = FirebaseDatabase.getInstance().getReference("QuizDataHistory");
        aRef = FirebaseDatabase.getInstance().getReference("Achievment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        count_correct = (int) Objects.requireNonNull(getIntent().getExtras()).get("score");
        quiz=  Objects.requireNonNull(getIntent().getExtras().get("name")).toString();

        pieData.add(new SliceValue(count_correct, Color.GREEN).setLabel("Correct"));
        pieData.add(new SliceValue(total - count_correct, Color.RED).setLabel("Wrong"));
        pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);
        pieChartData.setHasCenterCircle(true);
        pieChartView.setPieChartData(pieChartData);


        //Creating Notification Channel for below oreo versions
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


      aRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.hasChild("dp")) {
                  String game = Objects.requireNonNull(dataSnapshot.child("quizwon").getValue()).toString();
                  String points = Objects.requireNonNull(dataSnapshot.child("dp").getValue()).toString();
                  game_won = Integer.parseInt(game);
                  dp_points = Integer.parseInt(points);
                  if (count_correct >= 3) {
                      int po=dp_points+100;
                      int gw=game_won+1;
                      aRef.child("dp").setValue("" + po);
                      aRef.child("quizwon").setValue("" +gw);


                  } else {
                      int po=dp_points-120;
                      aRef.child("dp").setValue("" + po);

                  }
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });

        if (count_correct >= 3) {
            congo.setText("You Win");
            congo.setTextColor(Color.GREEN);
            points.setText("+100 DP");
            points.setTextColor(Color.GREEN);
            victory="Won";
        } else {
            congo.setText("You Lost");
            congo.setTextColor(Color.RED);
            points.setText("-120 DP");
            victory="Lost";
        }


        saveDataToFirebase();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuizResultActivity.this, CategoryActivity.class));
                finish();

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    void saveDataToFirebase() {
        String score= String.valueOf(count_correct);
        String date;
        date = SimpleDateFormat.getDateInstance().format(new Date());
        HistoryModel his = new HistoryModel(score,date,quiz,victory);
        dRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(String.valueOf(System.currentTimeMillis())).setValue(his).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(QuizResultActivity.this, "Quiz saved to My Quiz", Toast.LENGTH_SHORT).show();
                    displayNotification(quiz,victory,score);
                }
            }
        });

    }

    private void displayNotification(String quiz, String victory, String score) {

        Intent intent= new Intent(getApplicationContext(), NotificationActivity.class);
        intent.putExtra("title","Quiz Result");
        intent.putExtra("message","You have "+victory+" "+quiz+" Challenge and you have answered "+score+" correctly. Do practice by playing more challenges to get more Developer Points.");
        intent.putExtra("activity","MyQuiz.class");

        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("Quiz Result")
                .setContentText("You have "+victory+" "+quiz+" Challenge and you have answered "+score+" correctly. Do practice by playing more challenges to get more Developer Points.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(1,mBuilder.build());
    }


}
