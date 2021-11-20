package com.example.connectfarmapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.adapters.ArticlesAdapter;
import com.example.connectfarmapplication.databinding.ActivityPersonalPageBinding;
import com.example.connectfarmapplication.models.PersonalPageResponse;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalPageActivity extends AppCompatActivity {
    private ActivityPersonalPageBinding binding;
    private final String TAG = "PersonalPageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(PersonalPageActivity.this, R.layout.activity_personal_page);

        setUI();
        binding.btnBack.setOnClickListener(v->{
            finish();
        });
    }

    private void setUI() {
        Intent intent = getIntent();
        String token = intent.getStringExtra("token") == null ? "mciYBoSReiTsoTMmZCrAup9U5Ym1" : intent.getStringExtra("token");
        String my_token = Utils.getToken(PersonalPageActivity.this);
        DataClient client = APIUtils.getDataClient();
        client.getPersonalPage(token,  my_token).enqueue(new Callback<PersonalPageResponse>() {
            @Override
            public void onResponse(Call<PersonalPageResponse> call, Response<PersonalPageResponse> response) {
                if (response.isSuccessful()) {
                    PersonalPageResponse data = response.body();
                    Glide.with(PersonalPageActivity.this)
                            .load(Uri.parse(APIUtils.PATH + data.getAvatar()))
                            .placeholder(R.drawable.image)
                            .into(binding.avatar);
                    binding.tvUsername.setText(data.getUser_name());
                    ArticlesAdapter adapter = new ArticlesAdapter(PersonalPageActivity.this, data.getArticles(), token, 1);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonalPageActivity.this);
                    linearLayoutManager.setReverseLayout(true);
                    binding.articles.setLayoutManager(linearLayoutManager);
                    binding.articles.setAdapter(adapter);
                } else {
                    Log.e(TAG, "onResponse: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<PersonalPageResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }
}