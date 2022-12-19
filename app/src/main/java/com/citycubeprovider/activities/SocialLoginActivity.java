package com.citycubeprovider.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivitySocialLoginBinding;

public class SocialLoginActivity extends AppCompatActivity {

    ActivitySocialLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_social_login);

        binding.btnGetStarted.setOnClickListener(v ->
                {
                   startActivity(new Intent(SocialLoginActivity.this,LoginActivity.class));
                   finish();
                }
        );

    }
}