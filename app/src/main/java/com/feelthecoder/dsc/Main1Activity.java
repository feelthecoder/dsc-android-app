package com.feelthecoder.dsc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feelthecoder.dsc.LoginActivity.login;
import com.feelthecoder.dsc.ui.AboutFragment;
import com.feelthecoder.dsc.ui.CompeteFragment;
import com.feelthecoder.dsc.ui.DonateFragment;
import com.feelthecoder.dsc.ui.EducationFragment;
import com.feelthecoder.dsc.ui.EventsFragment;
import com.feelthecoder.dsc.ui.HomeFragment;
import com.feelthecoder.dsc.ui.LeaderboardFragment;
import com.feelthecoder.dsc.ui.ProjectFragment;
import com.feelthecoder.dsc.ui.QuizFragment;
import com.feelthecoder.dsc.ui.SettingsFragment;
import com.feelthecoder.dsc.ui.SpandanFragment;
import com.feelthecoder.dsc.ui.TrainingFragment;
import com.feelthecoder.dsc.ui.WorkshopFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;

public class Main1Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    Fragment fm =new HomeFragment();
    Fragment fme= new HomeFragment();
    FirebaseAuth mAuth;
    private AppUpdateManager mAppUpdateManager;
    GoogleSignInClient mGoogleSignInClient;
    NavigationView navigationView;
    DatabaseReference databaseReference;
    String feedback,complain_suggestion;
    FloatingActionButton fab;
    public List<String> uCurrent;
    private int RC_APP_UPDATE=101;
    String date_,dob;
    String name_bd;
    Boolean mBool;

    private static final String CHANNEL_ID="FAB";
    private static final String CHANNEL_NAME="Complain";
    private static final String CHANNEL_DESC="This Channel shows notifications about the suggestions, feedback and complains.";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_main1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date_ = simpleDateFormat.format(new Date());
        String[] actual_date=date_.split("/");
        String formateed_date;
        String day=actual_date[0];
        String month=actual_date[1];
        formateed_date=day+"/"+month;
        mAuth = FirebaseAuth.getInstance();




        String folder_main = "DSC";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        FirebaseMessaging.getInstance().subscribeToTopic("Update").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        uCurrent=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users");
         fab = findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main1Activity.this);
                    alertDialog.setTitle("Complain/Suggestions");
                    alertDialog.setMessage("Enter your complains/suggestions.");
                    alertDialog.setIcon(R.drawable.twotone_copyright_black_18dp);
                    final EditText input = new EditText(Main1Activity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setPositiveButton("Submit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    complain_suggestion = input.getText().toString();
                                    if (complain_suggestion.isEmpty()) {
                                        input.setError("Can't be empty.");
                                        input.requestFocus();
                                        return;
                                    }
                                   DatabaseReference dRef=FirebaseDatabase.getInstance().getReference("MainData").child("ComplainSuggestions");
                                    dRef.child(Objects.requireNonNull(dRef.push().getKey())).child("comment").setValue(complain_suggestion);
                                    Toast.makeText(Main1Activity.this, "Compalain/Suggestions Submitted.", Toast.LENGTH_SHORT).show();
                                    showNotification("Complain/Suggestions");
                                }
                            });

                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                }
            });
            fab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main1Activity.this);
                    alertDialog.setTitle("Feedback");
                    alertDialog.setMessage("Share your feedback to improve DSC.");
                    alertDialog.setIcon(R.drawable.sharp_local_play_black_18dp);
                    final EditText input = new EditText(Main1Activity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setPositiveButton("Submit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    feedback = input.getText().toString();
                                    if (feedback.isEmpty()) {
                                        input.setError("Can't be empty.");
                                        input.requestFocus();
                                        return;
                                    }
                                    DatabaseReference dRef=FirebaseDatabase.getInstance().getReference("MainData").child("Feedback");
                                    dRef.child(Objects.requireNonNull(dRef.push().getKey())).child("comment").setValue(feedback);
                                    Toast.makeText(Main1Activity.this, "Feedback Submitted.", Toast.LENGTH_SHORT).show();
                                    showNotification("Feedback");

                                }
                            });

                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                    return false;

                }
            });




        showBirthDayDialog(formateed_date);

        checkIfEmailVerified();
        readDataFromFirebase(databaseReference);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigate);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);





    }

    private void showBirthDayDialog(String date_) {
        databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("dob")){
                    dob= Objects.requireNonNull(dataSnapshot.child("dob").getValue()).toString();
                    name_bd= Objects.requireNonNull(dataSnapshot.child("displayName").getValue()).toString();
                    String[] actual_date=dob.split("/");
                    String formateed_date;
                    String day=actual_date[0];
                    String month=actual_date[1];
                    formateed_date=day+"/"+month;

                    if(date_.equals(formateed_date)){
                        SharedPreferences mPrefs=getSharedPreferences("MyPreff",0);
                        mBool=mPrefs.getBoolean("isi",true);
                        if(mBool){
                            SharedPreferences mPref=getSharedPreferences("MyPreff",0);
                            SharedPreferences.Editor editor = mPref.edit();
                            editor.putBoolean("isi",false);
                            editor.apply();
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main1Activity.this);
                            alertDialog.setTitle("Happy Birthday! "+name_bd);
                            alertDialog.setMessage("Developer Student Club wishes you a very great and joyful birthday! May all your dreams came true & you achieve great in life."
                                    +""
                                    +"Have a Nice Day !");
                            alertDialog.setPositiveButton("Thank You !", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog.show();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public FloatingActionButton getFloatingActionButton() {
        return fab;
    }
    public void setFragmentObject(Fragment fm){
        this.fme=fm;
    }

    private void readDataFromFirebase(DatabaseReference databaseReference) {
        databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uCurrent.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot dn: dataSnapshot.getChildren()){
                        String udetail=dn.getValue(String.class);

                        uCurrent.add(udetail);
                    }
                    setNavigationHeader();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

   private void setNavigationHeader() {
        View hView=navigationView.getHeaderView(0);
        TextView txt1,txt2,txt3;
        CircularImageView circularImageView=hView.findViewById(R.id.img_head);
        Glide.with(Main1Activity.this).load(uCurrent.get(5)).into(circularImageView);
        txt1=hView.findViewById(R.id.user1);
        txt2=hView.findViewById(R.id.user3);
        txt3=hView.findViewById(R.id.user2);
        txt1.setText(uCurrent.get(0));
        txt2.setText(uCurrent.get(2));
        txt3.setText(uCurrent.get(6));

       SharedPreferences mPrefs=getSharedPreferences("MyGuide",0);
       String is=mPrefs.getString("gd","no");
       if(Objects.requireNonNull(is).equals("no")){
           new GuideView.Builder(this)
                   .setTitle("Complain/Suggestions")
                   .setContentText("Press to give suggestions and complains.")
                   .setTargetView(fab)
                   .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                   .build()
                   .show();

           new GuideView.Builder(this)
                   .setTitle("Feedback")
                   .setContentText("Long press to give your feedback. ")
                   .setTargetView(fab)
                   .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                   .build()
                   .show();

           SharedPreferences.Editor editor = mPrefs.edit();
           editor.putString("gd","yes");
           editor.apply();
       }


    }


@Override
public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawerLayout.closeDrawers();
        switch (item.getItemId()) {
        case R.id.HOME:
                fm = new HomeFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_home);
        break;
        case R.id.edu:
                 fm = new EducationFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_edu);
            fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.Train:
                fm = new TrainingFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_train);
            fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.workshop:
                fm = new WorkshopFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_workshop);
            fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.Event:
                fm = new EventsFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_events);
            fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.tech:
                fm = new SpandanFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_tech);
            fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.quiz:
                fm = new QuizFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_quiz);
        fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.leaderboard:
                fm = new LeaderboardFragment();
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_leader);
            fab.setVisibility(View.INVISIBLE);
                break;
        case R.id.compete:
                fm = new CompeteFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_compete);
            fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.project:
                fm=new ProjectFragment();
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_project);
            fab.setVisibility(View.INVISIBLE);
                break;
        case R.id.donate:
                fm = new DonateFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_donate);
            fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.about:
                fm = new AboutFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_about);
            fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.sett:
                fm = new SettingsFragment();
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_sett);
            fab.setVisibility(View.INVISIBLE);
        break;
        case R.id.Share:
              Intent a= new Intent(Intent.ACTION_SEND);
              final String appPackageName= getApplicationContext().getPackageName();
              String strAppLink;
              try{
                  strAppLink="https://play.google.com/store/apps/details?id=" +appPackageName;
              }
              catch(android.content.ActivityNotFoundException exc){
                  strAppLink="https://play.google.com/store/apps/details?id=" +appPackageName;
              }
              a.setType("text/link");
              String shareBody="Hey! Download this amazing app of Developer Student Club functioning in Rajkiya Engineering College Bijnor. You can play quiz & earn rewards.You can get " +
                      "Interview questions from this app. " +
                      "You can also use this app to register for different events,trainings,workshops,competitions,etc organised by them." +
                      "Also get resources to learn various programming languages,new technology and other type of technical resources." +
                      "You can also get other non-technical e-resources using this app. Download now !! and get excited benefits." +"\n"+""+strAppLink;
              String shareSub="Download DSC";
              a.putExtra(Intent.EXTRA_SUBJECT,shareSub);
            a.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(a,"Share Using"));

        break;
        case R.id.log:
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_lock_power_off).setTitle("Confirm logout")
                    .setMessage("Are you sure?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            sign_out();
                            Intent intent = new Intent(Main1Activity.this, login.class);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("no", null).show();
         break;
        }
        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
        }

        private void sign_out() {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(Main1Activity.this, "Successfully Signed Out.", Toast.LENGTH_SHORT).show();
                                        }
                                });

        }

        @Override
