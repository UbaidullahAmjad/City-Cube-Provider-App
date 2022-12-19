package com.citycubeprovider.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.citycubeprovider.MainActivity;
import com.citycubeprovider.R;
import com.citycubeprovider.activities.DeliveryDetailActivity;
import com.citycubeprovider.activities.LiveTrackingActivity;
import com.citycubeprovider.adapters.AdapterAvailableRun;
import com.citycubeprovider.adapters.ProductAdapter;
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

import org.json.JSONObject;

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

public class AvailableRuns extends Fragment implements onBookingListener {
    public String TAG = "AvailableRunsFragment";
    FragmentAvailableRunsBinding binding;
    CityCubeProviderInterface apiInterface;
    ArrayList<BookingModel.Result> arrayList;
    AdapterAvailableRun adapter;
    AlertDialog alertDialog;
    String driverStatus ="",str_image_path="",str_image_path2="",signaturePath="";


    BroadcastReceiver JobStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Dialog Open ====", "=====");
            try {
                JSONObject object = new JSONObject(intent.getStringExtra("object"));
                if (intent.getStringExtra("object") != null) {
                    Log.e("Dialog Open11111 ====", "=====");
                    if (object.get("status").equals("Cancel_by_user")) {
                        //   NewRequestDialog.getInstance().stopCountDownTimer();
                    } else {
                        if (NetworkReceiver.isConnected()) getCurrentRequest();
                        else
                            App.showToast(getActivity(), getString(R.string.network_failure), Toast.LENGTH_LONG);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_available_runs, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arrayList = new ArrayList<>();
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        adapter = new AdapterAvailableRun(getActivity(), arrayList,AvailableRuns.this);
        binding.rvAvailable.setAdapter(adapter);
        if (NetworkReceiver.isConnected())
            getCurrentRequest();
        else
            App.showToast(getActivity(), getString(R.string.network_failure), Toast.LENGTH_LONG);
    }


    public void getCurrentRequest() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("driver_id", DataManager.getInstance().getUserData(getActivity()).result.id);
        Log.e(TAG, "get cuurent Booking Service Request " + map);
        Call<BookingModel> loginCall = apiInterface.getBookingCurt(map);
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
        BookingConfirmAlert(arrayList.get(position).id,arrayList.get(position).userId,arrayList.get(position).picklaterdate);
    }


    public void BookingConfirmAlert(String bookingId,String userId,String date) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(getResources().getString(R.string.are_you_sure_you_have_accept_booking_request));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getString(R.string.accept),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (NetworkReceiver.isConnected())
                            DriverChangeStatus(bookingId, "Accept",userId,date);
                        else
                            App.showToast(getActivity(), getString(R.string.network_failure), Toast.LENGTH_LONG);
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  dialog.dismiss();
                        DriverChangeStatus(bookingId, "Cancel",userId,date);
                    }
                });

        alertDialog = builder1.create();
        alertDialog.show();
    }


    public void BookingAcceptOrCancel(String bookingId, String status,String userId,String date) {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("request_id", bookingId);
        map.put("picklaterdate", date);
        map.put("user_id", userId);
        map.put("driver_id", DataManager.getInstance().getUserData(getActivity()).result.id);
        map.put("status", status);
        Log.e(TAG, "Booking Service Accept Cancel Request " + map);
        Call<Map<String,String>> acceptCancelCall = apiInterface.bookingAcceptCancel(map);
        acceptCancelCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Booking Service Accept Cancel Response :" + responseString);
                    if (data.get("status").equals("1")) {
                        alertDialog.dismiss();
                        if (NetworkReceiver.isConnected()) getCurrentRequest();
                        else
                            App.showToast(getActivity(), getString(R.string.network_failure), Toast.LENGTH_LONG);
                    } else if (data.get("status").equals("0")) {
                        App.showToast(getActivity(), data.get("message"), Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map<String,String>> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }


    public void DriverChangeStatus(String request_id, String status,String userId,String date) {
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
        RequestBody pickDate = RequestBody.create(MediaType.parse("text/plain"), date);


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
                        alertDialog.dismiss();
                        if (NetworkReceiver.isConnected()) getCurrentRequest();
                        else
                            App.showToast(getActivity(), getString(R.string.network_failure), Toast.LENGTH_LONG);
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



    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(JobStatusReceiver, new IntentFilter("Job_Status_Action1"));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(JobStatusReceiver);
    }
}