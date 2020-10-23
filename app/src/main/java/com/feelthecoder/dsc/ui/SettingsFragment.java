package com.feelthecoder.dsc.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.feelthecoder.dsc.Adapters.SettingsPageAdapter;
import com.feelthecoder.dsc.AppUI.SAppFragment;
import com.feelthecoder.dsc.AppUI.SUserFragment;
import com.feelthecoder.dsc.R;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class SettingsFragment extends Fragment {
   Toolbar tabTool;
   ViewPager viewPager;
   TabLayout tabLayout;

   SettingsPageAdapter pagerAdapter;
   View view;

    @Override
    public void onAttach(Context context) {


        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        viewPager=view.findViewById(R.id.viewpager);
        tabLayout=view.findViewById(R.id.tabLayout);
        pagerAdapter=new SettingsPageAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new SUserFragment(),"User Settings");
        pagerAdapter.addFragment(new SAppFragment(),"App Settings");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
