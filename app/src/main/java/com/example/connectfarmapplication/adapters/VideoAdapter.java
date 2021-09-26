package com.example.connectfarmapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.models.Video;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.viewHolder> {

    Context context;
    ArrayList<Video> videoArrayList;
    private static UploadAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(UploadAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    public VideoAdapter (Context context, ArrayList<Video > videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @Override
    public VideoAdapter .viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoAdapter .viewHolder holder, final int i) {
        holder.title.setText(videoArrayList.get(i).getVideoTitle());
        holder.duration.setText(videoArrayList.get(i).getVideoDuration());
        Glide.with(context)
                .load(videoArrayList.get(i).getVideoUri())
                .placeholder(context.getDrawable(R.drawable.image))
                .into(holder.ivVideo);
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, duration;
        ImageView ivVideo;
        public viewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_video_title);
            duration = (TextView) itemView.findViewById(R.id.tv_duration);
            ivVideo = itemView.findViewById(R.id.iv_video_thumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoArrayList.get(getAdapterPosition()).setChoose(true);
                }
            });
        }

    }
}