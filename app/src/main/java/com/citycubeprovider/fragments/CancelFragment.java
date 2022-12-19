package com.citycubeprovider.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citycubeprovider.R;
import com.citycubeprovider.adapters.AdapterCancelBooking;
import com.citycubeprovider.databinding.FragmentCancelBinding;
import com.citycubeprovider.model.BookingModel;
import com.citycubeprovider.retrofit.ApiClient;
import com.citycubeprovider.retrofit.CityCubeProviderInterface;
import com.citycubeprovider.utility.DataManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CancelFragment extends Fragment {

    public String TAG = "CancelFragment";
    FragmentCancelBinding binding;
    CityCubeProviderInterface apiInterface;
    ArrayList<BookingModel.Result> arrayList;
    AdapterCancelBooking adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_cancel,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        arrayList = new ArrayList<>();
        adapter = new AdapterCancelBooking(getActivity(),arrayList);
        binding.rvCancel.setAdapter(adapter);

        getActiveBooking();
    }

    private void getActiveBooking() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("driver_id", DataManager.getInstance().getUserData(getActivity()).result.id);
        map.put("status","Cancel");
        Log.e(TAG, "get Cancelled Booking Service Request " + map);
        Call<BookingModel> loginCall = apiInterface.getBookingStatus2(map);
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