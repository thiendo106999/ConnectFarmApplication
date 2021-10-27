package com.example.connectfarmapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.SellAdapter;
import com.example.connectfarmapplication.databinding.ActivityMyStorageBinding;
import com.example.connectfarmapplication.models.ProductResponse;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyStorageActivity extends AppCompatActivity {
    private ActivityMyStorageBinding binding;
    private SellAdapter adapter ;
    private String TAG = "My Storage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MyStorageActivity.this, R.layout.activity_my_storage);

        setupUI();
        binding.back.setOnClickListener(v -> finish());


    }

    private void setupUI() {
        String token = Utils.getToken(MyStorageActivity.this);
        DataClient client = APIUtils.getDataClient();
        client.getRegisteredProduct(token).enqueue(new Callback<ArrayList<ProductResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ProductResponse>> call, Response<ArrayList<ProductResponse>> response) {
                ArrayList<ProductResponse> products;
                if (response.isSuccessful()) {
                    products = response.body();
                    adapter = new SellAdapter(products, MyStorageActivity.this);
                    LinearLayoutManager manager = new LinearLayoutManager(MyStorageActivity.this);
                    binding.list.setLayoutManager(manager);
                    binding.list.setAdapter(adapter);
                } else {
                    Log.e(TAG, "onResponse: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ProductResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}