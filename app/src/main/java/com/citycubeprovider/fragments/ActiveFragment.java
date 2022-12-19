package com.citycubeprovider.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.citycubeprovider.R;
import com.citycubeprovider.activities.HomeActivity;
import com.citycubeprovider.activities.LiveTrackingActivity;
import com.citycubeprovider.adapters.AdapterActiveBooking;
import com.citycubeprovider.adapters.AdapterAvailableRun;
import com.citycubeprovider.databinding.FragmentActiveBinding;
import com.citycubeprovider.databinding.FragmentAvailableRunsBinding;
import com.citycubeprovider.listener.onBookingListener;
import com.citycubeprovider.model.BookingDetailModel;
import com.citycubeprovider.model.BookingModel;
import com.citycubeprovider.retrofit.ApiClient;
import com.citycubeprovider.retrofit.CityCubeProviderInterface;
import com.citycubeprovider.utility.App;
import com.citycubeprovider.utility.DataManager;
import com.citycubeprovider.utility.NetworkReceiver;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveFragment extends Fragment implements onBookingListener {

    public String TAG = "ActiveFragment";
    FragmentActiveBinding binding;
    CityCubeProviderInterface apiInterface;
    ArrayList<BookingModel.Result> arrayList;
    AdapterActiveBooking adapter;
    String driverStatus ="",str_image_path="",str_image_path2="",signaturePath="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_active,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        arrayList = new ArrayList<>();
        adapter = new AdapterActiveBooking(getActivity(),arrayList,ActiveFragment.this);
        binding.rvActive.setAdapter(adapter);

        getActiveBooking();
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

    @Override
    public void onBooking(int position) {
        if(NetworkReceiver.isConnected()) DriverChangeStatus(arrayList.get(position).id,"Start",position);
        else App.showToast(getActivity(),getString(R.string.network_failure),Toast.LENGTH_LONG);
    }

    public void DriverChangeStatus(String request_id, String status,int position) {
        MultipartBody.Part filePart1, filePart2, filePart3;

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart1 = MultipartBody.Part.createFormData("loading_image", file.getName(), RequestBody.create(MediaType.parse("loading_image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart1 = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }


        if (!str_image_path2.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path2));
            filePart2 = MultipartBody.Part.createFormData("unloading_image", file.getName(), RequestBody.create(MediaType.parse("unloading_image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart2 = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }


        if (!signaturePath.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(signaturePath));
            filePart3 = MultipartBody.Part.createFormData("signature_image", file.getName(), RequestBody.create(MediaType.parse("signature_image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart3 = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }


        RequestBody driver_id = RequestBody.create(MediaType.parse("text/plain"), DataManager.getInstance().getUserData(getActivity()).result.id);
        RequestBody status1 = RequestBody.create(MediaType.parse("text/plain"), status);
        RequestBody request_id1 = RequestBody.create(MediaType.parse("text/plain"), request_id);
        RequestBody pickDate = RequestBody.create(MediaType.parse("text/plain"), arrayList.get(position).picklaterdate);


        Call<BookingDetailModel> call = apiInterface.driverChangedStatus(driver_id, status1, request_id1, pickDate, filePart1, filePart2, filePart3);
        call.enqueue(new Callback<BookingDetailModel>() {
            @Override
            public void onResponse(Call<BookingDetailModel> call, Response<BookingDetailModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    BookingDetailModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Request Accept or Cancel Response :" + responseString);
                    if (data.status.equals("1")) {
                        driverStatus = data.result.status;
                        if (driverStatus.equals("Start")) {
                            getActivity().startActivity(new Intent(getActivity(), LiveTrackingActivity.class)
                                    .putExtra("request_id",arrayList.get(position).id));

                        }
                    } else if (data.status.equals("0")) {
                        App.showToast(getActivity(), data.message, Toast.LENGTH_SHORT);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BookingDetailModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

}