package com.feelthecoder.dsc.AboutActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feelthecoder.dsc.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {
    
    ImageView insta,facebook,github,hackerrank,linkdin;
    TextView bio,name,specialization,projects,skills,post;
    CircularImageView profile;
    String instagram,fb,git,hackerr,linkd,bi_o,nam_e,special,project,skill,pos_t,profile_pic;

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
        setContentView(R.layout.activity_profile);
        Toolbar toolbar=findViewById(R.id.tool_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        //Attach view with fields by finding id's

        insta=findViewById(R.id.inst_a);
        facebook=findViewById(R.id.face_book);
        github=findViewById(R.id.git_hub);
        hackerrank=findViewById(R.id.hacker_rank);
        linkdin=findViewById(R.id.linkd_in);
        
        bio=findViewById(R.id.profile_bio_viewm);
        name=findViewById(R.id.profile_name_viewm);
        specialization=findViewById(R.id.profile_special_view_a);
        projects=findViewById(R.id.profile_project_view_a);
        skills=findViewById(R.id.profile_skills_viewma);
        post=findViewById(R.id.profile_post_view_a);
        profile=findViewById(R.id.profile_image_viewm);
        
        //get data by intent

        nam_e=getIntent().getExtras().get("name").toString();
        pos_t=getIntent().getExtras().get("post").toString();
        skill=getIntent().getExtras().get("skills").toString();
       special= getIntent().getExtras().get("special").toString();
        git=getIntent().getExtras().get("github").toString();
        fb=getIntent().getExtras().get("fb").toString();
        linkd=getIntent().getExtras().get("linkdin").toString();
        hackerr=getIntent().getExtras().get("hackerrank").toString();
        instagram=getIntent().getExtras().get("insta").toString();
        bi_o=getIntent().getExtras().get("bio").toString();
        profile_pic=getIntent().getExtras().get("profile").toString();
        project=getIntent().getExtras().get("projects").toString();

        //set data to views

        bio.setText(bi_o);
        projects.setText(project);
        skills.setText(skill);
        post.setText(pos_t);
        Glide.with(getApplicationContext()).load(profile_pic).into(profile);
        name.setText(nam_e);
        specialization.setText(special);


        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(instagram);
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(instagram)));
                }

            }
        });


        linkdin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://"+linkd));
                final PackageManager packageManager = getApplicationContext().getPackageManager();
                final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.isEmpty()) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id="+linkd));
                }
                startActivity(intent);


            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href="+fb));
                startActivity(intent);
            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(git));
                startActivity(i);
            }
        });

        hackerrank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(hackerr));
                startActivity(i);
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
