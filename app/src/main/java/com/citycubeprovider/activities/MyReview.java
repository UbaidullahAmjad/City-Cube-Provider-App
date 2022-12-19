package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.citycubeprovider.R;
import com.citycubeprovider.adapters.AdapterType2;
import com.citycubeprovider.adapters.RatingAdapter;
import com.citycubeprovider.databinding.ActivityMyReviewBinding;
import com.citycubeprovider.model.CarListModel2;
import com.citycubeprovider.model.FeedbackModel;
import com.citycubeprovider.retrofit.ApiClient;
import com.citycubeprovider.retrofit.CityCubeProviderInterface;
import com.citycubeprovider.utility.App;
import com.citycubeprovider.utility.DataManager;
import com.citycubeprovider.utility.MyService;
import com.citycubeprovider.utility.NetworkReceiver;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReview extends AppCompatActivity {
    public String TAG ="MyReview";
    ActivityMyReviewBinding binding;
    ArrayList<FeedbackModel.Result>arrayList;
    CityCubeProviderInterface apiInterface;
    RatingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_review);
        initView();
    }

    private void initView() {
        arrayList = new ArrayList<>();
        binding.ivBack.setOnClickListener(v -> finish());
        adapter = new RatingAdapter(this,arrayList);
        binding.rvRating.setAdapter(adapter);

        if(NetworkReceiver.isConnected()) getFeedback();
        else App.showToast(MyReview.this,getString(R.string.network_failure), Toast.LENGTH_LONG);
    }

    private void getFeedback() {
        Map<String,String> map = new HashMap<>();
        map.put("user_id",DataManager.getInstance().getUserData(MyReview.this).result.id);
        Call<FeedbackModel> signupCall = apiInterface.getFeedback(map);
        signupCall.enqueue(new Callback<FeedbackModel>() {
            @Override
            public void onResponse(Call<FeedbackModel> call, Response<FeedbackModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    FeedbackModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Car Type Response :" + responseString);
                    if (data.status.equals("1")) {
                        binding.tvNotFound.setVisibility(View.GONE);
                        arrayList.clear();
                        arrayList.addAll(data.result);
                        adapter.notifyDataSetChanged();

                    } else if (data.status.equals("0")) {
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();

                      //  App.showToast(MyReview.this, data.message, Toast.LENGTH_SHORT);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FeedbackModel> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });
    }
}