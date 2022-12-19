package com.citycubeprovider.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.citycubeprovider.fragments.ActiveFragment;
import com.citycubeprovider.fragments.CancelFragment;
import com.citycubeprovider.fragments.CompletedFragment;

/**
 * Created by Ravindra Birla on 13,April,2021
 */
public class TaskStatusPagerAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public TaskStatusPagerAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ActiveFragment activeFragment = new ActiveFragment();
                return activeFragment;
            case 1:
                CancelFragment cancelFragment = new CancelFragment();
                return cancelFragment;
            case 2:
                CompletedFragment completedFragment = new CompletedFragment();
                return completedFragment;
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