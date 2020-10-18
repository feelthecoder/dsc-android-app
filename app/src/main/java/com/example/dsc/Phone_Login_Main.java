package com.example.dsc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.dsc.Model.CountryData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Phone_Login_Main extends AppCompatActivity {
    Button btn;
    EditText mob;
    private Spinner spin;
    public static Activity fa;

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
        setContentView(R.layout.phone_login_main);
        btn = findViewById(R.id.phone_loge);
        fa=this;
        mob = findViewById(R.id.ed_login);
        spin = findViewById(R.id.spinn);
        spin.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, CountryData.countryNames));


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spin.getSelectedItemPosition()];
                String mobile = mob.getText().toString().trim();

                if (mobile.length() != 10) {
                    mob.setError("Enter a valid number");
                    mob.requestFocus();
                    return;
                }
                String phoneNumber = "+" + code + mobile;
                Intent intent = new Intent(Phone_Login_Main.this, Phone_Verification_Activity.class);
                intent.putExtra("mobile", phoneNumber);
                startActivity(intent);
                finish();
            }
        });

    }

}
