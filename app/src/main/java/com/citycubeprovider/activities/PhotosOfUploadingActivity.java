package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.citycubeprovider.R;
import com.citycubeprovider.adapters.UploadingImageAdapter;
import com.citycubeprovider.databinding.ActivityPhotosOfUploadingBinding;

public class PhotosOfUploadingActivity extends AppCompatActivity {

    ActivityPhotosOfUploadingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_photos_of_uploading);

        binding.ivBack.setOnClickListener(v -> finish());


        GridLayoutManager gridLayoutManager = new GridLayoutManager(PhotosOfUploadingActivity.this, 3);
        binding.rvUploading.setLayoutManager(gridLayoutManager);
        binding.rvUploading.setAdapter(new UploadingImageAdapter(PhotosOfUploadingActivity.this));


        binding.btnContinue.setOnClickListener(v ->
                {
                    startActivity(new Intent(PhotosOfUploadingActivity.this,CutomerSignatureActivity.class));

                }
                );

    }
}