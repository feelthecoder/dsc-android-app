package com.example.dsc;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class welcome_main extends AppCompatActivity {
    private LinearLayout mDotLayout;
    ViewPager mSlideViewPager;


    String appPermissions[]={
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int PERMISSION_CODE=1240;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wel_activity);

        mSlideViewPager = findViewById(R.id.view_welcome);
        mDotLayout= findViewById(R.id.dots_layout);

        mSlideViewPager.setAdapter(new Slider_Adaptor(getSupportFragmentManager()));

        addDotsIndicator(0);

        if(checkAndRequestPermissions()){
            initApp();
        }
    }

    private void initApp() {
        mSlideViewPager.addOnPageChangeListener(viewListener);
    }

    public void addDotsIndicator(int position){
        mDotLayout.removeAllViews();
        TextView[] mDots = new TextView[3];
        for(int i = 0; i< mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.black_color));

            mDotLayout.addView(mDots[i]);

        }
        mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
    }


    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public static class Slider_Adaptor extends FragmentPagerAdapter {
        private Fragment[] childFragments;

        Slider_Adaptor(FragmentManager fm) {
            super(fm);
            childFragments = new Fragment[] {
                    new slide_layout_1(), //0
                    new slide_layout_2(), //1
                    new slide_layout_3() //2
            };
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return childFragments[position];
        }

        @Override
        public int getCount() {
            return childFragments.length; //3 items
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = getItem(position).getClass().getName();
            return title.subSequence(title.lastIndexOf(".") + 1, title.length());
        }
    }

    private boolean checkAndRequestPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for(String perm: appPermissions){
            if(ContextCompat.checkSelfPermission(this,perm)!= PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(perm);
            }
        }
        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),PERMISSION_CODE);
            return false;

        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==PERMISSION_CODE){
            HashMap<String, Integer> permissionResuts= new HashMap<>();
            int deniedCount=0;

            for(int i=0; i<grantResults.length; i++){
                if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                    permissionResuts.put(permissions[i],grantResults[i]);

                }

            }
            if(deniedCount == 0){
                initApp();
            }
            else
            {
                for(Map.Entry<String, Integer> entry :permissionResuts.entrySet()){
                    String permName=entry.getKey();
                    int permResult= entry.getValue();

                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,permName)){
                        showDialog("","This app needs Storage permissions to work without problems.","Yes, Grant permissions.",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        checkAndRequestPermissions();
                                    }

                                },"No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                },false);
                    }
                    else
                    {
                        showDialog("","You have denied some permissions. Allow all permissions at [Settings] > [Permissions]","" +
                                        "Go to Settings",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",getPackageName()
                                                ,null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    }
                                },"No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                },false);
                        break;
                    }
                }
            }
        }
    }

    public AlertDialog
        showDialog(String title, String msg, String positiveLabel, DialogInterface.OnClickListener positiveOnClick, String negativeLabel, DialogInterface.OnClickListener negativeOnClick, boolean isCancelAble){
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setCancelable(isCancelAble);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel,positiveOnClick);
        builder.setNegativeButton(negativeLabel,negativeOnClick);

        AlertDialog alert= builder.create();
        alert.show();
        return alert;
    }
}