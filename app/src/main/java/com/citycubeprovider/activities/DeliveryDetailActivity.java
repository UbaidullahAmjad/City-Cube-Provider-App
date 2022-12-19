package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityDeliveryDetailBinding;

public class DeliveryDetailActivity extends AppCompatActivity {

    ActivityDeliveryDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_delivery_detail);

        binding.rlAccept.setOnClickListener(v ->

                {
                    startActivity(new Intent(DeliveryDetailActivity.this, LiveTrackingActivity.class));
                }
                );

        binding.rlReject.setOnClickListener(v ->
                {
                    finish();
                }
                );

        binding.ivBack.setOnClickListener(v -> finish());
    }
}