package com.example.connectfarmapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ItemImageBinding;
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.ui.FullscreenActivity;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Image> listImage;
    //ItemImageBinding mBinding;
    String TAG = "ImageAdapter";

    private static ImageAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(ImageAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public ImageAdapter(Context context, ArrayList<Image> listImage) {
        this.context = context;
        this.listImage = listImage;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemImageBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_image, parent, false);
        return new MyViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageAdapter.MyViewHolder holder, int position) {
        String path = listImage.get(position).getImage();
        Log.e(TAG, "onBindViewHolder: " + context.toString() );
        if (context.toString().contains("Articles")) {
            holder.imageBinding.progress.setVisibility(View.VISIBLE);
        }
        Glide.with(context)
                .load(path)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.imageBinding.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.imageBinding.progress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageBinding.image);
    }

    @Override
    public int getItemCount() {
        return listImage == null ? 0 : listImage.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding imageBinding;

        public MyViewHolder(ItemImageBinding mBinding) {
            super(mBinding.getRoot());
            imageBinding = mBinding;
            mBinding.image.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullscreenActivity.class);
                Gson gson = new Gson();
                String temp = gson.toJson(listImage);
                intent.putExtra("list_image", temp);
                intent.putExtra("position", getAbsoluteAdapterPosition());
                context.startActivity(intent);
            });
        }
    }


}