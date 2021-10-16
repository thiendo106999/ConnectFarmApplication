package com.example.connectfarmapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectfarmapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.MyViewHolder> {

    private ArrayList<AgriculturalResponse> listPrice;

    private Context context;

    public PriceAdapter( ArrayList<AgriculturalResponse> listPrice, Context context) {
        this.context = context;
        this.listPrice = listPrice;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_price, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvPrice.setText(listPrice.get(position).getPrice());
        holder.tvName.setText(listPrice.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return listPrice == null ? 0 : listPrice.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvPrice = itemView.findViewById(R.id.price);
        }
    }
}
