package com.example.connectfarmapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ActivityMainSellBinding;

public class MainSellActivity extends AppCompatActivity {
    ActivityMainSellBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainSellActivity.this, R.layout.activity_main_sell);

        binding.back.setOnClickListener(v -> finish());
        binding.showAll.setOnClickListener(v -> {
            startActivity(new Intent(MainSellActivity.this, SellActivity.class));
        });
        binding.registered.setOnClickListener(v -> {
            startActivity(new Intent(MainSellActivity.this, RegisteredProductActivity.class));
        });
        binding.storage.setOnClickListener(v -> {
            startActivity(new Intent(MainSellActivity.this, MyStorageActivity.class));
        });
    }
}