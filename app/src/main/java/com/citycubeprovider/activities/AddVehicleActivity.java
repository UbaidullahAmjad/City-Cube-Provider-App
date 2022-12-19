package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

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

import com.bumptech.glide.Glide;
import com.citycubeprovider.BuildConfig;
import com.citycubeprovider.R;
import com.citycubeprovider.adapters.AdapterBrand;
import com.citycubeprovider.adapters.AdapterModel;
import com.citycubeprovider.adapters.AdapterType;
import com.citycubeprovider.databinding.ActivityAddVehicleBinding;
import com.citycubeprovider.model.BrandListModel;
import com.citycubeprovider.model.CarListModel;
import com.citycubeprovider.model.ModListModel;
import com.citycubeprovider.model.SignupModel;
import com.citycubeprovider.retrofit.ApiClient;
import com.citycubeprovider.retrofit.CityCubeProviderInterface;
import com.citycubeprovider.retrofit.Constant;
import com.citycubeprovider.utility.App;
import com.citycubeprovider.utility.DataManager;
import com.citycubeprovider.utility.NetworkReceiver;
import com.citycubeprovider.utility.SessionManager;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleActivity extends AppCompatActivity {
    public String TAG = "AddVehicleActivity";
    ActivityAddVehicleBinding binding;
    CityCubeProviderInterface apiInterface;
    NetworkReceiver receiver;
    String car_id = "", brand_id = "", model_id = "";
    ArrayList<CarListModel.Result> carArrayList;
    ArrayList<BrandListModel.Result> brandArrayList;
    ArrayList<ModListModel.Result> modArrayList;
    String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_vehicle);
        initViews();
        getCarList();
    }

    private void initViews() {
        binding.btnContinue.setOnClickListener(v -> { validation();});

        carArrayList = new ArrayList<>();
        brandArrayList = new ArrayList<>();
        modArrayList = new ArrayList<>();

        binding.rlImg.setOnClickListener(v -> {
            if (checkPermisssionForReadStorage())
                showImageSelection();
        });


        binding.spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                car_id = carArrayList.get(position).id;
               // getCardBand(car_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

     /*   binding.spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                brand_id = brandArrayList.get(position).id;
                getModel(brand_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model_id = modArrayList.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


    }

    private void getCarList() {
        Call<CarListModel> signupCall = apiInterface.getCarList();
        signupCall.enqueue(new Callback<CarListModel>() {
            @Override
            public void onResponse(Call<CarListModel> call, Response<CarListModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    CarListModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Car Type Response :" + responseString);
                    if (data.status.equals("1")) {
                        carArrayList.clear();

                        carArrayList.addAll(data.result);
                        binding.spinnerModel.setAdapter(new AdapterType(AddVehicleActivity.this, carArrayList));
                        car_id =  carArrayList.get(0).id;
                      //  getCardBand(carArrayList.get(0).id);
                    } else if (data.status.equals("0")) {
                        App.showToast(AddVehicleActivity.this, data.message, Toast.LENGTH_SHORT);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CarListModel> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });
    }

    private void getCardBand(String carBand) {
        Map<String, String> map = new HashMap<>();
        map.put("car_type_id", carBand);
        Log.e(TAG, "Car Brand Request :" + carBand);
        Call<BrandListModel> signupCall = apiInterface.cardBrandList(map);
        signupCall.enqueue(new Callback<BrandListModel>() {
            @Override
            public void onResponse(Call<BrandListModel> call, Response<BrandListModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    BrandListModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Car Brand Response :" + responseString);
                    if (data.status.equals("1")) {
                        brandArrayList.clear();
                        brandArrayList.addAll(data.result);
                        binding.spinnerBrand.setAdapter(new AdapterBrand(AddVehicleActivity.this, brandArrayList));
                        brand_id = brandArrayList.get(0).id;
                        getModel(brandArrayList.get(0).id);
                    } else if (data.status.equals("0")) {
                        App.showToast(AddVehicleActivity.this, data.message, Toast.LENGTH_SHORT);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BrandListModel> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });

    }

    private void getModel(String id) {
        Map<String, String> map = new HashMap<>();
        map.put("brand_id", id);
        Log.e(TAG, "Car Model Request  :" + id);
        Call<ModListModel> signupCall = apiInterface.modelList(map);
        signupCall.enqueue(new Callback<ModListModel>() {
            @Override
            public void onResponse(Call<ModListModel> call, Response<ModListModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    ModListModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Car Model Response :" + responseString);
                    if (data.status.equals("1")) {
                        modArrayList.clear();
                        modArrayList.addAll(data.result);
                        model_id = modArrayList.get(0).id;
                        binding.spinnerModel.setAdapter(new AdapterModel(AddVehicleActivity.this, modArrayList));
                    } else if (data.status.equals("0")) {
                        App.showToast(AddVehicleActivity.this, data.message, Toast.LENGTH_SHORT);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ModListModel> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });

    }


    public void showImageSelection() {

        final Dialog dialog = new Dialog(AddVehicleActivity.this);
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

        uriSavedImage = FileProvider.getUriForFile(AddVehicleActivity.this,
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
                str_image_path = DataManager.getInstance().getRealPathFromURI(AddVehicleActivity.this, data.getData());
                Glide.with(AddVehicleActivity.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivImg);

            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(AddVehicleActivity.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivImg);


            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(AddVehicleActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(AddVehicleActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(AddVehicleActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddVehicleActivity.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(AddVehicleActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(AddVehicleActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(AddVehicleActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(AddVehicleActivity.this,
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
                        Toast.makeText(AddVehicleActivity.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddVehicleActivity.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }


    public void validation() {
        if (str_image_path.equals("")) {
            App.showToast(AddVehicleActivity.this,getString(R.string.please_upload_vehicle_image),Toast.LENGTH_SHORT);
        }
        else if (car_id.equals(""))
            App.showToast(AddVehicleActivity.this, getString(R.string.please_select_service_type), Toast.LENGTH_SHORT);

        /*else if (brand_id.equals(""))
            App.showToast(AddVehicleActivity.this, getString(R.string.please_select_brand), Toast.LENGTH_SHORT);

        else if (model_id.equals(""))
            App.showToast(AddVehicleActivity.this, getString(R.string.please_select_model), Toast.LENGTH_SHORT);

        else if (binding.etYear.getText().toString().equals("")) {
            binding.etYear.setError(getString(R.string.please_enter_year));
            binding.etYear.setFocusable(true);
        } else if (binding.etNumberPlate.getText().toString().equals("")) {
            binding.etNumberPlate.setError(getString(R.string.please_enter_vehicle_number));
            binding.etNumberPlate.setFocusable(true);
        } else if (binding.etColor.getText().toString().equals("")) {
            binding.etColor.setError(getString(R.string.please_enter_vehicle_color));
            binding.etColor.setFocusable(true);
        }*/ else {
            if (NetworkReceiver.isConnected())
                AddVehicle();
            else
                App.showToast(AddVehicleActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT);
        }


    }


    public void AddVehicle() {
        DataManager.getInstance().showProgressMessage(AddVehicleActivity.this, getString(R.string.please_wait));
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("car_document", file.getName(), RequestBody.create(MediaType.parse("car_document/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), SessionManager.readString(AddVehicleActivity.this, Constant.driver_id, ""));
        RequestBody car_type_id = RequestBody.create(MediaType.parse("text/plain"), car_id);
        RequestBody brand = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody car_model = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody year_of_manufacture = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody car_number = RequestBody.create(MediaType.parse("text/plain"), binding.etNumberPlate.getText().toString());
        RequestBody car_color = RequestBody.create(MediaType.parse("text/plain"), "");


        Call<SignupModel> signupCall = apiInterface.addVehicle(user_id, car_type_id, brand, car_model, year_of_manufacture,
                car_number, car_color, filePart);
        signupCall.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(Call<SignupModel> call, Response<SignupModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SignupModel data = response.body();
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e(TAG, "ADD VEHICLE RESPONSE" + dataResponse);
                        //  App.showToast(AddVehicleAct.this, data.message, Toast.LENGTH_SHORT);
                        startActivity(new Intent(AddVehicleActivity.this,AddPersonalDocument.class));
                        finish();
                    } else if (data.status.equals("0")) {
                        App.showToast(AddVehicleActivity.this, data.message, Toast.LENGTH_SHORT);
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