package com.citycubeprovider.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityShareBinding;


public class ShareActivity extends AppCompatActivity {

    ActivityShareBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share);

        binding.ivBack.setOnClickListener(v ->
                {
                    finish();
                }
        );


    }
}