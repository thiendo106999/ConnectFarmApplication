package com.example.connectfarmapplication.ui;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.connectfarmapplication.MainActivity;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getToken();
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
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
            if(phoneNumber.isEmpty() || !validatePhoneNumber(phoneNumber)){
                loginBinding.edtPhoneNumber.setError("Số điện thoại không hợp lệ, vui lòng kiểm tra lại");
            }else{
                startPhoneNumberVerification();
                enabledVerifyForm(true);
            }
        });

        //send opt to login
        loginBinding.btnSentOtp.setOnClickListener(v->{
            String code = loginBinding.edtOpt.getText().toString().trim();
            verifyPhoneNumberWithCode(mVerifyId, code );
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(s, forceResendingToken);

                mVerifyId =  s;
                forceResendingToken = token;
            }
        };

        //resend opt code
        loginBinding.resend.setOnClickListener(v->{
            resendVerificationCode(getPhoneNumberInFormatVietNamese(), forceResendingToken);
        });

        loginBinding.btnBack.setOnClickListener(v -> {
            enabledVerifyForm(false);
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
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", user.getUid());
                    editor.apply();

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: "+ e.getMessage() );
                    }
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
        return Pattern.compile("(84|0[3|5|7|8|9])+([0-9]{8})").matcher(number).matches();
    }

    private String getPhoneNumberInFormatVietNamese(){
        String phoneNumber = loginBinding.edtPhoneNumber.getText().toString().trim();
        return "+84" + phoneNumber.substring(1);
    }

    private void getToken(){
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if(token != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

    }
}