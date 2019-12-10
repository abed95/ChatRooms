package com.abedo.chatappphp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abedo.chatappphp.models.MainResponse;
import com.abedo.chatappphp.models.User;
import com.abedo.chatappphp.webservices.WebService;
import com.fourhcode.forhutils.FUtilsValidation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    private EditText etUsername, etEmail, etPassword, etRepeatPassword;
    private Button btn_signup;
    private RelativeLayout rlltLoading, rlltBody;
    private ProgressBar prgsLoading;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loadUi();
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);


            }
        });

        /*قمنا بإنشاء اوبجكت User جديد ونقوم باسناد القيم اليه من الـ Edittexts والتى ادخل المستخدم بها القيم

ثم بعد ذلك استخدمنا ال singletone Websrevice باستدعاء الميثود getInstance يليلها getAPI ثم استخدمنا الميثود registerUser التى كتبناها مسبقا فى ملف API

ثم هناك احتمالين onResponse فى حالة نجح الاتصال بالسيرفر او onFailure فى حالة فشل الاتصال بالسيرفر لسبب مثل تغير ال url او توقف السيرفر او اى امر اخر .

بداخل onResponse لديك response ويمكنك الوصول للاوبجكت MainResponse القادم من السيرفر عن طريق response.body()  ثم نستخدم بعدها .statues مثلا لنجلب ال stause او message التى جعلنا ملف php فى ال api يعود لنا بهم

كما ترى الامر سلس جدا وريتروفيت تقوم بتحويل الـ JSON الى Models والعكس.*/
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FUtilsValidation.isEmpty(etUsername, getString(R.string.enter_username))
                        && !FUtilsValidation.isEmpty(etEmail, getString(R.string.enter_email))
                        && FUtilsValidation.isValidEmail(etEmail, getString(R.string.enter_valid_email))
                        && !FUtilsValidation.isEmpty(etPassword, getString(R.string.enter_password))
                        && !FUtilsValidation.isEmpty(etRepeatPassword, getString(R.string.enter_password_again))
                        && FUtilsValidation.isPasswordEqual(etPassword, etRepeatPassword, getString(R.string.password_isnt_equal))
                ) {
                    setLoadingMode();
                    // create new user object and set data from editTexts
                    final User user = new User();
                    user.username = etUsername.getText().toString();
                    user.email = etEmail.getText().toString();
                    user.password = etPassword.getText().toString();
                    // register user with retorfit
                    WebService.getInstance().getApi().registerUser(user).enqueue(new Callback<MainResponse>() {
                        @Override
                        public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                            if (response.body().status == 2) {
                                Toast.makeText(RegisterActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                setNormalMode();
                            } else if (response.body().status == 1) {
                                Toast.makeText(RegisterActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                // go to login activity

                                Intent gotToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                                gotToLogin.putExtra("email", user.email);
                                gotToLogin.putExtra("pass", user.password);
                                startActivity(gotToLogin);
                                finish();
                            } else {
                                setNormalMode();
                                Toast.makeText(RegisterActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            }
                            setNormalMode();
                        }

                        @Override
                        public void onFailure(Call<MainResponse> call, Throwable t) {
                            setNormalMode();
                            Log.e(TAG, t.getLocalizedMessage());

                        }
                    });

                }
            }
        });
    }

    private void loadUi() {
        rlltLoading = findViewById(R.id.rllt_loading);
        rlltBody = findViewById(R.id.rllt_body);
        prgsLoading = findViewById(R.id.prgs_loading);
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etRepeatPassword = findViewById(R.id.et_repeat_password);
        tvLogin = findViewById(R.id.tv_already_have_account);
        btn_signup = findViewById(R.id.btn_signup);
    }

    // set loading layout visible and hide body layout
    private void setLoadingMode() {
        rlltLoading.setVisibility(View.VISIBLE);
        rlltBody.setVisibility(View.GONE);
    }

    // set body layout visible and hide loading layout
    private void setNormalMode() {
        rlltLoading.setVisibility(View.GONE);
        rlltBody.setVisibility(View.VISIBLE);
    }

}
