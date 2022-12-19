package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityAddAccountBinding;

public class AddAccountActivity extends AppCompatActivity {

    ActivityAddAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_account);


        binding.btnAdd.setOnClickListener(v ->
                {
                    startActivity(new Intent(this,LoginActivity.class));
                    finish();
                }
        );

    }
}