package com.example.connectfarmapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.connectfarmapplication.databinding.ActivityMainBinding;
import com.example.connectfarmapplication.ui.LoginActivity;
import com.example.connectfarmapplication.ui.PostActivity;
import com.example.connectfarmapplication.ui.PriceListActivity;
import com.example.connectfarmapplication.ui.SellActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);

        mainBinding.btnPrice.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, PriceListActivity.class));
        });

        mainBinding.btnPost.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        });

        mainBinding.btnShop.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, SellActivity.class));
        });

        mainBinding.logout.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", null);
            editor.apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}