package com.example.dsc.StartActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dsc.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class slide_layout_1 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.silde_layout_1, container, false);
        Button btn1 = rootView.findViewById(R.id.button1);
        requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.cvolo));
        requireActivity().getWindow().setNavigationBarColor(requireActivity().getColor(R.color.cvolo));

        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.dscrecbijnor.com/"));
                startActivity(intent);
            }
        });

        return rootView;
    }


}