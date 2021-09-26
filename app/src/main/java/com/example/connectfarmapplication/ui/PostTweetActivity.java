package com.example.connectfarmapplication.ui;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.UploadAdapter;
import com.example.connectfarmapplication.adapters.VideoAdapter;
import com.example.connectfarmapplication.databinding.ActivityPostTweetBinding;
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.models.New;
import com.example.connectfarmapplication.models.Video;
import com.example.connectfarmapplication.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class PostTweetActivity extends AppCompatActivity implements SurfaceHolder.Callback{


    private ActivityPostTweetBinding statusBinding;
    private UploadAdapter uploadAdapter;
    private ArrayList<Image> listImageResource, listImage;
    private ArrayList<Video> listVideoResource;
    private Video video;
    private final String TAG = "Post tweet activity";
    private File storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBinding = DataBindingUtil.setContentView(this, R.layout.activity_post_tweet);

        listImageResource = getImagePaths();
        listVideoResource = getVideoPaths();


        loadImages();
        loadVideos();

        statusBinding.edtContent.setOnFocusChangeListener((v, hasFocus) -> {
            statusBinding.edtContent.setText("");
            statusBinding.btnDangBai.setEnabled(true);
            statusBinding.btnDangBai.setBackgroundColor(getApplication().getResources().getColor(R.color.yellowmain2));
        });

        statusBinding.imgBackStatus.setOnClickListener(v -> finish());

        statusBinding.layout.setOnClickListener(v -> Utils.hideSoftKeyboard(PostTweetActivity.this));

        statusBinding.btChoose.setOnClickListener(v -> {
            listImage = new ArrayList<>();
            for (Image image : listImageResource) {
                if (image.isChosen() && !listImage.contains(image)) {
                    listImage.add(image);
                }
            }
            uploadAdapter = new UploadAdapter(PostTweetActivity.this, listImage, 2);
            statusBinding.rvImage.setAdapter(uploadAdapter);
            statusBinding.rvImage.setLayoutManager(new LinearLayoutManager(PostTweetActivity.this, LinearLayoutManager.HORIZONTAL, false));
            statusBinding.chooseImage.setVisibility(View.GONE);
            statusBinding.rvImage.setVisibility(View.VISIBLE);
        });
        statusBinding.btChooseVideo.setOnClickListener(v -> {

            for (Video temp : listVideoResource) {
                if (temp.isChoose()) {
                    video = temp;
                    break;
                }
            }
            Log.e(TAG, "onCreate: " + video.getVideoTitle());
            statusBinding.chooseVideo.setVisibility(View.GONE);

            Glide.with(PostTweetActivity.this)
                    .load(video.getVideoUri()) // or URI/path
                    .into(statusBinding.videoThumbnail);
            statusBinding.videoThumbnail.setVisibility(View.VISIBLE);
        });

        statusBinding.ivBackImage.setOnClickListener(v -> statusBinding.chooseImage.setVisibility(View.GONE));
        statusBinding.ivBackVideo.setOnClickListener(v -> statusBinding.chooseVideo.setVisibility(View.GONE));

        statusBinding.btnDangBai.setOnClickListener(v -> {
            postNew();
            finish();
        });

        statusBinding.btnLoadAnh.setOnClickListener(v -> {
            statusBinding.chooseImage.setVisibility(View.VISIBLE);
        });
        statusBinding.btnLoadVideo.setOnClickListener(v -> {
            statusBinding.chooseVideo.setVisibility(View.VISIBLE);
        });
    }


    private void loadImages() {
        uploadAdapter = new UploadAdapter(this, listImageResource, 1);
        statusBinding.rvListImage.setAdapter(uploadAdapter);
        statusBinding.rvListImage.setLayoutManager(new GridLayoutManager(this, 3));
    }

    public void loadVideos() {
        VideoAdapter adapter = new VideoAdapter(this, listVideoResource);
        statusBinding.rvListVideo.setAdapter(adapter);
        statusBinding.rvListVideo.setLayoutManager(new LinearLayoutManager(PostTweetActivity.this, LinearLayoutManager.VERTICAL, false));
    }

    public ArrayList<Video> getVideoPaths() {
        ArrayList<Video> videos = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                Video videoModel = new Video();
                videoModel.setVideoTitle(title);
                videoModel.setVideoUri(Uri.parse("file://" + data));
                videoModel .setVideoDuration(convertMillisecondToTime(Long.parseLong(duration)));
                videos.add(videoModel);

            } while (cursor.moveToNext());
        }
        return videos;
    }

    public ArrayList<Image> getImagePaths() {
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<>();
        ArrayList<Image> resultIAV = new ArrayList<>();

        String[] directories = null;
        if (u != null) {
            c = managedQuery(u, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);

                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));

                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();

                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                    ) {
                        String path = imagePath.getAbsolutePath();
                        resultIAV.add(new Image(path));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return resultIAV;
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
        return result;
    }

    @SuppressLint("DefaultLocale")
    private String convertMillisecondToTime(long duration) {
        long seconds = (duration / 1000) % 60;
        long minutes = ((duration / (1000 * 60)) % 60);

        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}