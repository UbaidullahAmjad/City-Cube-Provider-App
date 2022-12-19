package com.citycubeprovider.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.citycubeprovider.BuildConfig;
import com.citycubeprovider.R;
import com.citycubeprovider.adapters.AdapterType2;
import com.citycubeprovider.databinding.ActivityAddVehicleBinding;
import com.citycubeprovider.model.CarListModel2;
import com.citycubeprovider.model.SignupModel;
import com.citycubeprovider.retrofit.ApiClient;
import com.citycubeprovider.retrofit.CityCubeProviderInterface;
import com.citycubeprovider.retrofit.Constant;
import com.citycubeprovider.utility.App;
import com.citycubeprovider.utility.DataManager;
import com.citycubeprovider.utility.NetworkReceiver;
import com.citycubeprovider.utility.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleAct extends AppCompatActivity {
    ActivityAddVehicleBinding binding;
    CityCubeProviderInterface apiInterface;
    public String TAG = "AddVehicleAct";
    ArrayList<CarListModel2.Result> arrayList;
    String car_id = "", car_name = "", userName = "", email = "", mobile = "", countryCode = "", password = "", refreshedToken = "";
    String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_vehicle);
        initViews();
    }

    private void initViews() {
        arrayList = new ArrayList<>();
        if (getIntent() != null) {
            userName = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            mobile = getIntent().getStringExtra("mobile");
            countryCode = getIntent().getStringExtra("phone_code");
            password = getIntent().getStringExtra("password");


        }

        if (NetworkReceiver.isConnected()) getCarList();
        else
            App.showToast(AddVehicleAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);


        binding.rlImg.setOnClickListener(v -> {
            if (checkPermisssionForReadStorage())
                showImageSelection();
        });

        binding.spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                car_id = arrayList.get(position).id;
                car_name = arrayList.get(position).carName;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnContinue.setOnClickListener(v -> {
            validation();
        });


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            try {
                refreshedToken = instanceIdResult.getToken();
                Log.e("Token===", refreshedToken);
                // Yay.. we have our new token now.
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void validation() {
        if (str_image_path.equals("")) {
            App.showToast(AddVehicleAct.this, getString(R.string.please_upload_vehicle_image), Toast.LENGTH_SHORT);
        } else if (car_id.equals(""))
            App.showToast(AddVehicleAct.this, getString(R.string.please_select_service_type), Toast.LENGTH_SHORT);
        else if (binding.etNumberPlate.getText().toString().equals("")) {
            binding.etNumberPlate.setError(getString(R.string.please_enter_vehicle_number));
            binding.etNumberPlate.setFocusable(true);
        } else {
            if (NetworkReceiver.isConnected())
                signup();
            else
                App.showToast(AddVehicleAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT);
        }

    }


    private void getCarList() {
        Call<CarListModel2> signupCall = apiInterface.getCarList2();
        signupCall.enqueue(new Callback<CarListModel2>() {
            @Override
            public void onResponse(Call<CarListModel2> call, Response<CarListModel2> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    CarListModel2 data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Car Type Response :" + responseString);
                    if (data.status.equals("1")) {
                        arrayList.clear();
                        arrayList.addAll(data.result);
                        binding.spinnerModel.setAdapter(new AdapterType2(AddVehicleAct.this, arrayList));

                    } else if (data.status.equals("0")) {
                        App.showToast(AddVehicleAct.this, data.message, Toast.LENGTH_SHORT);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CarListModel2> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });
    }

    public void showImageSelection() {

        final Dialog dialog = new Dialog(AddVehicleAct.this);
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
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);

    }

    private void openCamera() {

        File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + "/CityCubeProvider/Images/");

        if (!dirtostoreFile.exists()) {
            dirtostoreFile.mkdirs();
        }

        String timestr = DataManager.getInstance().convertDateToString(Calendar.getInstance().getTimeInMillis());

        File tostoreFile = new File(Environment.getExternalStorageDirectory() + "/CityCubeProvider/Images/" + "IMG_" + timestr + ".jpg");

        str_image_path = tostoreFile.getPath();

        uriSavedImage = FileProvider.getUriForFile(AddVehicleAct.this,
                BuildConfig.APPLICATION_ID + ".provider",
                tostoreFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

        startActivityForResult(intent, REQUEST_CAMERA);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            binding.rlPickImg.setVisibility(View.GONE);
            binding.ivImg.setVisibility(View.VISIBLE);
            Log.e("Result_code", requestCode + "");
            if (requestCode == SELECT_FILE) {
                str_image_path = DataManager.getInstance().getRealPathFromURI(AddVehicleAct.this, data.getData());
                Glide.with(AddVehicleAct.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivImg);

            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(AddVehicleAct.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivImg);


            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(AddVehicleAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(AddVehicleAct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(AddVehicleAct.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddVehicleAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(AddVehicleAct.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(AddVehicleAct.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(AddVehicleAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(AddVehicleAct.this,
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
                        Toast.makeText(AddVehicleAct.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddVehicleAct.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }

    private void signup() {
        MultipartBody.Part filePart;
        DataManager.getInstance().showProgressMessage(AddVehicleAct.this, getString(R.string.please_wait));
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody u_name = RequestBody.create(MediaType.parse("text/plain"), userName);
        RequestBody email1 = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody mobile1 = RequestBody.create(MediaType.parse("text/plain"), mobile);
        RequestBody country_code1 = RequestBody.create(MediaType.parse("text/plain"), countryCode);
        RequestBody pswd = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody regis_id = RequestBody.create(MediaType.parse("text/plain"), refreshedToken);
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "PROVIDER");
        RequestBody carId = RequestBody.create(MediaType.parse("text/plain"), car_id);
        RequestBody carName = RequestBody.create(MediaType.parse("text/plain"), car_name);
        RequestBody carNumber = RequestBody.create(MediaType.parse("text/plain"), binding.etNumberPlate.getText().toString());


        Call<SignupModel> signupCall = apiInterface.signupUser(u_name, email1, mobile1, country_code1, pswd, regis_id, type,
                carId, carName, carNumber, filePart);
        signupCall.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(Call<SignupModel> call, Response<SignupModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SignupModel data = response.body();
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e(TAG, "Register Response :" + dataResponse);
                        SessionManager.writeString(AddVehicleAct.this, Constant.driver_id, data.result.id);
                        Toast.makeText(AddVehicleAct.this, getString(R.string.sign_in_successful), Toast.LENGTH_SHORT); //Toast.makeText(LoginAct.this, getString(R.string.sign_in_successful), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddVehicleAct.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    } else if (data.status.equals("0")) {
                        App.showToast(AddVehicleAct.this, data.message, Toast.LENGTH_SHORT);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SignupModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }

        });
    }

}
