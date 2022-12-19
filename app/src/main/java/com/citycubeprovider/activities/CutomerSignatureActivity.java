package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityCutomerSignatureBinding;
import com.williamww.silkysignature.views.SignaturePad;


public class CutomerSignatureActivity extends AppCompatActivity {

    ActivityCutomerSignatureBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cutomer_signature);

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnContinue.setOnClickListener(v ->
                {
                    startActivity(new Intent(CutomerSignatureActivity.this,FeedbackActivity.class));
                }
                );

        binding.signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {

            }
        });

    }
}