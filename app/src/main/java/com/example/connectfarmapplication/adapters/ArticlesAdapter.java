package com.example.connectfarmapplication.adapters;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ItemArticleBinding;
import com.example.connectfarmapplication.databinding.ItemNewBinding;
import com.example.connectfarmapplication.models.Article;
import com.example.connectfarmapplication.models.Comment;
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.models.New;
import com.example.connectfarmapplication.models.User;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.utils.Utils;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<Article> articles;
    private ItemArticleBinding articleBinding;
    private DatabaseReference databaseReference;
    private SimpleExoPlayer player;

    public ArticlesAdapter(Context context, ArrayList<Article> list) {
        this.context = context;
        this.articles = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        articleBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_article, parent, false);
        return new MyViewHolder(articleBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String token = "8u0JceVLiCQfW6rpd6rJwvBs5RH2";
        getUser(token, holder);
        holder.articleBinding.setArticle(articles.get(position));

        if (articles.get(position).getImages() != null) {
            loadImages(position, holder);
            holder.articleBinding.rcvListImage.setVisibility(View.VISIBLE);
            holder.articleBinding.videoLayout.setVisibility(View.GONE);
        }
        if (articles.get(position).getVideo() != null){
            loadVideo(position, holder);
            holder.articleBinding.rcvListImage.setVisibility(View.GONE);
            holder.articleBinding.videoLayout.setVisibility(View.VISIBLE);
        }

    }

    private void loadVideo(int position, MyViewHolder holder) {
        player = new SimpleExoPlayer.Builder(context).build();
        holder.articleBinding.videoPlayer.setPlayer(player);
        // Build the media item.
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"));
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();
        // Start the playback.
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ItemArticleBinding articleBinding;
        public MyViewHolder(@NonNull ItemArticleBinding articleBinding) {
            super(articleBinding.getRoot());
            this.articleBinding = articleBinding;
        }
    }
    private void getUser(String token, MyViewHolder holder) {
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(token);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    holder.articleBinding.setUser(user);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
        }
    }

    private void loadImages(int position, MyViewHolder holder){
        ArrayList<Image> listImage = new ArrayList<>();
        ArrayList<String> images = (ArrayList<String>) articles.get(position).getImages();
        for (String image:images) {
            listImage.add(new Image(image));
        }
        Log.e(TAG, "loadImages: " + images.size() );
        ImageAdapter adapter = new ImageAdapter(context, listImage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.articleBinding.rcvListImage.setLayoutManager(linearLayoutManager);
        holder.articleBinding.rcvListImage.setAdapter(adapter);
        holder.articleBinding.videoLayout.setVisibility(View.GONE);
        holder.articleBinding.rcvListImage.setVisibility(View.VISIBLE);
    }
}
