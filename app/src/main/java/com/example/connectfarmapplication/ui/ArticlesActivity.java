package com.example.connectfarmapplication.ui;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.ArticlesAdapter;
import com.example.connectfarmapplication.databinding.ActivityArticlesBinding;
import com.example.connectfarmapplication.models.Article;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.utils.EndlessRecyclerViewScrollListener;
import com.example.connectfarmapplication.utils.Utils;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

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
    boolean isLoading = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_articles);

        setUpUI();
        binding.progress.setVisibility(View.VISIBLE);
        token = Utils.getToken(this);

        client = APIUtils.getDataClient();
        client.getArticles(token).enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful()) {
                    articleList = (ArrayList<Article>) response.body();
                    adapter = new ArticlesAdapter(ArticlesActivity.this, articleList, token, 0);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ArticlesActivity.this);
                    linearLayoutManager.setReverseLayout(true);
                    binding.rcvListNew.setLayoutManager(linearLayoutManager);
                    binding.rcvListNew.setAdapter(adapter);
                    binding.rcvListNew.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                        }
                    });
                    binding.progress.setVisibility(View.GONE);
                } else {
                    binding.progress.setVisibility(View.GONE);
                    Log.e("tag ", "onResponse: " + response.toString());
                }
            }
            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                Log.e("TAG", "onFailure: "+  t.getMessage());
                binding.progress.setVisibility(View.GONE);
            }
        });
        binding.showTweetActivity.setOnClickListener(v -> {
            startActivity(new Intent(ArticlesActivity.this, PostTweetActivity.class));
        });

        binding.back.setOnClickListener(v -> finish());

        binding.showFilter.setOnClickListener(v->{
            if (binding.showTags.getVisibility() == View.VISIBLE) {
                binding.showTags.setVisibility(View.GONE);
            } else binding.showTags.setVisibility(View.VISIBLE);

        });

        binding.reload.setOnClickListener(v->{
            binding.progress.setVisibility(View.VISIBLE);
            client.getArticleDependOnTags(getTags()).enqueue(new Callback<List<Article>>() {
                @Override
                public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                    if (response.isSuccessful()) {
                        articleList = (ArrayList<Article>) response.body();
                        adapter = new ArticlesAdapter(ArticlesActivity.this, articleList, token, 0);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ArticlesActivity.this);
                        linearLayoutManager.setReverseLayout(true);
                        binding.rcvListNew.setLayoutManager(linearLayoutManager);
                        binding.rcvListNew.setAdapter(adapter);
                    } else {
                        Log.e("tag ", "onResponse: " + response.toString());
                    }
                    binding.progress.setVisibility(View.GONE);
                }
                @Override
                public void onFailure(Call<List<Article>> call, Throwable t) {
                    Log.e("TAG", "onFailure: "+  t.getMessage());
                    binding.progress.setVisibility(View.GONE);
                }
            });
        });
    }

    private void setUpUI() {
        DataClient client = APIUtils.getDataClient();
        client.getRuleWritable(Utils.getToken(ArticlesActivity.this)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body().equals("true")) {
                    binding.layoutPost.setVisibility(View.VISIBLE);
                } else binding.layoutPost.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private String getTags() {
        String result = "";
        if (binding.rice.isChecked()) {
            result += "#luagao ";
        }
        if (binding.vegetable.isChecked()) {
            result += "#hoamau ";
        }
        if (binding.fruits.isChecked()) {
            result += "#traicay ";
        }
        if (binding.phanBon.isChecked()) {
            result += "#phanbon ";
        }
        if (binding.thuocTruSau.isChecked()) {
            result += "#thuoctrusau ";
        }
        if (binding.nongCu.isChecked()) {
            result += "#nongcu ";
        }
        return result;
    }

}