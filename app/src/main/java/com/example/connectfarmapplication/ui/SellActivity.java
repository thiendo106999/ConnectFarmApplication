package com.example.connectfarmapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.SellAdapter;
import com.example.connectfarmapplication.databinding.ActivitySellBinding;
import com.example.connectfarmapplication.models.DateAndProvinceResponse;
import com.example.connectfarmapplication.models.ProductResponse;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellActivity extends AppCompatActivity {
    ActivitySellBinding binding;
    private final String TAG = "SellActivity";
    private ArrayAdapter arrayAdapter;
    private SellAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SellActivity.this, R.layout.activity_sell);
        binding.progress.setVisibility(View.VISIBLE);
        getProducts(null, null, null);
        setupSpinner();
        binding.showFilter.setOnClickListener(v->{
            if (binding.lnFilter.getVisibility() == View.VISIBLE)
                binding.lnFilter.setVisibility(View.GONE);
            else binding.lnFilter.setVisibility(View.VISIBLE);
        });

        binding.search.setOnClickListener(v -> {
            String date = binding.date.getSelectedItem() == null ? null : binding.date.getSelectedItem().toString();
            String province = binding.province.getSelectedItem() == null ? null : binding.province.getSelectedItem().toString();
            String kind = getKind();
            getProducts(date, kind, province);
        });

        binding.back.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupSpinner() {
        DataClient client = APIUtils.getDataClient();
        client.setupSpinnerSell().enqueue(new Callback<DateAndProvinceResponse>() {
            @Override
            public void onResponse(Call<DateAndProvinceResponse> call, Response<DateAndProvinceResponse> response) {
                DateAndProvinceResponse data;
                if (response.isSuccessful()) {
                    data = response.body();
                    arrayAdapter = new ArrayAdapter(SellActivity.this, R.layout.spinner_text, data.getDates());
                    binding.date.setAdapter(arrayAdapter);
                    arrayAdapter = new ArrayAdapter(SellActivity.this, R.layout.spinner_text, data.getProvinces());
                    binding.province.setAdapter(arrayAdapter);
                    binding.progress.setVisibility(View.VISIBLE);
                } else {
                    Log.e(TAG, "Get Date and Province: onResponse is fails: " + response.body() );
                    binding.progress.setVisibility(View.VISIBLE);
                    Toast.makeText(SellActivity.this, "Lỗi không thể kết nối server", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<DateAndProvinceResponse> call, Throwable t) {
                binding.progress.setVisibility(View.VISIBLE);
                Log.e(TAG, "Get Date and Province: onFailure: " + t.getMessage());
            }
        });
    }

    private String getKind() {
        String result = "";
        if (binding.rice.isChecked())
            result = "luagao";
        else if (binding.vegetable.isChecked())
            result = "hoamau";
        else if (binding.fruit.isChecked())
            result = "traicay";
        return result;
    }
    private void getProducts(String date, String kind, String province) {
        DataClient client = APIUtils.getDataClient();
        client.getProducts(date, kind, province).enqueue(new Callback<ArrayList<ProductResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ProductResponse>> call, Response<ArrayList<ProductResponse>> response) {
                ArrayList<ProductResponse> products = new ArrayList<>();
                if (response.isSuccessful()) {
                    products = response.body();
                    adapter = new SellAdapter(products, SellActivity.this);
                    LinearLayoutManager manager = new LinearLayoutManager(SellActivity.this);
                    binding.list.setLayoutManager(manager);
                    binding.list.setAdapter(adapter);
                    binding.progress.setVisibility(View.GONE);
                } else {
                    Log.e(TAG, "onResponse: " + response.body());
                    binding.progress.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ProductResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                binding.progress.setVisibility(View.GONE);
            }
        });
    }
}