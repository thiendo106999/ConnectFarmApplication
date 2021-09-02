package com.example.connectfarmapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.example.connectfarmapplication.databinding.ActivityMainBinding;
import com.example.connectfarmapplication.ui.ShowPriceActivity;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);

        mainBinding.btnPrice.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, ShowPriceActivity.class));
        });
    }
}