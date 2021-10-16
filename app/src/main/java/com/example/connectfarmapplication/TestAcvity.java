package com.example.connectfarmapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;

import com.example.connectfarmapplication.models.Article;
import com.example.connectfarmapplication.models.UploadResponse;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestAcvity extends AppCompatActivity {
    private DataClient client;
    String TAG = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_acvity);
        client = APIUtils.getDataClient();

//        client.getArticles().enqueue(new Callback<List<Article>>() {
//            @Override
//            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
//                if (response.isSuccessful()) {
//                    ArrayList<Article> list = (ArrayList<Article>) response.body();
//
//
//                }
//            }
//            @Override
//            public void onFailure(Call<List<Article>> call, Throwable t) {
//                Log.e(TAG, "onFailure: "+  t.getMessage());
//            }
//        });



    }
}