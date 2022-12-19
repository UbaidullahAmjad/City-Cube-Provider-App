package com.citycubeprovider.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityPromotionBinding;

public class PromotionActivity extends AppCompatActivity {

    ActivityPromotionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_promotion);
        binding.ivBack.setOnClickListener(v ->
                {
                    finish();
                }
        );


    }
}