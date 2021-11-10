package com.example.connectfarmapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ItemArticleBinding;
import com.example.connectfarmapplication.models.Article;
import com.example.connectfarmapplication.models.Comment;
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.models.UploadResponse;
import com.example.connectfarmapplication.models.UserInfo;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.ui.PersonalPageActivity;
import com.example.connectfarmapplication.utils.Utils;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Article> articles;
    private ItemArticleBinding articleBinding;
    private DatabaseReference databaseReference;
    private SimpleExoPlayer player;
    private CommentAdapter commentAdapter;
    private boolean click = false;
    private String token;

    public ArticlesAdapter(Context context, ArrayList<Article> list, String token ) {
        this.context = context;
        this.articles = list;
        this.token = token;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        articleBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_article, parent, false);
        return new MyViewHolder(articleBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.articleBinding.setArticle(articles.get(position));

        if (articles.get(position).getImages() != null) {
            loadImages(position, holder);
            holder.articleBinding.rcvListImage.setVisibility(View.VISIBLE);
            holder.articleBinding.videoLayout.setVisibility(View.GONE);
        }
        if (articles.get(position).getVideo() != null) {
            loadVideo(position, holder);
            holder.articleBinding.rcvListImage.setVisibility(View.GONE);
            holder.articleBinding.videoLayout.setVisibility(View.VISIBLE);
        }
        getListComment(articles.get(position).getId(), holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if( player != null) {
            player.pause();
        }
    }

    private void loadVideo(int position, MyViewHolder holder) {
        player = new SimpleExoPlayer.Builder(context).build();
        holder.articleBinding.videoPlayer.setPlayer(player);
        // Build the media item.
        String path = APIUtils.PATH + articles.get(position).getVideo();
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(path));
        player.setMediaItem(mediaItem);
        player.prepare();
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

            articleBinding.btnPlay.setOnClickListener(v -> {
                player.play();
                articleBinding.btnPlay.setVisibility(View.GONE);
            });

            articleBinding.comment.setOnClickListener(v -> {
                click = !click;
                if (articleBinding.showComment.isShown()) {
                    articleBinding.showComment.setVisibility(View.GONE);
                } else {
                    articleBinding.showComment.setVisibility(View.VISIBLE);
                    articleBinding.edtComment.requestFocus();

                }
            });

            articleBinding.imvComment.setOnClickListener(v -> {
                String content = articleBinding.edtComment.getText().toString();

                if ((!content.equals(""))) {
                    databaseReference = FirebaseDatabase.getInstance().getReference("Comments");
                    Comment comment = new Comment();
                    comment.setContent(content);
                    comment.setUser_id(Utils.getToken((Activity) context));

                    databaseReference.child(articles.get(getAdapterPosition()).getId().toString()).push().setValue(comment);
                    articleBinding.edtComment.setText("");
                }
            });

            articleBinding.share.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Chia sẻ")
                        .setMessage("Bạn muốn chia sẻ bài đăng này?")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Hủy", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            });

            articleBinding.personalPage.setOnClickListener(v->{
                Intent intent = new Intent(context, PersonalPageActivity.class);
                intent.putExtra("token", articles.get(getAbsoluteAdapterPosition()).getAccess_token());
                context.startActivity(intent);
            });
        }
    }

    private void loadImages(int position, MyViewHolder holder){
        String images =  articles.get(position).getImages();
        databaseReference = FirebaseDatabase.getInstance().getReference("News").child(images);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Image> listImage = new ArrayList<>();
                for (DataSnapshot s : snapshot.child("list_image").getChildren()) {
                    listImage.add(new Image(s.getValue(String.class)));
                }
                ImageAdapter adapter = new ImageAdapter(context, listImage);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                holder.articleBinding.rcvListImage.setLayoutManager(linearLayoutManager);
                holder.articleBinding.rcvListImage.setAdapter(adapter);
                holder.articleBinding.rcvListImage.setVisibility(View.VISIBLE);
                holder.articleBinding.videoLayout.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getListComment(int idNew, ArticlesAdapter.MyViewHolder holder) {
        ArrayList<Comment> comments = new ArrayList<>();
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(idNew + "");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    comments.clear();
                    for (DataSnapshot s : snapshot.getChildren()
                    ) {
                        Comment comment = s.getValue(Comment.class);
                        comments.add(comment);
                    }
                    commentAdapter = new CommentAdapter(context, comments);
                    commentAdapter.notifyDataSetChanged();
                    holder.articleBinding.rcrComment.setAdapter(commentAdapter);
                    holder.articleBinding.rcrComment.setLayoutManager(new LinearLayoutManager(context));
                    holder.articleBinding.rcrComment.setHasFixedSize(true);
                    holder.articleBinding.rcrComment.setNestedScrollingEnabled(false);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.e("TAG", "onCancelled: " + error.getMessage());
                }
            });
        } catch (Exception e) {
        }
    }

}
