package com.example.connectfarmapplication.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ActivityPriceListBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PriceListActivity extends AppCompatActivity {
    ActivityPriceListBinding binding;
    ArrayAdapter<String> arrayAdapter;
    private final String TAG = "LOG";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(PriceListActivity.this, R.layout.activity_price_list);

        getProvinces();

        getDate();

        binding.search.setOnClickListener(v -> {
            String date = binding.date.getSelectedItem().toString();
            String kind = getKind();
            getResult(date, kind, "An Giang");
        });
    }

    private String getKind(){
        String result = "";
        if(binding.rice.isChecked())
            result = "Lúa Gạo";
        else if(binding.vegetable.isChecked())
            result = "Hoa màu";
        else if(binding.fruit.isChecked())
            result = "Trái Cây";
        return result;
    }
    private void getDate() {
        ArrayList<String> dates = new ArrayList<>();
        db.collection("date").get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        dates.add(document.getString("date"));
                    }
                    arrayAdapter = new ArrayAdapter<String>(PriceListActivity.this, android.R.layout.simple_list_item_1, dates);
                    binding.date.setAdapter(arrayAdapter);
                });

    }


    private ArrayList<String> getProvinces() {
        ArrayList<String> provinces = new ArrayList<>();
        db.collection("provinces").get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        provinces.add(document.getString("name"));
                    }
                    arrayAdapter = new ArrayAdapter<String>(PriceListActivity.this, android.R.layout.simple_list_item_1, provinces);
                    binding.province.setAdapter(arrayAdapter);
                    binding.province.setSelection(0);
                });

        return provinces;
    }

    private void getResult(String date, String kind, String province) {
        ArrayList<String> result = new ArrayList<>();
        db.collection("prices")
                .get()
                .addOnCompleteListener(task -> {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Objects.equals(document.getString("date"), date)
                                && Objects.equals(document.getString("agricultural_id"), kind)) {
                            result.add(document.getString("name") + "    " + document.getString("price"));
                        }
                    }
                    arrayAdapter = new ArrayAdapter<String>(PriceListActivity.this, android.R.layout.simple_list_item_1, result);
                    binding.list.setAdapter(arrayAdapter);

                });
    }

    private void addData() {
        String date = "4-9-2021";
        Map<String, Object> data = new HashMap<>();
        data.put("agricultural_id", "Lúa Gạo");
        data.put("date", date);
        data.put("name", "Lúa OM 9577");
        data.put("price", "6.100 đ/kg");
        data.put("province", "An Giang");
        db.collection("prices").add(data);

        data.clear();
        data.put("agricultural_id", "Lúa Gạo");
        data.put("date", date);
        data.put("name", "Lúa OM 9577");
        data.put("price", "6.100 đ/kg");
        data.put("province", "An Giang");
        db.collection("prices").add(data);


        data.clear();
        data.put("agricultural_id", "Trái Cây");
        data.put("date", date);
        data.put("name", "Giá sầu riêng Ri6");
        data.put("price", "80.000 đ/kg");
        data.put("province", "Miền Tây");
        db.collection("prices").add(data);

        data.clear();
        data.put("agricultural_id", "Trái Cây");
        data.put("date", date);
        data.put("name", "Chôm chôm Thái");
        data.put("price", "40.000 – 45.000 đồng/kg");
        data.put("province", "Miền Tây");
        db.collection("prices").add(data);

        data.clear();
        data.put("agricultural_id", "Trái Cây");
        data.put("date", date);
        data.put("name", "Mít");
        data.put("price", "16000 đồng/kg");
        data.put("province", "Miền Tây");
        db.collection("prices").add(data);

        data.clear();
        data.put("agricultural_id", "Hoa màu");
        data.put("date", date);
        data.put("name", "Cải xanh");
        data.put("price", "16000 đồng/kg");
        data.put("province", "Miền Tây");
        db.collection("prices").add(data);
        data.clear();
        data.put("agricultural_id", "Hoa màu");
        data.put("date", date);
        data.put("name", "Hành lá");
        data.put("price", "50000 đ/kg");
        data.put("province", "Miền Tây");
        db.collection("prices").add(data);
        data.clear();

        data.put("agricultural_id", "Hoa màu");
        data.put("date", date);
        data.put("name", "Rau muống");
        data.put("price", "16000 đồng/kg");
        data.put("province", "Miền Tây");
        db.collection("prices").add(data);
    }
}