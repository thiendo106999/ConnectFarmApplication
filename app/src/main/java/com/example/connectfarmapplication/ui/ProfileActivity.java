package com.example.connectfarmapplication.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.connectfarmapplication.MainActivity;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ActivityProfileBinding;
import com.example.connectfarmapplication.models.UploadResponse;
import com.example.connectfarmapplication.models.UserInfo;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.utils.RealPathUtil;
import com.example.connectfarmapplication.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setProfile();
        binding.btnBack.setOnClickListener(v -> finish());

        binding.chooseAvatar.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });

        binding.logout.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", null);
            editor.apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        });
    }

    private void setProfile() {
        String token = Utils.getToken(this);
        DataClient client = APIUtils.getDataClient();
        client.getUserInfo(token).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    UserInfo userInfo = response.body();
                    binding.setUser(userInfo);
                    Log.e(TAG, "onResponse: " + response.body());
                } else {
                    Log.e(TAG, "onResponseFails: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        String token = preferences.getString("token", "");
        if (resultCode == RESULT_OK) {
            Uri chosenImageUri = data.getData();
            Bitmap mBitmap = null;
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), chosenImageUri);
                binding.avatar.setImageBitmap(mBitmap);
                File file = new File(RealPathUtil.getPath(ProfileActivity.this, chosenImageUri));
                uploadImage(file, token);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void uploadImage(File file, String token) {
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        String temp = token + file.getName().substring(file.getName().lastIndexOf("."));
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), temp);
        RequestBody access_token = RequestBody.create(MediaType.parse("text/plain"), token);

        DataClient client = APIUtils.getDataClient();
        client.uploadAvatar(fileToUpload, filename, access_token).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                UploadResponse message = response.body();
                Log.e(TAG, "onResponse: " + message.getMessage());
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: upload" + t.getMessage());
            }
        });
        Toast.makeText(ProfileActivity.this, "Cập nhật ảnh đại diện thành công.", Toast.LENGTH_SHORT).show();
    }
}