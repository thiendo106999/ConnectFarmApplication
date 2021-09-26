package com.example.connectfarmapplication.adapters;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ItemNewBinding;
import com.example.connectfarmapplication.models.Comment;
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.models.New;
import com.example.connectfarmapplication.models.User;
import com.example.connectfarmapplication.ui.PostTweetActivity;
import com.example.connectfarmapplication.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<New> list;
    MediaPlayer mediaPlayer;
    SurfaceHolder surfaceHolder;
    ItemNewBinding mBinding;
    ImageAdapter adapter;
    CommentAdapter commentAdapter;
    DatabaseReference databaseReference;
    public static int FRAGMENT_LOVE = 1;
    public static int FRAGMENT_NEW = 0;
    int type;
    int click = 0;
    private Handler handlerSeekBarDuration;
    private Runnable runnable;
    private static final int SEEK_TIME = 5000;


    public NewsAdapter(Context context, ArrayList<New> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_new, parent, false);

        return new MyViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.mBinding.setNews(list.get(position));

      //  getListImages(position, holder);
        loadVideo(position, holder);
        String token = list.get(position).getUser_id();
        getUser(token, holder);
        getListComment(list.get(position).getId(), holder);

    }
    private void loadVideo(int position, MyViewHolder holder) {
        databaseReference = FirebaseDatabase.getInstance().getReference("News").child(list.get(position).getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("video")) {
                   for (DataSnapshot s : snapshot.child("video").getChildren()) {
                       String path = s.getValue(String.class);
                       holder.mBinding.frameLayout.setVisibility(View.VISIBLE);
                       holder.mBinding.rcvListImage.setVisibility(View.GONE);
                       surfaceHolder = holder.mBinding.surfaceview.getHolder();
                       playVideo(Uri.parse(path), holder);
                   }
                } else {
                    ArrayList<Image> listImage = new ArrayList<>();
                    for (DataSnapshot s : snapshot.child("list_image").getChildren()) {
                        listImage.add(new Image(s.getValue(String.class)));
                    }
                    adapter = new ImageAdapter(context, listImage);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                    holder.mBinding.rcvListImage.setLayoutManager(linearLayoutManager);
                    holder.mBinding.rcvListImage.setAdapter(adapter);
                    holder.mBinding.frameLayout.setVisibility(View.GONE);
                    holder.mBinding.rcvListImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser(String token, MyViewHolder holder) {

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(token);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    holder.mBinding.setUser(user);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
        }
    }

    private void getListComment(String idNew, MyViewHolder holder) {
        ArrayList<Comment> comments = new ArrayList<>();

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(idNew);

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
                    holder.mBinding.rcrComment.setAdapter(commentAdapter);
                    holder.mBinding.rcrComment.setLayoutManager(new LinearLayoutManager(context));
                    holder.mBinding.rcrComment.setHasFixedSize(true);
                    holder.mBinding.rcrComment.setNestedScrollingEnabled(false);

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.e("TAG", "onCancelled: " + error.getMessage());
                }
            });
        } catch (Exception e) {
        }


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemNewBinding mBinding;
        RecyclerView recyclerView, rc;
      //  SurfaceView surfaceView;
        VideoView videoView;

        public MyViewHolder(@NonNull ItemNewBinding itemView) {
            super(itemView.getRoot());

            this.mBinding = itemView;
            recyclerView = itemView.rcvListImage;
            rc = itemView.rcrComment;

        //    surfaceView = itemView.surfaceview;

            if (type == FRAGMENT_NEW) {
                itemView.heart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click = click == 0 ? 1 : 0;
                        if (click == 1) {
                            Glide.with(context)
                                    .load(R.drawable.heartdo)
                                    .into(mBinding.heart);
                            addToFavorite();
                        } else {
                            Glide.with(context)
                                    .load(R.drawable.heartden)
                                    .into(mBinding.heart);
                        }
                    }

                    private void addToFavorite() {
                        String token = Utils.getToken((Activity) context);
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(token);

                        databaseReference.child("favorite_posts").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                boolean temp = true;
                                if (snapshot.exists()) {
                                    for (DataSnapshot s : snapshot.getChildren()) {
                                        if (getAdapterPosition() != -1) {
                                            if (s.getValue(String.class).equals(list.get(getAdapterPosition()).getId())) {
                                                temp = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (temp) {
                                    databaseReference.child("favorite_posts").push().setValue(list.get(getAdapterPosition()).getId());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
            if (type == FRAGMENT_LOVE) {
                Glide.with(context)
                        .load(R.drawable.heartdo)
                        .into(mBinding.heart);
            }
            itemView.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click = click == 0 ? 1 : 0;
                    if (itemView.showComment.isShown()) {
                        itemView.showComment.setVisibility(View.GONE);
                    } else {
                        itemView.showComment.setVisibility(View.VISIBLE);
                        itemView.edtComment.requestFocus();

                    }
                    // getListComment(list.get(getAdapterPosition()).getId());
                }
            });

            itemView.imvComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = itemView.edtComment.getText().toString();

                    if ((!content.equals(""))) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("Comments");
                        Comment comment = new Comment();
                        comment.setContent(content);
                        comment.setUser_id(Utils.getToken((Activity) context));

                        databaseReference.child(list.get(getLayoutPosition()).getId()).push().setValue(comment);
                        itemView.edtComment.setText("");
                    }
                }
            });

         //   setupEvent(this);

        }
    }
    private void playVideo(Uri uri, MyViewHolder viewHolder) {


        viewHolder.mBinding.surfaceview.setVideoURI(uri);

        viewHolder.mBinding.surfaceview.setMediaController(new MediaController(context));
        viewHolder.mBinding.surfaceview.setZOrderOnTop(true);

        viewHolder.mBinding.surfaceview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                viewHolder.mBinding.surfaceview.setZOrderMediaOverlay(true);
                viewHolder.mBinding.surfaceview.requestFocus();
                viewHolder.mBinding.surfaceview.start();
            }
        });
     //   video.setVideoURI(Uri.parse(pathForTheFile));
       // viewHolder.mBinding.surfaceview.start();
    //    viewHolder.mBinding.surfaceview.setBackgroundColor(Color.TRANSPARENT);
    }

