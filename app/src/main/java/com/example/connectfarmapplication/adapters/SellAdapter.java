package com.example.connectfarmapplication.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ItemSellBinding;
import com.example.connectfarmapplication.models.Product;
import com.example.connectfarmapplication.models.ProductResponse;
import com.example.connectfarmapplication.models.UploadResponse;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellAdapter extends RecyclerView.Adapter<SellAdapter.MyViewHolder>{

    private ArrayList<ProductResponse> products;
    private Context context;
    private final static String TAG = "SellAdapter";

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
        if (products.get(position).getStatus().equals("registered")) {
            holder.binding.approve.setVisibility(View.VISIBLE);
        }
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

            if (context.toString().contains("MyStorageActivity")) {
                this.binding.delete.setVisibility(View.VISIBLE);

                this.binding.delete.setOnClickListener(v->{
                    new AlertDialog.Builder(context)
                            .setTitle("Xóa sản phẩm")
                            .setMessage("Bạn muốn xóa sản phẩm này?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DataClient client = APIUtils.getDataClient();
                                    client.deleteBook(products.get(getPosition()).getId()).enqueue(new Callback<UploadResponse>() {
                                        @Override
                                        public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                                            if (!response.isSuccessful()) {
                                                Log.e(TAG, "onResponse: " + response.body());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UploadResponse> call, Throwable t) {
                                            Log.e(TAG, "onFailure: " + t.getMessage());
                                        }
                                    });
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton("Hủy", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                });
            }
        }
    }
}
