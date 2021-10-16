package com.example.connectfarmapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ItemImageBinding;
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.ui.FullscreenActivity;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Image> listImage;
    ItemImageBinding mBinding;

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
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_image, parent, false);
        return new MyViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageAdapter.MyViewHolder holder, int position) {
        String path = "http://192.168.1.7:8000/api/storage/" + listImage.get(position).getImage();
        Glide.with(context)
                .load(Uri.parse(path))
                .into(mBinding.image);
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
            mBinding.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullscreenActivity.class);
                    Gson gson = new Gson();
                    String temp = gson.toJson(listImage);
                    intent.putExtra("list_image", temp);
                    intent.putExtra("position", getAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }
    }


}