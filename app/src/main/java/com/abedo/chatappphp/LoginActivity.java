package com.abedo.chatappphp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.abedo.chatappphp.models.LoginResponse;
import com.abedo.chatappphp.models.User;
import com.abedo.chatappphp.utils.Session;
import com.abedo.chatappphp.webservices.WebService;
import com.fourhcode.forhutils.FUtilsValidation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    private EditText etEmail,etPassword;
    private Button btn_login;
    private TextView tvSignUP;
    private RelativeLayout rlltLoading ,rlltBody;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadUi();

        tvSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FUtilsValidation.isEmpty(etEmail, getString(R.string.enter_email))
                        && FUtilsValidation.isValidEmail(etEmail, getString(R.string.enter_valid_email))
                        && !FUtilsValidation.isEmpty(etPassword, getString(R.string.enter_password))
                ) {
                    setLoadingMode();
                    // create new user
                    final User user = new User();
                    user.email = etEmail.getText().toString();
                    user.password = etPassword.getText().toString();
                    // login User using Retrofit
                    WebService.getInstance().getApi().loginUser(user).enqueue(new Callback<LoginResponse>() {

                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            // check for status value comming from server (response of login-user.php file status)
                            if (response.body().status == 2) {
                                Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            } else if (response.body().status == 1) {
                               user.username = response.body().user.user_name;
                               user.id=Integer.parseInt(response.body().user.id);
                               user.isAdmin=response.body().user.is_user_admin.equals("1");
                                Session.getInstance().loginUser(user);
                                Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(goToMain);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            }
                            setNormalMode();
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            // print error message in logcat
                            setNormalMode();
                            Log.e(TAG, t.getLocalizedMessage());

                        }
                    });

                }
            }
        });
    }

    private void loadUi(){
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        tvSignUP=findViewById(R.id.tv_dont_have_account);
        rlltBody=findViewById(R.id.rllt_body);
        rlltLoading=findViewById(R.id.rllt_loading);
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
