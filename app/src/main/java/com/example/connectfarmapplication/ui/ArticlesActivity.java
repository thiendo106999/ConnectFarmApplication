package com.example.connectfarmapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.ArticlesAdapter;
import com.example.connectfarmapplication.databinding.ActivityArticlesBinding;
import com.example.connectfarmapplication.models.Article;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlesActivity extends AppCompatActivity {
    private ActivityArticlesBinding binding;
    private String token;
    private ArrayList<Article> articleList;
    private ArticlesAdapter adapter;
    DataClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_articles);

        binding.progress.setVisibility(View.VISIBLE);
        token = Utils.getToken(this);

        client = APIUtils.getDataClient();
        client.getArticles().enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful()) {
                    articleList = (ArrayList<Article>) response.body();
                    adapter = new ArticlesAdapter(ArticlesActivity.this, articleList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ArticlesActivity.this);
                    linearLayoutManager.setReverseLayout(true);
                    binding.rcvListNew.setLayoutManager(linearLayoutManager);
                    binding.rcvListNew.setAdapter(adapter);
                    binding.progress.setVisibility(View.VISIBLE);
                } else {
                    Log.e("tag ", "onResponse: " + response.toString());
                    binding.progress.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                Log.e("TAG", "onFailure: "+  t.getMessage());
                binding.progress.setVisibility(View.VISIBLE);
            }
        });
        binding.showTweetActivity.setOnClickListener(v -> {
            startActivity(new Intent(ArticlesActivity.this, PostTweetActivity.class));
        });

        binding.back.setOnClickListener(v -> finish());
    }
}