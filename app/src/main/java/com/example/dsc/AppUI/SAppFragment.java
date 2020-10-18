package com.example.dsc.AppUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.dsc.R;
import com.example.dsc.VersionActivity;
import com.example.dsc.dsc_splash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SAppFragment extends Fragment {
    public Switch dark_mode,notification_off;

    String[] list_app={"Update","Rate","Version"};
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_s_app, container, false);

        dark_mode=view.findViewById(R.id.switch_dark);
        notification_off=view.findViewById(R.id.switch_notification);
        SharedPreferences mPrefs=getActivity().getSharedPreferences("MyPrefs",0);
        String is=mPrefs.getString("mode","not");
        SharedPreferences mPref=getActivity().getSharedPreferences("MyNoti",0);
        String noti=mPref.getString("notification","on");
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES || is.equals("dark")){
            dark_mode.setChecked(true);
        }
        dark_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    restartApp();
                }
                else
                {
                    RestartApp();
                }
            }
        });

        notification_off.setChecked(true);
        notification_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notification_off.setChecked(true);
                Toast.makeText(getContext(), "Notifications can not be turned off", Toast.LENGTH_SHORT).show();
            }
        });



        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),R.layout.list_details, list_app);
        ListView listView = view.findViewById(R.id.app_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +"com.example.dsc")));
                }
                else
                if(position==1){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +"com.example.dsc")));
                }
                else
                if(position==2){
                    startActivity(new Intent(getActivity(), VersionActivity.class));
                }
            }
        });

        return view;
    }


    private void restartApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("It will restart app. Continue ?");
        builder.setTitle("Dark Mode");
        builder.setCancelable(false);
        builder.setPositiveButton(
                        "Yes",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                SharedPreferences mPref=getActivity().getSharedPreferences("MyPrefs",0);
                                SharedPreferences.Editor editor = mPref.edit();
                                editor.putString("mode","dark");
                                editor.apply();
                                Intent intent=new Intent(getContext(), dsc_splash.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
        builder.setNegativeButton(
                        "No",
                        new DialogInterface
                                .OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        });
        builder.show();
    }
    private void RestartApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("It will restart app. Continue ?");
        builder.setTitle("Dark Mode");
        builder.setCancelable(false);
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        SharedPreferences mPref=getActivity().getSharedPreferences("MyPrefs",0);
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putString("mode","");
                        editor.apply();
                        Intent intent=new Intent(getContext(), dsc_splash.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
        builder.setNegativeButton(
                "No",
                new DialogInterface
                        .OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}

