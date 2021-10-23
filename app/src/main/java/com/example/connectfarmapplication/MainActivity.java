package com.example.connectfarmapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.connectfarmapplication.databinding.ActivityMainBinding;
import com.example.connectfarmapplication.models.UserInfo;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.ui.ArticlesActivity;
import com.example.connectfarmapplication.ui.PriceListActivity;
import com.example.connectfarmapplication.ui.ProfileActivity;
import com.example.connectfarmapplication.ui.SellActivity;
import com.example.connectfarmapplication.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private final String TAG = "main";
    private final int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        setupUI();
        //    setupPermission();

        mainBinding.btnPrice.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PriceListActivity.class));
        });

        mainBinding.btnPost.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ArticlesActivity.class));
        });

        mainBinding.btnShop.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SellActivity.class));
        });

        mainBinding.infos.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
    }

    private void setupPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Log.e(TAG, "setupPermission: " + PackageManager.PERMISSION_GRANTED);
            Log.e(TAG, "setupPermission: manifest" + Manifest.permission.READ_EXTERNAL_STORAGE);

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.e(TAG, "setupPermission: " + Manifest.permission.READ_EXTERNAL_STORAGE);
                new AlertDialog.Builder(this)
                        .setTitle("Cấp quyền")
                        .setMessage("Bạn cần phải cấp quyền để truy cập vào bộ nhớ")
                        .setPositiveButton("Đồng ý", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, STORAGE_PERMISSION_CODE))
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss()).create().show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, STORAGE_PERMISSION_CODE);
            }
        }
    }


    private void setupUI() {
        String token = Utils.getToken(this);
        DataClient client = APIUtils.getDataClient();
        client.getUserInfo(token).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    UserInfo userInfo = response.body();
                    mainBinding.name.setText("Xin chào: " + userInfo.getName());
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });
    }

}

