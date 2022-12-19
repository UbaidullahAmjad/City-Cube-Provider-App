package com.citycubeprovider.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.citycubeprovider.R;
import com.citycubeprovider.adapters.HomePagerAdapter;
import com.citycubeprovider.databinding.ActivityHomeBinding;
import com.citycubeprovider.retrofit.ApiClient;
import com.citycubeprovider.retrofit.CityCubeProviderInterface;
import com.citycubeprovider.retrofit.Constant;
import com.citycubeprovider.utility.App;
import com.citycubeprovider.utility.DataManager;
import com.citycubeprovider.utility.MyService;
import com.citycubeprovider.utility.NetworkReceiver;
import com.citycubeprovider.utility.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    public String TAG = "HomeActivity";
    ActivityHomeBinding binding;
    HomePagerAdapter  adapterViewPager;
    CityCubeProviderInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(CityCubeProviderInterface.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        initViews();
    }


    private void initViews() {

        if(DataManager.getInstance().getUserData(HomeActivity.this).result.onlineStatus.equals("ONLINE"))
            binding.dashboard.switchChange.setChecked(true);
        else binding.dashboard.switchChange.setChecked(false);


        binding.drawerLayout.tvUserName.setText(DataManager.getInstance().getUserData(HomeActivity.this).result.userName);
        binding.drawerLayout.tvEmail.setText(DataManager.getInstance().getUserData(HomeActivity.this).result.email);

        binding.dashboard.ivMenu.setOnClickListener(v -> { navmenu(); });

        binding.drawerLayout.rlHome.setOnClickListener(v -> { navmenu(); });

        binding.drawerLayout.rlOrderHistory.setOnClickListener(v -> { startActivity(new Intent(HomeActivity.this,MyRideActivity.class)); });

        binding.drawerLayout.rlMyRviews.setOnClickListener(v -> { startActivity(new Intent(HomeActivity.this,MyReview.class)); });

        binding.drawerLayout.rlMyProfile.setOnClickListener(v -> { startActivity(new Intent(this,MyProfileActivity.class)); });

        binding.drawerLayout.rlSettings.setOnClickListener(v -> { startActivity(new Intent(this,SettingsActivity.class)); });

        binding.drawerLayout.rlWallet.setOnClickListener(v -> { startActivity(new Intent(this,WalletActivity.class)); });

        binding.drawerLayout.rlPromotion.setOnClickListener(v -> { startActivity(new Intent(this,PromotionActivity.class)); });

        binding.drawerLayout.rlShare.setOnClickListener(v -> { startActivity(new Intent(this,ShareActivity.class)); }                                    );

        binding.drawerLayout.rlLogout.setOnClickListener(v -> { SessionManager.clear(HomeActivity.this,DataManager.getInstance().getUserData(HomeActivity.this).result.id); });


        binding.dashboard.tabLayout.addTab(binding.dashboard.tabLayout.newTab().setText(R.string.available_runs));
        binding.dashboard.tabLayout.addTab(binding.dashboard.tabLayout.newTab().setText(R.string.my_runs));
        binding.dashboard.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapterViewPager = new HomePagerAdapter(this,getSupportFragmentManager(),binding.dashboard.tabLayout.getTabCount());

        binding.dashboard.vpPager.setAdapter(adapterViewPager);

        binding.dashboard.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.dashboard.vpPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        binding.dashboard.switchChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(NetworkReceiver.isConnected())
                        changeStatus(DataManager.getInstance().getUserData(HomeActivity.this).result.id,"ONLINE");
                    else App.showToast(HomeActivity.this,getString(R.string.network_failure),Toast.LENGTH_SHORT);
                }
                else {
                    if(NetworkReceiver.isConnected())
                        changeStatus(DataManager.getInstance().getUserData(HomeActivity.this).result.id,"OFFLINE");
                    else App.showToast(HomeActivity.this,getString(R.string.network_failure),Toast.LENGTH_SHORT);
                }
            }
        });


    }

    public void navmenu() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            binding.drawer.openDrawer(GravityCompat.START);
        }
    }



    private void changeStatus(String id, String status) {
        Map<String,String> map = new HashMap<>();
        map.put("user_id",id);
        map.put("status",status);
        //  map.put("type",type);
        Log.e(TAG,"Upldate Driver Status Request "+map);
        Call<Map<String,String>> loginCall = apiInterface.updateStatus(map);
        loginCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    SessionManager.writeString(HomeActivity.this, Constant.driver_status,status);
                    Log.e(TAG,"Upldate Driver Status Response :"+responseString);
                    App.showToast(HomeActivity.this,status, Toast.LENGTH_SHORT);

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

    @Override
    protected void onResume() {
        super.onResume();
        ContextCompat.startForegroundService(getApplicationContext(),new Intent(HomeActivity.this, MyService.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MyService.class));

    }
}