public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
        }

@Override
public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
        drawerLayout.closeDrawer(GravityCompat.START);
        } else
                if (fm instanceof HomeFragment && fme instanceof HomeFragment) {
                    fab.setVisibility(View.VISIBLE);
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
        .setMessage("Are you sure?")
        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        }
        }).setNegativeButton("no", null).show();
        }
                else
                    {
                        fme =new HomeFragment();
                        fm= new HomeFragment();
                        fab.setVisibility(View.VISIBLE);
                        super.onBackPressed();
                }
        }
        private void checkIfEmailVerified()
        {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String providerId = null;
            assert user != null;
            for (UserInfo profile : user.getProviderData()) {
                providerId = profile.getProviderId();
            }

            assert providerId != null;
            if(providerId.equals("password"))
            {
                if (!user.isEmailVerified())
                {

                    Toast.makeText(Main1Activity.this, "Email is not verified", Toast.LENGTH_SHORT).show();
                }
            }
        }

    @Override
    protected void onStart() {
        super.onStart();

        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE, Main1Activity.this, RC_APP_UPDATE);
                }
                catch (IntentSender.SendIntentException e) {
            }

        }
        else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
            popupSnackbarForCompleteUpdate();
        } else {

        }
    });
    }

    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED){
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED){
                        if (mAppUpdateManager != null){
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                    }
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
            }
        }
    }

    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.drawer_layout),
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null){
                mAppUpdateManager.completeUpdate();
            }
        });


        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    private void showNotification(String name) {

        Intent intent= new Intent(getApplicationContext(),NotificationActivity.class);
        String msg="Your "+name+" is submitted. Thank You.";
        intent.putExtra("title",name);
        intent.putExtra("message",msg);
        intent.putExtra("activity","Main1Activity.class");


        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle(name)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(1,mBuilder.build());
    }

}