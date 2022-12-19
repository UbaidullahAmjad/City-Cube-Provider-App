package com.citycubeprovider.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityFeedbackBinding;

public class FeedbackActivity extends AppCompatActivity {

    ActivityFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);

        binding.tvCancel.setOnClickListener(v ->
                {
                    finish();
                }
                );

        binding.btnContinue.setOnClickListener(v ->
                {
                    startActivity(new Intent(FeedbackActivity.this,HomeActivity.class));
                    finish();
                }
                );

    }
}