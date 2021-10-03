package com.example.connectfarmapplication.retrofit;

import com.example.connectfarmapplication.models.Article;
import com.example.connectfarmapplication.models.UploadVideoResponse;
import com.example.connectfarmapplication.models.User;
import com.example.connectfarmapplication.models.UserInfo;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DataClient {
    @GET("api/articles")
    Call<List<Article>> getArticles();

    @FormUrlEncoded
    @POST("api/get_use_info")
    Call<UserInfo> getUserInfo(@Field("token") String token);

    @POST("api/create_user_info")
    Call<String> createUser(@Body HashMap<String, String> userInfo);

    @FormUrlEncoded
    @POST("api/check_new_user")
    Call<String> checkNewUser(@Field("token") String token);

    @Multipart
    @POST("api/upload_video")
    Call<UploadVideoResponse> uploadVideo(@Part("video") RequestBody video);

}
