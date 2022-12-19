package com.citycubeprovider.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.citycubeprovider.fragments.AvailableRuns;
import com.citycubeprovider.fragments.MyRuns;

/**
 * Created by Ravindra Birla on 13,April,2021
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public HomePagerAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AvailableRuns homeFragment = new AvailableRuns();
                return homeFragment;
            case 1:
                MyRuns sportFragment = new MyRuns();
                return sportFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}