//    private void playVideo(Uri uri, MyViewHolder viewHolder) {
//        Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            if (mediaPlayer == null) {
//                mediaPlayer = new MediaPlayer();
//            }
//            try {
//                mediaPlayer.setDataSource(context, uri);
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            surfaceHolder = viewHolder.mBinding.surfaceview.getHolder();
//            surfaceHolder.setFixedSize(176, 144);
//            mediaPlayer.setDisplay( surfaceHolder);
//            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//                @Override
//                public void surfaceCreated(@NonNull SurfaceHolder holder) {
//
//                }
//
//                @Override
//                public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
//
//                }
//
//                @Override
//                public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//
//                }
//            });
//
//
//          // mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//
//
//            mediaPlayer.setOnPreparedListener(mp -> {
//                mp.start();
//                int duration = mp.getDuration();
//                viewHolder.mBinding.sbDuration.setMax(duration);
//
//                viewHolder.mBinding.videoControl.setVisibility(View.GONE);
//                viewHolder.mBinding.sbDuration.setMax(duration);
//                viewHolder.mBinding.tvDuration.setText(convertMillisecondToTime(duration));
//
//                handlerSeekBarDuration = new Handler();
//                if(mp.isPlaying()){
//                    ((Activity)context).runOnUiThread(runnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            try{
//                                int mCurrentPosition = mp.getCurrentPosition();
//                                if (mCurrentPosition > 0) {
//                                    viewHolder.mBinding.pbLoadingVideo.setVisibility(View.GONE);
//                                }
//                                viewHolder.mBinding.sbDuration.setProgress(mCurrentPosition);
//                                viewHolder.mBinding.tvCurrentTime.setText(convertMillisecondToTime(mCurrentPosition));
//                                handlerSeekBarDuration.postDelayed(this, 1000);
//                            }catch (IllegalStateException e){
//                                Log.e("TAG", "run: "+e.getMessage() );
//                            }
//                        }
//                    });
//                }
//
//            });
//            try {
//                mediaPlayer.prepareAsync();
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//
//        }, 100);
//    }
    @SuppressLint("DefaultLocale")
    private String convertMillisecondToTime(long duration) {
        long seconds = (duration / 1000) % 60;
        long minutes = ((duration / (1000 * 60)) % 60);

        return String.format("%02d:%02d", minutes, seconds);
    }

//    private void setupEvent(MyViewHolder holder) {
//        holder.mBinding.ivFastRewind.setOnClickListener(v -> seekRewind());
//        holder.mBinding.ivFastForward.setOnClickListener(v -> seekForward());
//        holder.mBinding.ivPlayPause.setOnClickListener(v -> onPlayPauseClick(holder));
//       // holder.mBinding. ivFullScreen.setOnClickListener(v -> onFullScreenClick());
//
//        holder.mBinding.surfaceview.setOnTouchListener((v, event) -> {
//            holder.mBinding.videoControl.setVisibility(View.VISIBLE);
//            new Handler().postDelayed(() -> holder.mBinding.videoControl.setVisibility(View.GONE), 3000);
//            return true;
//        });
//
//        holder.mBinding.sbDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (mediaPlayer != null && fromUser) {
//                    mediaPlayer.seekTo(progress);
//                    seekBar.setProgress(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//    }

    public void seekForward() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition + SEEK_TIME <= mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(currentPosition + SEEK_TIME);
        } else {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }
    private void seekRewind() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition - SEEK_TIME >= 0) {
            mediaPlayer.seekTo(currentPosition - SEEK_TIME);
        } else {
            mediaPlayer.seekTo(0);
        }
    }
    private void onPlayPauseClick(MyViewHolder holder) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            holder.mBinding.ivPlayPause.setImageResource(R.drawable.ic_play_arrow);
        } else {
            mediaPlayer.start();
            holder.mBinding.ivPlayPause.setImageResource(R.drawable.ic_pause);
        }
    }

}
