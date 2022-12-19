package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityWalletBinding;
import com.google.android.material.tabs.TabLayout;

public class WalletActivity extends AppCompatActivity {

    ActivityWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        


        binding = DataBindingUtil.setContentView(this,R.layout.activity_wallet);

        binding.ivBack.setOnClickListener(v -> finish());


        binding.tabLayoutEventDay.addTab(binding.tabLayoutEventDay.newTab().setText("Today"));
        binding.tabLayoutEventDay.addTab(binding.tabLayoutEventDay.newTab().setText("Weekly"));
        binding.tabLayoutEventDay.addTab(binding.tabLayoutEventDay.newTab().setText("Monthly"));

    }
}