package com.example.connectfarmapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.connectfarmapplication.MainActivity;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ActivityLoginBinding;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding loginBinding;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth firebaseAuth;
    private String mVerifyId;
    private static final String TAG  = "Login";
    private SharedPreferences sharedPreferences;
    private Intent intent;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


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
       // firebaseAuth = FirebaseAuth.getInstance();

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

        // Callback registration
        callbackManager = CallbackManager.Factory.create();

        loginBinding.loginButton.setReadPermissions("email");
        loginBinding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token",  loginResult.getAccessToken().getUserId());
                editor.apply();
                getActivity();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loginBinding.btnBack.setOnClickListener(v -> {
            enabledVerifyForm(false);
            enableTouchScreen();
            loginBinding.progress.setVisibility(View.GONE);
        });
    }
    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
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
        return "+84" + phoneNumber.substring(1);
        //return "+84968406091";
    }

    private void getActivity() {
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            DataClient client = APIUtils.getDataClient();
            client.checkNewUser(token).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        if (response.body().equals("true")) {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, InformationUserActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "fails" + t.getMessage());
                }
            });
            enableTouchScreen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}