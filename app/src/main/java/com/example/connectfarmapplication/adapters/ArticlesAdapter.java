package com.example.connectfarmapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Article> articles;
//    private ItemArticleBinding articleBinding;
    private DatabaseReference databaseReference;
    SimpleExoPlayer player;
    private CommentAdapter commentAdapter;
    private boolean click = false;
    private String token;
    private String TAG = "ArticlesAdapter";
    protected DataClient client = APIUtils.getDataClient();
    private int page;
    private final int PERSONAL_PAGE = 1;
    private final int ARTICLE_PAGE = 0;

    public ArticlesAdapter(Context context, ArrayList<Article> list, String token, int page) {
        this.context = context;
        this.articles = list;
        this.token = token;
        this.page = page;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArticleBinding articleBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_article, parent, false);
        return new MyViewHolder(articleBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.articleBinding.setArticle(articles.get(position));

        if (articles.get(position).isLiked()) {
            holder.articleBinding.like.setTextColor(Color.BLUE);
            Drawable img = context.getResources().getDrawable(R.drawable.heartdo);
            holder.articleBinding.icHeart.setImageDrawable(img);
        }

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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadVideo(int position, MyViewHolder holder) {
        holder.articleBinding.videoPlayer.getHorizontalScrollbarThumbDrawable();
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
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
                articleBinding.thumbnail.setVisibility(View.GONE);
                player = new SimpleExoPlayer.Builder(context).build();
                articleBinding.videoPlayer.setPlayer(player);
                String path = APIUtils.PATH + articles.get(getAbsoluteAdapterPosition()).getVideo();
                Log.e(TAG, "loadVideo: " + path );
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(path));
                player.setMediaItem(mediaItem);
                player.prepare();
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
                        .setTitle("Chia s???")
                        .setMessage("B???n mu???n chia s??? b??i ????ng n??y?")
                        .setPositiveButton("?????ng ??", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                client.shareArticle(token, articles.get(getAbsoluteAdapterPosition()).getId()).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.e(TAG, "onResponse: " + response.body() );
                                        Toast.makeText(context, "Chia s??? th??nh c??ng", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context, "Chia s??? th???t b???i", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onFailure: " + t.getMessage() );
                                    }
                                });
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("H???y", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            });

            articleBinding.personalPage.setOnClickListener(v->{
                Intent intent = new Intent(context, PersonalPageActivity.class);
                intent.putExtra("token", articles.get(getAbsoluteAdapterPosition()).getAccess_token());
                context.startActivity(intent);
            });

            articleBinding.icHeart.setOnClickListener(v -> {
                int like = Integer.parseInt(articleBinding.like.getText().toString());

                if (articles.get(getAbsoluteAdapterPosition()).isLiked()) {
                    articles.get(getAbsoluteAdapterPosition()).setLiked(false);
                    Drawable black = context.getResources().getDrawable(R.drawable.heartden);
                    articleBinding.icHeart.setImageDrawable(black);
                    articleBinding.like.setTextColor(Color.BLACK);
                    articleBinding.like.setText(String.valueOf(--like));
                } else {
                    articles.get(getAbsoluteAdapterPosition()).setLiked(true);
                    Drawable red = context.getResources().getDrawable(R.drawable.heartdo);
                    articleBinding.icHeart.setImageDrawable(red);
                    articleBinding.like.setTextColor(Color.BLUE);
                    articleBinding.like.setText(String.valueOf(++like));
                }

                client.likeArticle(Utils.getToken((Activity) context), articles.get(getAbsoluteAdapterPosition()).getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });


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
