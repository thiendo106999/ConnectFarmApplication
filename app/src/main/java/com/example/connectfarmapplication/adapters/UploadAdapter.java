package com.example.connectfarmapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.utils.Utils;

import java.util.ArrayList;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Image> listImage;
    private int key;

    private static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public UploadAdapter(Context context, ArrayList<Image> listImage, int key) {
        this.context = context;
        this.listImage = listImage;
        this.key = key;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public UploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_upload, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadAdapter.ViewHolder holder, int position) {

        Glide.with(context)
                .load(listImage.get(position).getImage())
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return listImage == null ? 0 : listImage.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, chosen;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageItem2);
            cardView = itemView.findViewById(R.id.card);
            chosen = itemView.findViewById(R.id.chosen);
            if (key == 1) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onItemClick(itemView, getLayoutPosition());
                        } else {
                            boolean isCheck = listImage.get(getLayoutPosition()).isChosen();

                            if (isCheck) {
                                listImage.get(getLayoutPosition()).setChosen(!isCheck);
                                cardView.getLayoutParams().width = (int) Utils.pxFromDp(context, 100);
                                cardView.getLayoutParams().height = (int) Utils.pxFromDp(context, 100);
                                chosen.setVisibility(View.GONE);
                            } else {
                                listImage.get(getLayoutPosition()).setChosen(!isCheck);
                                cardView.getLayoutParams().width = (int) Utils.pxFromDp(context, 80);
                                cardView.getLayoutParams().height = (int) Utils.pxFromDp(context, 80);
                                chosen.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            } else {
                cardView.getLayoutParams().width = (int) Utils.pxFromDp(context, 140);
                cardView.getLayoutParams().height = (int) Utils.pxFromDp(context, 140);
                image.setScaleType(ImageView.ScaleType.CENTER);
            }
        }
    }


}
