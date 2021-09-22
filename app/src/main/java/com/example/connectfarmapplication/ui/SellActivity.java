package com.example.connectfarmapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.SellAdapter;
import com.example.connectfarmapplication.databinding.ActivitySellBinding;
import com.example.connectfarmapplication.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SellActivity extends AppCompatActivity {
    ActivitySellBinding binding;
    ArrayList<Product> products;
    SellAdapter adapter ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SellActivity.this, R.layout.activity_sell);
        getProducts();

        binding.showFilter.setOnClickListener(v->{
            if (binding.lnFilter.getVisibility() == View.VISIBLE)
                binding.lnFilter.setVisibility(View.GONE);
            else binding.lnFilter.setVisibility(View.VISIBLE);
        });


    }

    private void getProducts() {
        ArrayList<Product> products = new ArrayList<>();
        db.collection("Products").get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        products.add(document.toObject(Product.class));
                    }
                    adapter = new SellAdapter(products, SellActivity.this);
                    LinearLayoutManager manager = new LinearLayoutManager(this);
                    binding.list.setLayoutManager(manager);
                    binding.list.setAdapter(adapter);
                })
        .addOnFailureListener(e -> Log.e("TAG", "onFailure: " + e.getMessage() ));


    }
}