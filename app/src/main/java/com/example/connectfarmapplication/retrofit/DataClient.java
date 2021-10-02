package com.example.connectfarmapplication.retrofit;

import com.example.connectfarmapplication.models.Article;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DataClient {
    @GET("api/articles")
    Call<List<Article>> getArticles();
}
