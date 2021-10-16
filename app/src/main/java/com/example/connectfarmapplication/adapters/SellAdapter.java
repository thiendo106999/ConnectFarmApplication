package com.example.connectfarmapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ItemSellBinding;
import com.example.connectfarmapplication.models.Product;
import com.example.connectfarmapplication.models.ProductResponse;

import java.util.ArrayList;

public class SellAdapter extends RecyclerView.Adapter<SellAdapter.MyViewHolder>{

    private ArrayList<ProductResponse> products;
    private Context context;

    public SellAdapter(ArrayList<ProductResponse> products, Context context){
        this.context = context;
        this.products = products;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSellBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_sell, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setProduct(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemSellBinding binding;
        public MyViewHolder(@NonNull ItemSellBinding item) {
            super(item.getRoot());
            this.binding = item;

        }
    }
}
