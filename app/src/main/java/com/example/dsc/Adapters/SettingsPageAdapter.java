package com.example.dsc.Adapters;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SettingsPageAdapter extends FragmentPagerAdapter {

     ArrayList<Fragment> mFragmentList;
     ArrayList<String> mFragmentTitleList;

    public SettingsPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
        mFragmentList= new ArrayList<>();
        mFragmentTitleList=new ArrayList<>();

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);

    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
