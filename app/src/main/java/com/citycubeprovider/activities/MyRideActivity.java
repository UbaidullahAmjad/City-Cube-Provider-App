package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.citycubeprovider.R;
import com.citycubeprovider.adapters.TaskStatusPagerAdapter;
import com.citycubeprovider.databinding.ActivityMyRideBinding;
import com.google.android.material.tabs.TabLayout;

public class MyRideActivity extends AppCompatActivity {
    ActivityMyRideBinding binding;
    TaskStatusPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_ride);

        binding.ivBack.setOnClickListener(v -> finish());

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.active));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.cancel));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.completed));

        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapterViewPager = new TaskStatusPagerAdapter(this,getSupportFragmentManager(),binding.tabLayout.getTabCount());

        binding.vpPager.setAdapter(adapterViewPager);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                binding.vpPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}