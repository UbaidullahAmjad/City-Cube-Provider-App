package com.citycubeprovider.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

         binding.ivBack.setOnClickListener(v -> finish());

         setClickListeners();
    }

    private void setClickListeners() {

        binding.rlVehicle.setOnClickListener(v ->
                {
                    //startActivity(new Intent(this,ManageVehicle.class));
                }

        );

        binding.rlDocument.setOnClickListener(v ->
                {
                  //  startActivity(new Intent(this,ManageDocument.class));
                }
        );

        binding.rlProfile.setOnClickListener(v ->
                {
                  //  startActivity(new Intent(this,ProfileActivity.class));
                }

                );

        binding.rlPass.setOnClickListener(v ->
                {
                   // startActivity(new Intent(this,ChangePasswordActivity.class));
                }

        );

    }


}