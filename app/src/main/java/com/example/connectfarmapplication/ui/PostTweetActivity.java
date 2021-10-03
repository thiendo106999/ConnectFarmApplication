package com.example.connectfarmapplication.ui;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.models.New;
import com.example.connectfarmapplication.models.UploadVideoResponse;
import com.example.connectfarmapplication.models.Video;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostTweetActivity extends AppCompatActivity {


    private ActivityPostTweetBinding statusBinding;
    private ArrayList<Image>  listImage;
    private Video video;
    private final String TAG = "Post tweet activity";
    private static int RESULT_LOAD_VIDEO = 1;
    private static int RESULT_LOAD_IMAGE = 2;
    ArrayList<Uri> mArrayUri;
    Uri videoUri;



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
           // postNew();
            if (videoUri != null) {
                upload(new File(videoUri.getPath()));

            }
          //  finish();
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
            String[] filePathImage = {MediaStore.Images.Media.DATA};
            String[] filePathVideo = {MediaStore.Video.Media.DATA};
            mArrayUri = new ArrayList();
            Cursor cursor;
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case 1:
                        mArrayUri = null;
                        videoUri = data.getData();

                        cursor = getContentResolver().query(videoUri,
                                filePathVideo, null, null, null);
                        cursor.moveToFirst();
                        cursor.close();
                        Glide.with(PostTweetActivity.this)
                                .load(videoUri)
                                .placeholder(this.getDrawable(R.drawable.image))
                                .into(statusBinding.videoThumbnail);
                        statusBinding.rvImage.setVisibility(View.GONE);
                        statusBinding.videoThumbnail.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        videoUri = null;
                        // Get the Image from data
                        if (data.getData() != null) {
                            Uri mImageUri = data.getData();
                            mArrayUri.add(mImageUri);
                            // Get the cursor
                            cursor = getContentResolver().query(mImageUri,
                                    filePathImage, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();
                            cursor.close();
                        } else {
                            if (data.getClipData() != null) {
                                ClipData mClipData = data.getClipData();
                                for (int i = 0; i < mClipData.getItemCount(); i++) {
                                    ClipData.Item item = mClipData.getItemAt(i);
                                    Uri uri = item.getUri();
                                    mArrayUri.add(uri);
                                    // Get the cursor
                                    cursor = getContentResolver().query(uri, filePathImage, null, null, null);
                                    // Move to first row
                                    cursor.moveToFirst();
                                    cursor.close();
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
            Log.e(TAG, "onActivityResult: load image and video" + e.getMessage() );
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void upload(File file) {
        MediaType MEDIA_TYPE = MediaType.parse("video/mp4");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, file);
        DataClient client = APIUtils.getDataClient();
        client.uploadVideo(requestBody).enqueue(new Callback<UploadVideoResponse>() {
            @Override
            public void onResponse(Call<UploadVideoResponse> call, Response<UploadVideoResponse> response) {
                if (response.isSuccessful()) {
                    UploadVideoResponse rb = response.body();
                } else {
                    Log.i("mok", "F");
                }
            }
            @Override
            public void onFailure(Call<UploadVideoResponse> call, Throwable t) {
                t.printStackTrace();
                Log.i("mok", t.getCause() + "");
                Log.i("mok", "T");
                finish();
            }
        });
    }

    private void postNew() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("News");
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);

        HashMap<String, String> temp = new HashMap<>();
        SimpleDateFormat sf = new SimpleDateFormat("hh:mm dd-MM-yyyy");
        SimpleDateFormat sf1 = new SimpleDateFormat("dd-MM-yyyy");
        String date = sf.format(Calendar.getInstance().getTime());
        // String key_date = sf1.format(Calendar.getInstance().getTime());

        long miliseconds = Calendar.getInstance().getTimeInMillis();
        String uid = preferences.getString("token", "");
        String id_new = uid + miliseconds;
        String content = statusBinding.edtContent.getText().toString();

        New aNew = new New(id_new, content, "100", uid,
                listImage, "#heee", date);
        temp.put("id", id_new);
        temp.put("content", aNew.getContent());
        temp.put("likes", aNew.getLikes() + "");
        temp.put("user_id", aNew.getUser_id());
        temp.put("tag", getTags());
        temp.put("time", aNew.getTime());

        databaseReference.child(id_new).setValue(temp);

        //upload image or video
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference;
        if (listImage != null && listImage.size() > 0) {
            storageReference = storage.getReference("UploadImage");
            for (Image image : listImage) {
                Uri file = Uri.fromFile(new File(image.getImage()));
                storageReference.child(id_new).child(file.getLastPathSegment()).
                        putFile(file).addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child(id_new).child("list_image").push().setValue(uri.toString());
                    }
                }));
            }
        } else {
            storageReference = storage.getReference("UploadVideo");
            storageReference.child(id_new).child(video.getVideoUri().getLastPathSegment()).
                    putFile(video.getVideoUri()).addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    databaseReference.child(id_new).child("video").push().setValue(uri.toString());
                }
            }));
        }
    }
}