package com.example.connectfarmapplication.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.AgriculturalResponse;
import com.example.connectfarmapplication.adapters.PriceAdapter;
import com.example.connectfarmapplication.databinding.ActivityPriceListBinding;
import com.example.connectfarmapplication.models.DateAndProvinceResponse;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceListActivity extends BaseActivity {
    private ActivityPriceListBinding binding;
    private ArrayAdapter<String> arrayAdapter;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "PriceListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(PriceListActivity.this, R.layout.activity_price_list);
        setUp();
        getDatesAndProvinces();
        binding.back.setOnClickListener(v -> {
            finish();
        });
        binding.search.setOnClickListener(v -> {
            String date = binding.date.getSelectedItem().toString();
            String kind = getKind();

            DataClient client = APIUtils.getDataClient();
            client.getPriceAgricultural(date, kind).enqueue(new Callback<ArrayList<AgriculturalResponse>>() {
                @Override
                public void onResponse(Call<ArrayList<AgriculturalResponse>> call, Response<ArrayList<AgriculturalResponse>> response) {
                    ArrayList<AgriculturalResponse> list = new ArrayList<>();
                    if (response.isSuccessful()) {
                        list = response.body();
                    }
                    PriceAdapter adapter = new PriceAdapter(list, PriceListActivity.this);
                    LinearLayoutManager manager = new LinearLayoutManager(PriceListActivity.this);
                    binding.listPrice.setAdapter(adapter);
                    binding.listPrice.setLayoutManager(manager);
                }

                @Override
                public void onFailure(Call<ArrayList<AgriculturalResponse>> call, Throwable t) {

                }
            });
            // getResult(date, kind, "An Giang");
        });

        binding.showFilter.setOnClickListener(v->{
            binding.lnFilter.setVisibility(binding.lnFilter.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
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

    private void getDatesAndProvinces() {
        DataClient client = APIUtils.getDataClient();
        client.getDatesAndProvinces().enqueue(new Callback<DateAndProvinceResponse>() {
            @Override
            public void onResponse(Call<DateAndProvinceResponse> call, Response<DateAndProvinceResponse> response) {
                DateAndProvinceResponse data = new DateAndProvinceResponse();
                if (response.isSuccessful()) {
                    data = response.body();
                    Log.e("TAG", "onResponse: " + data.toString());
                }
                arrayAdapter = new ArrayAdapter(PriceListActivity.this, R.layout.spinner_text, data.getDates());
                binding.date.setAdapter(arrayAdapter);
                arrayAdapter = new ArrayAdapter(PriceListActivity.this, R.layout.spinner_text, data.getProvinces());
                binding.province.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<DateAndProvinceResponse> call, Throwable t) {
                Log.e(TAG, "Get Date and Province: onFailure: " + t.getMessage());
            }
        });
    }

    private void setUp() {

        DataClient client = APIUtils.getDataClient();
        client.getPriceAgricultural( "", "").enqueue(new Callback<ArrayList<AgriculturalResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<AgriculturalResponse>> call, Response<ArrayList<AgriculturalResponse>> response) {
                ArrayList<AgriculturalResponse> list = new ArrayList<>();
                if (response.isSuccessful()) {
                    list = response.body();
                }
                PriceAdapter adapter = new PriceAdapter(list, PriceListActivity.this);
                LinearLayoutManager manager = new LinearLayoutManager(PriceListActivity.this);
                binding.listPrice.setAdapter(adapter);
                binding.listPrice.setLayoutManager(manager);
            }

            @Override
            public void onFailure(Call<ArrayList<AgriculturalResponse>> call, Throwable t) {

            }
        });
    }
}