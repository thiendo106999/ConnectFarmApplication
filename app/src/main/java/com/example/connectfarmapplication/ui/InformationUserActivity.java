package com.example.connectfarmapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.connectfarmapplication.MainActivity;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ActivityInformationUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class InformationUserActivity extends BaseActivity {
    private ActivityInformationUserBinding informationUserBinding;
    private Intent intent;
    private HashMap<String, String> userInfor;
    private final String TAG = "Information";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        informationUserBinding = DataBindingUtil.setContentView( InformationUserActivity.this, R.layout.activity_information_user);

        informationUserBinding.layout.setOnClickListener(v->{
            hideKeyboard(informationUserBinding.layout);
        });

        intent = getIntent();
        String token = intent.getStringExtra("token");
        Log.e(TAG, "onCreate: " + token);
        informationUserBinding.btNext.setOnClickListener(v->{
            if(validationInputData() && token != null){
                try {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(token);
                    userInfor = getData();
                    reference.setValue(userInfor).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(InformationUserActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(InformationUserActivity.this, "Đã có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    private HashMap<String, String> getData() {
        HashMap<String, String> temp = new HashMap<>();
        temp.put("full_name", informationUserBinding.etFullName.getText().toString().trim());
        temp.put("job", informationUserBinding.etJob.getText().toString().trim());
        temp.put("address", informationUserBinding.etAddress.getText().toString().trim());
        temp.put("year_born", informationUserBinding.etYearBorn.getText().toString().trim());
        return temp;
    }
}