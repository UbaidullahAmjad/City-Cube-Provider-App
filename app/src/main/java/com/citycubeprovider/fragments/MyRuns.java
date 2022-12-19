package com.citycubeprovider.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.citycubeprovider.R;
import com.citycubeprovider.adapters.AdapterAvailableRun;
import com.citycubeprovider.adapters.AdapterRun;
import com.citycubeprovider.adapters.ProductAdapter;
import com.citycubeprovider.databinding.FragmentMyRunsBinding;
import com.citycubeprovider.model.BookingModel;
import com.citycubeprovider.retrofit.ApiClient;
import com.citycubeprovider.retrofit.CityCubeProviderInterface;
import com.citycubeprovider.utility.App;
import com.citycubeprovider.utility.DataManager;
import com.citycubeprovider.utility.NetworkReceiver;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRuns extends Fragment {
    FragmentMyRunsBinding binding;
    public String TAG = "MyRuns";
    CityCubeProviderInterface apiInterface;
    ArrayList<BookingModel.Result> arrayList;
    AdapterRun adapter;

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_my_runs, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        arrayList = new ArrayList<>();
        adapter = new AdapterRun(getActivity(), arrayList);
        binding.rvRun.setAdapter(adapter);

        if (NetworkReceiver.isConnected())
            getActiveBooking();
        else
            App.showToast(getActivity(), getString(R.string.network_failure), Toast.LENGTH_LONG);
    }

    private void getActiveBooking() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("driver_id", DataManager.getInstance().getUserData(getActivity()).result.id);
        map.put("status","Accept");
        Log.e(TAG, "get cuurent Booking Service Request " + map);
        Call<BookingModel> loginCall = apiInterface.getBookingStatus(map);
        loginCall.enqueue(new Callback<BookingModel>() {
            @Override
            public void onResponse(Call<BookingModel> call, Response<BookingModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    BookingModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "get cuurent Booking Service Response :" + responseString);
                    if (data.status.equals("1")) {
                        binding.tvNotFound.setVisibility(View.GONE);
                        arrayList.clear();
                        arrayList.addAll(data.result);
                        adapter.notifyDataSetChanged();
                    } else if (data.status.equals("0")) {
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BookingModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

}