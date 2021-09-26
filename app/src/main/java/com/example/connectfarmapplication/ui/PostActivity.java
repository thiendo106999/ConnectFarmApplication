package com.example.connectfarmapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.NewsAdapter;
import com.example.connectfarmapplication.databinding.ActivityPostBinding;
import com.example.connectfarmapplication.models.New;
import com.example.connectfarmapplication.models.User;
import com.example.connectfarmapplication.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    ActivityPostBinding postBinding;
    private NewsAdapter adapter;
    private ArrayList<New> listNews;
    private String token;
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postBinding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        postBinding.progress.setVisibility(View.VISIBLE);
        token = Utils.getToken(this);

        if (!token.equals("1")) {
            getUser(token);
            listNews = getListNew();
        }

        postBinding.showTweetActivity.setOnClickListener(v -> {
            startActivity(new Intent(PostActivity.this, PostTweetActivity.class));
        });
    }

    private ArrayList<New> getListNew() {
        ArrayList<New> listNews = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("News");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listNews.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        New news = s.getValue(New.class);
                        listNews.add(news);
                    }
                    adapter = new NewsAdapter(PostActivity.this, listNews, NewsAdapter.FRAGMENT_NEW);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostActivity.this);
                    linearLayoutManager.setReverseLayout(true);
                    postBinding.rcvListNew.setLayoutManager(linearLayoutManager);
                    postBinding.rcvListNew.setAdapter(adapter);
                }
                postBinding.progress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                postBinding.progress.setVisibility(View.GONE);
            }
        });
        return listNews;
    }

    private void getUser(String token) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(token);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(PostActivity.this)
                        .load(user.getAvatar()).placeholder(R.drawable.bg_login)
                        .into(postBinding.avatar);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void playVideo(Uri videoUrl) {

    }
}