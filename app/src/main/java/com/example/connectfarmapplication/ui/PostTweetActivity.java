package com.example.connectfarmapplication.ui;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.UploadAdapter;
import com.example.connectfarmapplication.databinding.ActivityPostTweetBinding;
import com.example.connectfarmapplication.models.CreateArticleResponse;
import com.example.connectfarmapplication.models.UploadResponse;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.utils.RealPathUtil;
import com.example.connectfarmapplication.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostTweetActivity extends AppCompatActivity {


    private ActivityPostTweetBinding statusBinding;
    private final String TAG = "Post tweet activity";
    private final static int RESULT_LOAD_VIDEO = 1;
    private final static int RESULT_LOAD_IMAGE = 2;
    private ArrayList<Uri> mArrayUri;
    private Uri videoUri;
    private DataClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBinding = DataBindingUtil.setContentView(this, R.layout.activity_post_tweet);

        statusBinding.edtContent.setOnFocusChangeListener((v, hasFocus) -> {
            statusBinding.edtContent.setText("");
            statusBinding.btnDangBai.setEnabled(true);
            statusBinding.btnDangBai.setBackgroundColor(getApplication().getResources().getColor(R.color.yellowmain2));
        });

        statusBinding.imgBackStatus.setOnClickListener(v -> finish());

        statusBinding.layout.setOnClickListener(v -> Utils.hideSoftKeyboard(PostTweetActivity.this));

        statusBinding.btnDangBai.setOnClickListener(v -> {
            if (getTags().length() == 0) {
                Toast.makeText(PostTweetActivity.this, "Trường chủ đề không được bỏ trống", Toast.LENGTH_SHORT).show();
            } else {
                postArticle();
                finish();
            }
        });

        statusBinding.btnLoadAnh.setOnClickListener(v -> {
            loadImageFromGallery();
        });
        statusBinding.btnLoadVideo.setOnClickListener(v -> {
            loadVideoFromGallery(statusBinding.btnLoadVideo);
        });
    }

    private String getTags() {
        String result = "";
        if (statusBinding.rice.isChecked()) {
            result += "#luagao ";
        }
        if (statusBinding.vegetable.isChecked()) {
            result += "#hoamau ";
        }
        if (statusBinding.fruits.isChecked()) {
            result += "#traicay ";
        }
        if (statusBinding.phanBon.isChecked()) {
            result += "#phanbon ";
        }
        if (statusBinding.thuocTruSau.isChecked()) {
            result += "#thuoctrusau ";
        }
        if (statusBinding.nongCu.isChecked()) {
            result += "#nongcu ";
        }
        return result;
    }

    public void loadVideoFromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }
    public void loadImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case 1:
                        mArrayUri = null;
                        videoUri = data.getData();
                        Glide.with(PostTweetActivity.this)
                                .load(videoUri)
                                .placeholder(this.getDrawable(R.drawable.image))
                                .into(statusBinding.videoThumbnail);
                        statusBinding.rvImage.setVisibility(View.GONE);
                        statusBinding.videoThumbnail.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        videoUri = null;
                        mArrayUri = new ArrayList<>();
                        // Get the Image from data
                        if (data.getData() != null) {
                            Uri mImageUri = data.getData();
                            mArrayUri.add(mImageUri);
                        } else {
                            if (data.getClipData() != null) {
                                ClipData mClipData = data.getClipData();
                                for (int i = 0; i < mClipData.getItemCount(); i++) {
                                    ClipData.Item item = mClipData.getItemAt(i);
                                    Uri uri = item.getUri();
                                    mArrayUri.add(uri);
                                }
                            }
                        }
                        UploadAdapter uploadAdapter = new UploadAdapter(PostTweetActivity.this, mArrayUri);
                        statusBinding.rvImage.setAdapter(uploadAdapter);
                        statusBinding.rvImage.setLayoutManager(new LinearLayoutManager(PostTweetActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        statusBinding.rvImage.setVisibility(View.VISIBLE);
                        statusBinding.videoThumbnail.setVisibility(View.GONE);
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onActivityResult: load image and video" + e.getMessage());
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadFile(File file, String articleId) {
        // Parsing any Media type file
        String token = Utils.getToken(PostTweetActivity.this);
        String temp = token + System.currentTimeMillis() + file.getName().substring(file.getName().lastIndexOf("."));
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), temp);
        RequestBody article_id = RequestBody.create(MediaType.parse("text/plain"), articleId);

        DataClient client = APIUtils.getDataClient();
        client.uploadImage(fileToUpload, filename, article_id).enqueue(new Callback<UploadResponse>() {
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
    }

    public void postArticle() {
        client = APIUtils.getDataClient();
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        String token = preferences.getString("token", "");
        String content = statusBinding.edtContent.getText().toString().trim();
        String tags = getTags();
        String media_id = token + System.currentTimeMillis();
        if (mArrayUri != null && mArrayUri.size() > 0) {
            uploadImage(media_id);
        }

        client.uploadArticle(token, content, tags, media_id).enqueue(new Callback<CreateArticleResponse>() {
            @Override
            public void onResponse(Call<CreateArticleResponse> call, Response<CreateArticleResponse> response) {
                if (response.isSuccessful()) {
                    CreateArticleResponse body = response.body();
                    if (videoUri != null) {
                        uploadFile(new File(RealPathUtil.getPath(PostTweetActivity.this, videoUri)), body.getArticle_id());
                    }
                    String articles_id = String.valueOf(body.getArticle_id());
                    Log.e(TAG, "onResponse: " + articles_id);
                }
            }

            @Override
            public void onFailure(Call<CreateArticleResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void uploadImage(String articles_id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("News");
        for (Uri file : mArrayUri) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference("UploadImage");

            storageReference.child(articles_id).child(file.getLastPathSegment()).
                    putFile(file).addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    databaseReference.child(articles_id).child("list_image").push().setValue(uri.toString());
                }
            }));

        }

    }
}