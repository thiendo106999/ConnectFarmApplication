package com.example.connectfarmapplication.ui;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.UploadAdapter;
import com.example.connectfarmapplication.databinding.ActivityPostTweetBinding;
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.models.New;
import com.example.connectfarmapplication.utils.StorageUtil;
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

public class PostTweetActivity extends AppCompatActivity {


    private ActivityPostTweetBinding statusBinding;
    private UploadAdapter uploadAdapter;
    private ArrayList<Image> listImageResourse, listImage;
    private final String TAG = "Post tweet activity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBinding = DataBindingUtil.setContentView(this, R.layout.activity_post_tweet);

        listImageResourse = getFilePaths();

        listImage = new ArrayList<>();

        uploadAdapter = new UploadAdapter(this, listImageResourse, 1);

        statusBinding.rvListImage.setAdapter(uploadAdapter);
        statusBinding.rvListImage.setLayoutManager(new GridLayoutManager(this, 3));

        statusBinding.edStatus.setOnFocusChangeListener((v, hasFocus) -> {
            statusBinding.edStatus.setText("");
            statusBinding.btnDangBai.setEnabled(true);
            statusBinding.btnDangBai.setBackgroundColor(getApplication().getResources().getColor(R.color.yellowmain2));
        });

        statusBinding.imgBackStatus.setOnClickListener(v -> finish());
        statusBinding.layout.setOnClickListener(v -> Utils.hideSoftKeyboard(PostTweetActivity.this));

        statusBinding.btnLoadAnh.setOnClickListener(v -> {
            getFilePaths();
            statusBinding.chooseImage.setVisibility(View.VISIBLE);
        });

        statusBinding.btChoose.setOnClickListener(v -> {
            for (Image image : listImageResourse) {
                if (image.isChosen() && !listImage.contains(image)) {
                    listImage.add(image);
                }

            }
            uploadAdapter = new UploadAdapter(PostTweetActivity.this, listImage, 2);
            statusBinding.rvImage.setAdapter(uploadAdapter);
            statusBinding.rvImage.setLayoutManager(new LinearLayoutManager(PostTweetActivity.this, LinearLayoutManager.HORIZONTAL, false));
            statusBinding.chooseImage.setVisibility(View.GONE);
        });

        statusBinding.ivBack.setOnClickListener(v -> statusBinding.chooseImage.setVisibility(View.GONE));

        statusBinding.btnDangBai.setOnClickListener(v -> {
            postNew();
            finish();
        });

        String[] allPath = StorageUtil.getStorageDirectories(this);
        Log.e(TAG, "onCreate: "+ allPath.toString());
    }


    public ArrayList<Image> getFilePaths() {

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
        String content = statusBinding.edStatus.getText().toString();

        New aNew = new New(id_new, content, "100", uid,
                listImage, "#heee", date);
        temp.put("id", id_new);
        temp.put("content", aNew.getContent());
        temp.put("likes", aNew.getLikes() + "");
        temp.put("user_id", aNew.getUser_id());
        temp.put("tag", getTags());
        temp.put("time", aNew.getTime());

        databaseReference.child(id_new).setValue(temp);

        //upload image
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("UploadImage");

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
}