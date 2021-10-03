package com.example.connectfarmapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.connectfarmapplication.MainActivity;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ActivityInformationUserBinding;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationUserActivity extends BaseActivity {
    private ActivityInformationUserBinding informationUserBinding;
    private Intent intent;
    private HashMap<String, String> userInfor;
    private final String TAG = "Information";
    private DataClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        informationUserBinding = DataBindingUtil.setContentView(InformationUserActivity.this, R.layout.activity_information_user);

        informationUserBinding.layout.setOnClickListener(v -> {
            hideKeyboard(informationUserBinding.layout);
        });

        intent = getIntent();
        String token = intent.getStringExtra("token");
     //   String token = "8u0JceVLiCQfW6rpd6rJwvBs5RH2";

        informationUserBinding.btNext.setOnClickListener(v->{
            if (validationInputData() && token != null) {
                client = APIUtils.getDataClient();
                userInfor = new HashMap<>();
                userInfor.put("name", informationUserBinding.etFullName.getText().toString().trim());
                userInfor.put("job", informationUserBinding.etJob.getText().toString().trim());
                userInfor.put("address", informationUserBinding.etAddress.getText().toString().trim());
                userInfor.put("year_of_birth", informationUserBinding.etYearBorn.getText().toString().trim());
                userInfor.put("access_token", token);
                client.createUser(userInfor).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            startActivity(new Intent(InformationUserActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "fails: fails" + t.getMessage() );
                    }
                });
            }
        });



//        informationUserBinding.btNext.setOnClickListener(v->{
//            if(validationInputData() && token != null){
//                try {
//                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(token);
//                    userInfor = getData();
//                    reference.setValue(userInfor).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            startActivity(new Intent(InformationUserActivity.this, MainActivity.class)
//                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                        }
//                    });
//                }catch (Exception e){
//                    Toast.makeText(InformationUserActivity.this, "Đã có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    public boolean validationInputData() {
        if (informationUserBinding.etFullName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Trường này không được bỏ trống", Toast.LENGTH_SHORT).show();
            informationUserBinding.etFullName.requestFocus();
            return false;
        }else if (informationUserBinding.etJob.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Trường này không được bỏ trống", Toast.LENGTH_SHORT).show();
            informationUserBinding.etJob.requestFocus();
            return false;
        } else if (informationUserBinding.etAddress.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Trường này không được bỏ trống", Toast.LENGTH_SHORT).show();
            informationUserBinding.etAddress.requestFocus();
            return false;
        }
        return true;
    }

}