package com.example.dsc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class slide_layout_3 extends Fragment {
     private Activity context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        context=getActivity();
        View rootView = inflater.inflate(R.layout.slide_layout_3, container, false);
        Button btn1 = rootView.findViewById(R.id.button3);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://recb.ac.in/"));
                startActivity(intent);
            }
        });

   Button btn2 = rootView.findViewById(R.id.buttonfinish);
   btn2.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           Intent intent = new Intent(context, slide_layout_4.class);
           startActivity(intent);
          Objects.requireNonNull(getActivity()).finish();
       }
   });

        return rootView;
    }

}

