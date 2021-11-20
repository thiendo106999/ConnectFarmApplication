package com.example.connectfarmapplication.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ActivityRegisteredProductBinding;
import com.example.connectfarmapplication.models.UploadResponse;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.example.connectfarmapplication.utils.RealPathUtil;
import com.example.connectfarmapplication.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisteredProductActivity extends AppCompatActivity {
    private ActivityRegisteredProductBinding binding;
    private File file;
    private static final String TAG = "RegisteredProduct";
    Uri chosenImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(RegisteredProductActivity.this, R.layout.activity_registered_product);

        binding.back.setOnClickListener(v -> finish());
        binding.image.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });

        binding.btnRegisteredProduct.setOnClickListener(v -> {
            if (checkInfos()) {
                String token = Utils.getToken(RegisteredProductActivity.this);
                RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                RequestBody access_token = RequestBody.create(MediaType.parse("text/plain"), token);
                RequestBody nong_san = RequestBody.create(MediaType.parse("text/plain"), binding.edtNongSan.getText().toString());
                RequestBody phone_number = RequestBody.create(MediaType.parse("text/plain"), binding.edtPhoneNumber.getText().toString());
                RequestBody address = RequestBody.create(MediaType.parse("text/plain"), binding.edtAddress.getText().toString());
                RequestBody date = RequestBody.create(MediaType.parse("text/plain"), binding.edtDate.getText().toString());
                RequestBody kind_of = RequestBody.create(MediaType.parse("text/plain"), getKind());
                RequestBody hexta = RequestBody.create(MediaType.parse("text/plain"), binding.edtHexta.getText().toString());
                String temp = token + System.currentTimeMillis() + file.getName().substring(file.getName().lastIndexOf("."));
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), temp);
                DataClient client = APIUtils.getDataClient();
                client.registeredProduct(fileToUpload, filename, nong_san ,
                        access_token, phone_number, address, date, kind_of, hexta).enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisteredProductActivity.this, "Bạn đã đăng ký thành công. Vui lòng chờ duyệt.", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Log.e(TAG, "onResponse: " + response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });


            }
        });
    }

    public boolean checkInfos() {
        if (file == null) {
            Toast.makeText(RegisteredProductActivity.this, "Vui lòng chọn hình ảnh", Toast.LENGTH_LONG).show();
            return false;
        }
        if (binding.edtNongSan.getText().equals("")) {
            Toast.makeText(RegisteredProductActivity.this, "Vui lòng nhập tên loại nông sản", Toast.LENGTH_LONG).show();
            return false;
        }
        if (binding.edtAddress.getText().equals("")) {
            Toast.makeText(RegisteredProductActivity.this, "Vui lòng nhập địa chỉ", Toast.LENGTH_LONG).show();
            return false;
        }
        if (binding.edtDate.getText().equals("")) {
            Toast.makeText(RegisteredProductActivity.this, "Vui lòng nhập ngày thu hoạch", Toast.LENGTH_LONG).show();
            return false;
        }
        if (binding.edtPhoneNumber.getText().equals("")) {
            Toast.makeText(RegisteredProductActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            chosenImageUri = data.getData();
            Bitmap mBitmap = null;
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), chosenImageUri);
                binding.image.setImageBitmap(mBitmap);
                file = new File(RealPathUtil.getPath(RegisteredProductActivity.this, chosenImageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}