package com.example.connectfarmapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    ActivityPostBinding postBinding;
    private NewsAdapter adapter;
    private ArrayList<New> listNews;
    private String token;
    DatabaseReference reference;
    ValueEventListener seenListener ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postBinding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        token = Utils.getToken(this);

        // seenMessage(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (!token.equals("1")) {
            getUser(token);
            SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
            String date = sf.format(Calendar.getInstance().getTime());

            listNews = getListNew();
        }
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
            }

            @Override
            public void onCancelled(DatabaseError error) {

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
}