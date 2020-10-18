package com.example.dsc.ResourceActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dsc.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class eResourceActivity extends AppCompatActivity {

    String[] list_resource={"Pdf Books","Recommended Youtube Courses","Udemy Free Course","NPTEL Lectures", "DSC Tutor","Standard Books","Best Blogs" };
    String title,tit;
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
        setContentView(R.layout.activity_e_resource);

        Toolbar toolbar=findViewById(R.id.tool_e);
        tit= Objects.requireNonNull(getIntent().getExtras().get("title")).toString();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(tit);
        title=tit.toLowerCase().replace(" ","").replace("++","plusplus").replace("#","sharp");
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_details, list_resource);
        ListView listView =findViewById(R.id.resource_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent pdf=new Intent(eResourceActivity.this, ResourcePDFActivity.class);
                    pdf.putExtra("title",title);
                    startActivity(pdf);
                }
                else
                if(position==1){
                    Intent youtube=new Intent(eResourceActivity.this, ResourceYoutubeActivity.class);
                    youtube.putExtra("title",title);
                    startActivity(youtube);
                }
                else
                if(position==2){
                    Intent udemy=new Intent(eResourceActivity.this, ResourceUdemyActivity.class);
                    udemy.putExtra("title",title);
                    startActivity(udemy);
                }
                else
                if(position==3){
                    Intent nptel=new Intent(eResourceActivity.this, ResourceNPTELActivity.class);
                    nptel.putExtra("title",title);
                    startActivity(nptel);
                }
                else
                if(position==4){
                    Intent dsc=new Intent(eResourceActivity.this, ResourceDSCActivity.class);
                    dsc.putExtra("title",title);
                    startActivity(dsc);
                }
                else
                if(position==5){
                    Intent sbook=new Intent(eResourceActivity.this, ResourceStandardBookActivity.class);
                    sbook.putExtra("title",title);
                    startActivity(sbook);
                }
                else
                if(position==6){
                    Intent blog=new Intent(eResourceActivity.this, ResourceBlogActivity.class);
                    blog.putExtra("title",title);
                    startActivity(blog);
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
}
