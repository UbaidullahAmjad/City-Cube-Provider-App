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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.citycubeprovider.BuildConfig;
import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ActivityEditProfileBinding;
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
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileAct extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;
    CityCubeProviderInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_profile);
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        initViews();
    }

    private void initViews() {
        binding.toolbar.ivBack.setOnClickListener(v -> { finish(); });
        binding.toolbar.tvTitle.setText(getString(R.string.edit_profile));

        setUserInfo();

        binding.ivProfile.setOnClickListener(v -> {
            if(checkPermisssionForReadStorage())
                showImageSelection();
        });


     //   binding.tvChangePassword.setOnClickListener(v -> { startActivity(new Intent(EditProfileActivity.this,ChangePasswordActivity.class)); });

        binding.btnUpdate.setOnClickListener(v -> {validation();});

    }

    private void validation() {
        if (binding.etUserName.getText().toString().equals("")) {
            binding.etUserName.setError(getString(R.string.please_enter_username));
            binding.etUserName.setFocusable(true);
        }  else if (binding.etMobile.getText().toString().equals("")) {
            binding.etMobile.setError(getString(R.string.please_enter_mobile_number));
            binding.etMobile.setFocusable(true);
        }
        else {
            if(NetworkReceiver.isConnected()) updateProfile();
            else App.showToast(EditProfileAct.this,getString(R.string.network_failure), Toast.LENGTH_LONG);
        }
    }

    public void setUserInfo(){
        binding.etUserName.setText(DataManager.getInstance().getUserData(EditProfileAct.this).result.userName);
        binding.etEmail.setText(DataManager.getInstance().getUserData(EditProfileAct.this).result.email);
        binding.etMobile.setText(DataManager.getInstance().getUserData(EditProfileAct.this).result.mobile);
        binding.ccp.setCountryForPhoneCode(Integer.parseInt(DataManager.getInstance().getUserData(EditProfileAct.this).result.phoneCode));

        Glide.with(EditProfileAct.this)
                .load(DataManager.getInstance().getUserData(EditProfileAct.this).result.image)
                .override(80,80)
                .apply(new RequestOptions().placeholder(R.drawable.user_default))
                .into(binding.ivProfile);

    }

    public void showImageSelection() {

        final Dialog dialog = new Dialog(EditProfileAct.this);
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

        uriSavedImage = FileProvider.getUriForFile(EditProfileAct.this,
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
            Log.e("Result_code", requestCode + "");
            if (requestCode == SELECT_FILE) {
                str_image_path = DataManager.getInstance().getRealPathFromURI(EditProfileAct.this, data.getData());
                Glide.with(EditProfileAct.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivProfile);

            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(EditProfileAct.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivProfile);
            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(EditProfileAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(EditProfileAct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(EditProfileAct.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(EditProfileAct.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(EditProfileAct.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(EditProfileAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(EditProfileAct.this,
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
                        Toast.makeText(EditProfileAct.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileAct.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }


    private void updateProfile() {
        MultipartBody.Part filePart;
        DataManager.getInstance().showProgressMessage(EditProfileAct.this, getString(R.string.please_wait));
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody f_name = RequestBody.create(MediaType.parse("text/plain"), binding.etUserName.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), binding.etEmail.getText().toString());
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), binding.etMobile.getText().toString());
        RequestBody country_code = RequestBody.create(MediaType.parse("text/plain"), binding.ccp.getSelectedCountryCode());
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), DataManager.getInstance().getUserData(EditProfileAct.this).result.id);


        Call<SignupModel> signupCall = apiInterface.profileUpdate(f_name, email, mobile, country_code,user_id, filePart);
        signupCall.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(Call<SignupModel> call, Response<SignupModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SignupModel data = response.body();
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        SessionManager.writeString(EditProfileAct.this, Constant.USER_INFO, dataResponse);
                        finish();
                    } else if (data.status.equals("0")) {
                        App.showToast(EditProfileAct.this, data.message, Toast.LENGTH_SHORT);
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
