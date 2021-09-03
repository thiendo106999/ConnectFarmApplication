package com.example.connectfarmapplication.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.connectfarmapplication.databinding.ActivityFullscreenBinding;

public class FullscreenActivity extends AppCompatActivity {


    ActivityFullscreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }


}