package com.amg.ips.ui;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    ArrayList<PlaceholderFragment> fragments;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new PlaceholderFragment());
        fragments.add(new PlaceholderFragment());
        fragments.add(new PlaceholderFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return PlaceholderFragment.GetInstance(fragments, position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}