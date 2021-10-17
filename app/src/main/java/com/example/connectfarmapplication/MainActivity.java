package com.example.connectfarmapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.connectfarmapplication.databinding.ActivityMainBinding;
import com.example.connectfarmapplication.models.UserInfo;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.ui.ArticlesActivity;
import com.example.connectfarmapplication.ui.LoginActivity;
import com.example.connectfarmapplication.ui.PostActivity;
import com.example.connectfarmapplication.ui.PriceListActivity;
import com.example.connectfarmapplication.ui.ProfileActivity;
import com.example.connectfarmapplication.ui.SellActivity;
import com.example.connectfarmapplication.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private final String TAG = "main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);

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

        mainBinding.btnPrice.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, PriceListActivity.class));
        });

        mainBinding.btnPost.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, ArticlesActivity.class));
        });

        mainBinding.btnShop.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, SellActivity.class));
        });

        mainBinding.infos.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
    }
}