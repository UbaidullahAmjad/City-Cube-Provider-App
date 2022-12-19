package com.citycubeprovider.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.citycubeprovider.BuildConfig;
import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityLiveTrackingBinding;
import com.citycubeprovider.model.BookingDetailModel;
import com.citycubeprovider.model.BookingModel;
import com.citycubeprovider.retrofit.ApiClient;
import com.citycubeprovider.retrofit.CityCubeProviderInterface;
import com.citycubeprovider.utility.App;
import com.citycubeprovider.utility.DataManager;
import com.citycubeprovider.utility.DrawPollyLine;
import com.citycubeprovider.utility.NetworkReceiver;
import com.citycubeprovider.utility.UpdateLocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.kyanogen.signatureview.SignatureView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ng.max.slideview.SlideView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    public String TAG = "LiveTrackingActivity";
    ActivityLiveTrackingBinding binding;
    CityCubeProviderInterface apiInterface;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    public final static int PERMISSION_ID = 44;
    Vibrator vibrator;
    private PolylineOptions lineOptions;
    private LatLng PickUpLatLng, DropOffLatLng, carLatLng, prelatLng, mapCenterLatLng;
    private MarkerOptions PicUpMarker;
    private MarkerOptions DropOffMarker;
    private MarkerOptions carMarker1;
    Marker carMarker;
    String request_id = "", driverStatus = "", UserId = "", UserName = "", UserImage = "", mobile = "", unloading = "", signaturePath = "";
    AlertDialog alert11;
    String str_image_path = "", str_image_path2 = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_CAMERA2 = 3;
    private static final int SELECT_FILE2 = 4;

    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;
    AppCompatButton btnUnLoading, btnSignature, btnSubmit;
    ImageView ivUnloading, ivSignature;
    BookingDetailModel dtaaaaa;

    /*BroadcastReceiver LocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("latitude") != null) {
                carLatLng = new LatLng(Double.parseDouble(intent.getStringExtra("latitude")), Double.parseDouble(intent.getStringExtra("longitude")));
                if (prelatLng == null) {
                    //if (driverStatus.equals("Accept")) DrawPolyLine1();
                    //   else if(driverStatus.equals("Arrived"))  DrawPolyLine();
                    //   else if(driverStatus.equals("Start"))  DrawPolyLine1();
                    if (carMarker != null) carMarker.remove();
                    carMarker1.position(carLatLng);
                    carMarker = mMap.addMarker(carMarker1);
                    carMarker1.flat(true);
                    prelatLng = carLatLng;
                    if (PolyUtil.isLocationOnPath(carLatLng, polineLanLongLine, true, tolerance)) {
                        Log.e("chala on road===", "==true===");
                        //DrawPolyLine1();
                    } else Log.e("chala on road nahi hai=", "==false===");

                } else {
                    if (prelatLng != carLatLng) {
                        Log.e("locationChange====", carLatLng + "");
                        Location temp = new Location(LocationManager.GPS_PROVIDER);
                        temp.setLatitude(Double.parseDouble(intent.getStringExtra("latitude")));
                        temp.setLongitude(Double.parseDouble(intent.getStringExtra("longitude")));
                        //  MarkerAnimation.animateMarkerToGB(carMarker, carLatLng, new LatLngInterpolator.Spherical());
                        //  MarkerAnimation.move(mMap,carMarker,temp);
                        // MarkerAnimation.animateMarker(mMap,carMarker,polineLanLongLine,isMarkerRotating);
                        // float bearing = (float) bearingBetweenLocations(prelatLng, carLatLng);
                        // rotateMarker(carMarker, bearing);

                        moveVechile(carMarker, temp);
                        rotateMarker(carMarker, temp.getBearing(), start_rotation);

                        prelatLng = carLatLng;


                        if (driverStatus.equals("Start")) {
                            if (PolyUtil.isLocationOnPath(carLatLng, polineLanLongLine, true, tolerance)) {
                                Log.e("chala on road===", "==true===");

                            } else {
                                DrawPolyLine2();
                                Log.e("chala on road nahi hai=", "==false===");
                            }
                        }
                    }
                }
                animateCamera(carLatLng);
            }

            //  binding.tvAddress.setText(getAddress(TrackAct.this,Double.parseDouble(intent.getStringExtra("latitude")), Double.parseDouble(intent.getStringExtra("longitude"))));
        }
    };*/


    BroadcastReceiver TripStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("status") != null) {
                if (intent.getStringExtra("status").equals("chat")) {
                    if (NetworkReceiver.isConnected()) {
                        //   request_id = intent.getStringExtra("request_id");
                        getChatCount();
                    } else
                        App.showToast(LiveTrackingActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT);
                } else if (intent.getStringExtra("status").equals("Cancel_by_user")) {
                    startActivity(new Intent(LiveTrackingActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_tracking);
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        initViews();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void initViews() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        if (getIntent() != null)
            request_id = getIntent().getStringExtra("request_id");

        PicUpMarker = new MarkerOptions().title("Pick Up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
        DropOffMarker = new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker));

        binding.layoutforChat.setOnClickListener(v -> {
            startActivity(new Intent(LiveTrackingActivity.this, MsgChatAct.class)
                    .putExtra("UserId", UserId)
                    .putExtra("UserName", UserName)
                    .putExtra("UserImage", UserImage)
                    .putExtra("request_id", request_id));
        });

        binding.layoutforCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
            startActivity(intent);
        });

        binding.btnArrived.setOnClickListener(v -> {
            DriverArrivedDialog();
        });

        binding.slideViewLoading.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                uploadImageDialog();
            }
        });


        binding.slideViewUnLoading.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                unloadingDialog();
            }
        });


    }

    public void DriverArrivedDialog() {
      AlertDialog.Builder  builder1 = new AlertDialog.Builder(LiveTrackingActivity.this);
        builder1.setMessage(getResources().getString(R.string.are_you_sure_you_have_arrived_at_departure_location_of_user));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (NetworkReceiver.isConnected())
                            DriverChangeStatus(request_id, "Arrived");
                        else
                            App.showToast(LiveTrackingActivity.this, getString(R.string.network_failure), Toast.LENGTH_LONG);
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }




    private void uploadImageDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LiveTrackingActivity.this);
        builder1.setMessage(getResources().getString(R.string.please_upload_loading_image));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getString(R.string.upload),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (checkPermisssionForReadStorage())
                            showImageSelection();
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alert11 = builder1.create();
        alert11.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (NetworkReceiver.isConnected()) getBookingDetail(request_id);
        else
            App.showToast(LiveTrackingActivity.this, getString(R.string.network_failure), Toast.LENGTH_LONG);
    }


    public void getBookingDetail(String request_id) {
        Map<String, String> map = new HashMap<>();
        map.put("request_id", request_id);
        Log.e(TAG, "Request Accept or Cancel Request :" + map);
        Call<BookingDetailModel> call = apiInterface.bookingDetails(map);
        call.enqueue(new Callback<BookingDetailModel>() {
            @Override
            public void onResponse(Call<BookingDetailModel> call, Response<BookingDetailModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    dtaaaaa = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Request Accept or Cancel Response :" + responseString);
                    if (dtaaaaa.status.equals("1")) {
                        UserId = dtaaaaa.result.userDetails.id;
                        UserName = dtaaaaa.result.userDetails.userName; //+ " " + data.result.userDetails.lastLame;
                        UserImage = dtaaaaa.result.userDetails.userImage;
                        mobile = "+" + dtaaaaa.result.userDetails.phoneCode + dtaaaaa.result.userDetails.mobile;
                        binding.tvName.setText(UserName);
                        prelatLng = null;
                        driverStatus = dtaaaaa.result.status;
                        Glide.with(LiveTrackingActivity.this)
                                .load(UserImage)
                                .apply(new RequestOptions().placeholder(R.drawable.user_default))
                                .override(200, 200)
                                .into(binding.ivUserPropic);
                        PickUpLatLng = new LatLng(Double.parseDouble(dtaaaaa.result.picuplat), Double.parseDouble(dtaaaaa.result.pickuplon));
                        DropOffLatLng = new LatLng(Double.parseDouble(dtaaaaa.result.droplat), Double.parseDouble(dtaaaaa.result.droplon));
                        setCurrentLoc();
                        if (driverStatus.equals("Start")) {
                            binding.btnArrived.setVisibility(View.VISIBLE);
                            binding.btnBegin.setVisibility(View.GONE);
                            binding.btnEnd.setVisibility(View.GONE);
                        } else if (driverStatus.equals("Arrived")) {
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.VISIBLE);
                            binding.btnEnd.setVisibility(View.GONE);

                        } else if (driverStatus.equals("Loading")) {
                            vibrator.vibrate(100);
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.GONE);
                            binding.btnEnd.setVisibility(View.VISIBLE);

                        } else if (driverStatus.equals("Finish")) {
                            vibrator.vibrate(100);
                            App.showToast(LiveTrackingActivity.this,getString(R.string.your_service_done),Toast.LENGTH_LONG);
                            startActivity(new Intent(LiveTrackingActivity.this,HomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }

                    } else if (dtaaaaa.status.equals("0")) {
                        App.showToast(LiveTrackingActivity.this, dtaaaaa.message, Toast.LENGTH_SHORT);
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

    private void callService() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                startService(new Intent(LiveTrackingActivity.this, UpdateLocationService.class));
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(17).build();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void setCurrentLoc() {
        DrawPolyLine();
    }

    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(PickUpLatLng)
                .setDestination(DropOffLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                mMap.clear();
                // polineLanLongLine.clear();
                // polineLanLongLine = latLngs;
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.geodesic(true);
                lineOptions.color(R.color.black);
                AddDefaultMarker();
            }
        });
    }

    public void AddDefaultMarker() {
        if (mMap != null) {
            mMap.clear();
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
            if (PickUpLatLng != null) {
                PicUpMarker.position(PickUpLatLng);
                mMap.addMarker(PicUpMarker);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (DropOffLatLng != null) {
                DropOffMarker.position(DropOffLatLng);
                mMap.addMarker(DropOffMarker);
            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatCount();
        callService();
        //  registerReceiver(LocationReceiver, new IntentFilter("data_update_location1"));
        registerReceiver(TripStatusReceiver, new IntentFilter("Job_Status_Action"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregisterReceiver(LocationReceiver);
        stopService(new Intent(LiveTrackingActivity.this, UpdateLocationService.class));
        unregisterReceiver(TripStatusReceiver);

    }


    private void getChatCount() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", DataManager.getInstance().getUserData(LiveTrackingActivity.this).result.id);
        Log.e("MapMap", "CHAT COUNT REQUEST" + map);
        Call<Map<String, String>> chatCount = apiInterface.getChatCount(map);
        chatCount.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String, String> data = response.body();
                    if (data.get("status").equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "CHAT COUNT RESPONSE" + dataResponse);
                        if (data.get("count").equals("0")) {
                            binding.tvCounter.setVisibility(View.GONE);
                        } else {
                            binding.tvCounter.setVisibility(View.VISIBLE);
                            binding.tvCounter.setText(data.get("count"));
                        }

                    } else if (data.get("status").equals("0")) {
                        binding.tvCounter.setVisibility(View.GONE);
                        // Toast.makeText(TrackingActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }

        });

    }

    public void showImageSelection() {

        final Dialog dialog = new Dialog(LiveTrackingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_show_image_selection);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        LinearLayout layoutCamera = (LinearLayout) dialog.findViewById(R.id.layoutCemera);
        LinearLayout layoutGallary = (LinearLayout) dialog.findViewById(R.id.layoutGallary);
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                openCamera();
            }
        });
        layoutGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                getPhotoFromGallary();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void getPhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        if (unloading.equals("1"))
            startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE2);
        else startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);

    }

    private void openCamera() {

        File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + "/CityCubrProvider/Images/");

        if (!dirtostoreFile.exists()) {
            dirtostoreFile.mkdirs();
        }

        String timestr = DataManager.getInstance().convertDateToString(Calendar.getInstance().getTimeInMillis());

        File tostoreFile = new File(Environment.getExternalStorageDirectory() + "/CityCubrProvider/Images/" + "IMG_" + timestr + ".jpg");
        if (unloading.equals("1")) str_image_path2 = tostoreFile.getPath();
        else str_image_path = tostoreFile.getPath();

        uriSavedImage = FileProvider.getUriForFile(LiveTrackingActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                tostoreFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        if (unloading.equals("1")) startActivityForResult(intent, REQUEST_CAMERA2);
        else startActivityForResult(intent, REQUEST_CAMERA);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("Result_code", requestCode + "");
            if (requestCode == SELECT_FILE) {
                str_image_path = DataManager.getInstance().getRealPathFromURI(LiveTrackingActivity.this, data.getData());
                if (NetworkReceiver.isConnected())
                    DriverChangeStatus(request_id, "Loading");
                else
                    App.showToast(LiveTrackingActivity.this, getString(R.string.network_failure), Toast.LENGTH_LONG);
            } else if (requestCode == REQUEST_CAMERA) {
                if (NetworkReceiver.isConnected())
                    DriverChangeStatus(request_id, "Loading");
                else
                    App.showToast(LiveTrackingActivity.this, getString(R.string.network_failure), Toast.LENGTH_LONG);
            } else if (requestCode == SELECT_FILE2) {
                str_image_path2 = DataManager.getInstance().getRealPathFromURI(LiveTrackingActivity.this, data.getData());
                btnUnLoading.setVisibility(View.GONE);
                ivUnloading.setVisibility(View.VISIBLE);
                Glide.with(LiveTrackingActivity.this)
                        .load(str_image_path2)
                        .into(ivUnloading);

            } else if (requestCode == REQUEST_CAMERA2) {
                btnUnLoading.setVisibility(View.GONE);
                ivUnloading.setVisibility(View.VISIBLE);
                Glide.with(LiveTrackingActivity.this)
                        .load(str_image_path2)
                        .into(ivUnloading);

            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(LiveTrackingActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(LiveTrackingActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(LiveTrackingActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LiveTrackingActivity.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(LiveTrackingActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(LiveTrackingActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(LiveTrackingActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LiveTrackingActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(LiveTrackingActivity.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LiveTrackingActivity.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


            case PERMISSION_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setCurrentLoc();
                }
            }

        }
    }


    public void unloadingDialog() {
        final Dialog unloaddialog = new Dialog(LiveTrackingActivity.this);
        unloaddialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        unloaddialog.setContentView(R.layout.dialog_unloading);
        unloaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        unloaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        unloaddialog.setCancelable(true);
        unloaddialog.setCanceledOnTouchOutside(false);
        btnUnLoading = unloaddialog.findViewById(R.id.btnUnLoading);
        btnSignature = unloaddialog.findViewById(R.id.btnSignature);
        btnSubmit = unloaddialog.findViewById(R.id.btnSubmit);
        ivUnloading = unloaddialog.findViewById(R.id.ivUnloading);
        ivSignature = unloaddialog.findViewById(R.id.ivSignature);


        btnUnLoading.setOnClickListener(v -> {
            unloading = "1";
            if (checkPermisssionForReadStorage()) showImageSelection();
        });

        btnSignature.setOnClickListener(v -> {
            SignatureDailog();
        });

        btnSubmit.setOnClickListener(v -> {
               if(str_image_path2.equals(""))
                   App.showToast(LiveTrackingActivity.this,getString(R.string.please_upload_unloading_image),Toast.LENGTH_LONG);
            else if (signaturePath.equals(""))
                App.showToast(LiveTrackingActivity.this, getString(R.string.please_add_signature), Toast.LENGTH_LONG);
            else {
                   if (NetworkReceiver.isConnected())
                       DriverChangeStatus(request_id, "Finish");
                   else
                       App.showToast(LiveTrackingActivity.this, getString(R.string.network_failure), Toast.LENGTH_LONG);
               }
        });


        unloaddialog.show();

    }

    private void SignatureDailog() {
        Dialog dialog = new Dialog(LiveTrackingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        SignatureView signature_view = dialog.findViewById(R.id.signature_view);
        AppCompatButton btnClear = dialog.findViewById(R.id.btnClear);
        AppCompatButton btnSave = dialog.findViewById(R.id.btnSave);


        btnClear.setOnClickListener(v -> {
            signature_view.clearCanvas();
        });

        btnSave.setOnClickListener(v -> {
            signaturePath = String.valueOf(DataManager.getInstance().getImageUri(LiveTrackingActivity.this, signature_view.getSignatureBitmap()));

            if (signaturePath.equals(""))
                App.showToast(LiveTrackingActivity.this, getString(R.string.please_add_signature), Toast.LENGTH_LONG);
            else {
                btnSignature.setVisibility(View.GONE);
                ivSignature.setVisibility(View.VISIBLE);
                Glide.with(LiveTrackingActivity.this)
                        .load(signaturePath)
                        .into(ivSignature);
                dialog.dismiss();
                signaturePath = DataManager.getRealPathFromURI(LiveTrackingActivity.this, Uri.parse(signaturePath));
            }
        });


        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        dialog.show();

    }

    public void DriverChangeStatus(String request_id, String status) {
        MultipartBody.Part filePart1, filePart2, filePart3;

        DataManager.getInstance().showProgressMessage(LiveTrackingActivity.this, getString(R.string.please_wait));

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


        RequestBody driver_id = RequestBody.create(MediaType.parse("text/plain"), DataManager.getInstance().getUserData(LiveTrackingActivity.this).result.id);
        RequestBody status1 = RequestBody.create(MediaType.parse("text/plain"), status);
        RequestBody request_id1 = RequestBody.create(MediaType.parse("text/plain"), request_id);
        RequestBody pickDate = RequestBody.create(MediaType.parse("text/plain"), dtaaaaa.result.picklaterdate);


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
                            binding.btnArrived.setVisibility(View.VISIBLE);
                            binding.btnBegin.setVisibility(View.GONE);
                            binding.btnEnd.setVisibility(View.GONE);
                        } else if (driverStatus.equals("Arrived")) {
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.VISIBLE);
                            binding.btnEnd.setVisibility(View.GONE);

                        } else if (driverStatus.equals("Loading")) {
                            vibrator.vibrate(100);
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.GONE);
                            binding.btnEnd.setVisibility(View.VISIBLE);

                        } else if (driverStatus.equals("Finish")) {
                            vibrator.vibrate(100);
                            App.showToast(LiveTrackingActivity.this,getString(R.string.your_service_done),Toast.LENGTH_LONG);
                            startActivity(new Intent(LiveTrackingActivity.this,HomeActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    } else if (data.status.equals("0")) {
                        App.showToast(LiveTrackingActivity.this, data.message, Toast.LENGTH_SHORT);
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