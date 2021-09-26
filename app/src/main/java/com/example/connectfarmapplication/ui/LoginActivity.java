package com.example.connectfarmapplication.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.connectfarmapplication.MainActivity;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding loginBinding;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth firebaseAuth;
    private String mVerifyId;
    private final String TAG  = "Login";
    private SharedPreferences sharedPreferences;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("token", token);
            startActivity(intent);
        }
        loginBinding.setObj(LoginActivity.this);

        enabledVerifyForm(false);
        firebaseAuth = FirebaseAuth.getInstance();

        //hide keyboard
        loginBinding.lnParent.setOnClickListener(v -> {
            hideKeyboard(loginBinding.lnParent);
        });

        //send phone number to get opt verify
        loginBinding.btnSent.setOnClickListener(v -> {
            String phoneNumber = loginBinding.edtPhoneNumber.getText().toString().trim();
            disableTouchScreen();
            loginBinding.progress.setVisibility(View.VISIBLE);
            if(phoneNumber.isEmpty() || !validatePhoneNumber(phoneNumber)){
                loginBinding.edtPhoneNumber.setError("Số điện thoại không hợp lệ, vui lòng kiểm tra lại");
                enableTouchScreen();
                loginBinding.progress.setVisibility(View.GONE);
            }else{
                startPhoneNumberVerification();
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "onVerificationFailed: "+ e.getMessage() );
                Toast.makeText(LoginActivity.this, "Số điện thoại của bạn không hợp lệ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(s, forceResendingToken);
                mVerifyId =  s;
                forceResendingToken = token;
                enabledVerifyForm(true);
                enableTouchScreen();
                loginBinding.progress.setVisibility(View.GONE);
            }
        };

        //send opt to login
        loginBinding.btnSentOtp.setOnClickListener(v->{
            disableTouchScreen();
            loginBinding.progress.setVisibility(View.VISIBLE);
            String code = loginBinding.edtOpt.getText().toString().trim();
            if(!code.equals("")){
                verifyPhoneNumberWithCode(mVerifyId, code );
            } else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this, "Vui lòng nhập mã để xác thực", Toast.LENGTH_LONG).show();
                enableTouchScreen();
                loginBinding.progress.setVisibility(View.GONE);
            }
        });

        //resend opt code
        loginBinding.resend.setOnClickListener(v->{
            resendVerificationCode(getPhoneNumberInFormatVietNamese(), forceResendingToken);
        });

        loginBinding.btnBack.setOnClickListener(v -> {
            enabledVerifyForm(false);
            enableTouchScreen();
            loginBinding.progress.setVisibility(View.GONE);
        });
    }

    private void startPhoneNumberVerification() {
        String phoneNumber = getPhoneNumberInFormatVietNamese();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(2L, TimeUnit.MINUTES)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        loginBinding.progress.setVisibility(View.VISIBLE);
        disableTouchScreen();
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", user.getUid());
                    editor.apply();
                    getActivity();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: "+ e.getMessage() );
                    Toast.makeText(LoginActivity.this, "Mã xác thực không hợp lệ, vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                    loginBinding.progress.setVisibility(View.GONE);
                    enableTouchScreen();
                });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(2L, TimeUnit.MINUTES)
                .setActivity(this)
                .setCallbacks(callbacks)
                .setForceResendingToken(token)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void enabledVerifyForm(boolean b){
        if(!b){
            loginBinding.verifyLn.setVisibility(View.GONE);
            loginBinding.phoneLn.setVisibility(View.VISIBLE);
        }else {
            loginBinding.verifyLn.setVisibility(View.VISIBLE);
            loginBinding.phoneLn.setVisibility(View.GONE);
        }

    }

    private boolean validatePhoneNumber(String number){
       // return Pattern.compile("(84|0[3|5|7|8|9])+([0-9]{8})").matcher(number).matches();
        return true;
    }

    private String getPhoneNumberInFormatVietNamese(){
        String phoneNumber = loginBinding.edtPhoneNumber.getText().toString().trim();
        //return "+84" + phoneNumber.substring(1);
        return "+1 555-521-5554";
    }

    private void getActivity() {
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef;
            myRef = database.getReference("Users");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(token)) {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, InformationUserActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("token", token);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}