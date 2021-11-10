package com.example.connectfarmapplication.retrofit;

import com.example.connectfarmapplication.adapters.AgriculturalResponse;
import com.example.connectfarmapplication.models.Article;
import com.example.connectfarmapplication.models.CreateArticleResponse;
import com.example.connectfarmapplication.models.DateAndProvinceResponse;
import com.example.connectfarmapplication.models.PersonalPageResponse;
import com.example.connectfarmapplication.models.ProductResponse;
import com.example.connectfarmapplication.models.UploadResponse;
import com.example.connectfarmapplication.models.UploadVideoResponse;
import com.example.connectfarmapplication.models.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import okhttp3.RequestBody;
import retrofit2.http.Path;

public interface DataClient {
    @GET("api/articles")
    Call<List<Article>> getArticles();

    @FormUrlEncoded
    @POST("api/articles")
    Call<List<Article>> getArticleDependOnTags(@Field("tags") String tags);

    @FormUrlEncoded
    @POST("api/personal_page")
    Call<PersonalPageResponse> getPersonalPage(@Field("access_token") String token);

    @FormUrlEncoded
    @POST("api/get_user_info")
    Call<UserInfo> getUserInfo(@Field("token") String token);

    @POST("api/create_user_info")
    Call<String> createUser(@Body HashMap<String, String> userInfo);

    @FormUrlEncoded
    @POST("api/check_new_user")
    Call<String> checkNewUser(@Field("token") String token);

    @Multipart
    @POST("api/upload_file")
    Call<UploadResponse> uploadImage(@Part MultipartBody.Part file, @Part("file_name") RequestBody fileName, @Part("article_id") RequestBody article_id);

    @Multipart
    @POST("api/upload_avatar")
    Call<UploadResponse> uploadAvatar(@Part MultipartBody.Part file, @Part("file_name") RequestBody fileName, @Part("access_token") RequestBody token);

    @FormUrlEncoded
    @POST("api/upload_article")
    Call<CreateArticleResponse> uploadArticle(@Header("access_token") String access_token , @Field("content") String content, @Field("tags") String tags, @Field("media_id") String media_id);

    @FormUrlEncoded
    @POST("api/price_list")
    Call<ArrayList<AgriculturalResponse>> getPriceAgricultural(@Field("date") String date, @Field("kind") String kind);

    @POST("api/get_data_price_list")
    Call<DateAndProvinceResponse> getDatesAndProvinces();

    @GET("api/set_up_spinner_sell")
    Call<DateAndProvinceResponse> setupSpinnerSell();

    @FormUrlEncoded
    @POST("api/products")
    Call<ArrayList<ProductResponse>> getProducts(@Field("date") String date, @Field("kind_id") String kind, @Field("province") String province);

    @Multipart
    @POST("api/registered_product")
    Call<UploadResponse> registeredProduct(@Part MultipartBody.Part file, @Part("file_name") RequestBody fileName, @Part("name") RequestBody name, @Part("access_token") RequestBody token,
                                           @Part("phone_number") RequestBody phone_number, @Part("address") RequestBody address,
                                           @Part("date") RequestBody date, @Part("kind") RequestBody kind, @Part("hexta") RequestBody hexta);
    @FormUrlEncoded
    @POST("api/get_registered_product")
    Call<ArrayList<ProductResponse>> getRegisteredProduct(@Field("access_token") String access_token);

    @DELETE("api/delete/{id}")
    Call<UploadResponse> deleteBook(@Path("id") int productId);

}
