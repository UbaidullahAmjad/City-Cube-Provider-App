package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityMyProfileBinding;
import com.citycubeprovider.utility.DataManager;

public class MyProfileActivity extends AppCompatActivity {

    ActivityMyProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_profile);
        initViews();
    }

    private void initViews() {
        binding.ivBack.setOnClickListener(v -> finish());


        binding.rlEditProfile.setOnClickListener(v -> { startActivity(new Intent(MyProfileActivity.this,EditProfileAct.class)); });

        binding.rlMyReview.setOnClickListener(v -> { startActivity(new Intent(MyProfileActivity.this,MyReview.class)); });

        binding.rlMyRide.setOnClickListener(v -> { startActivity(new Intent(MyProfileActivity.this,MyRideActivity.class)); });

        binding.rlSettings.setOnClickListener(v -> { startActivity(new Intent(MyProfileActivity.this,SettingsActivity.class)); });
    }

    public void setUserInfo(){
        binding.tvName.setText(DataManager.getInstance().getUserData(MyProfileActivity.this).result.userName);
        binding.tvEmail.setText(DataManager.getInstance().getUserData(MyProfileActivity.this).result.email);
        Glide.with(MyProfileActivity.this)
                .load(DataManager.getInstance().getUserData(MyProfileActivity.this).result.image)
                .override(80,80)
                .apply(new RequestOptions().placeholder(R.drawable.user_default))
                .into(binding.ivProfile);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
    }

}