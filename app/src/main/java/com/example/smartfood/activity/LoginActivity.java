package com.example.smartfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter01.R;
import com.example.smartfood.entity.UserEntity;
import com.example.smartfood.repository.UserRepository;
import com.example.smartfood.util.SessionManager;
import com.example.smartfood.util.SmartToast;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEdit;
    private EditText passwordEdit;
    private UserRepository userRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_login);
        userRepository = new UserRepository(this);
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            openMain();
            return;
        }
        usernameEdit = findViewById(R.id.et_username);
        passwordEdit = findViewById(R.id.et_password);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        TextView registerText = findViewById(R.id.tv_register);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void login() {
        String username = usernameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            SmartToast.show(this, "用户名和密码不能为空");
            return;
        }
        userRepository.login(username, password, new com.example.smartfood.repository.DataCallback<UserEntity>() {
            @Override
            public void onResult(final UserEntity data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data == null) {
                            SmartToast.show(LoginActivity.this, "登录失败，请检查账号密码或后端服务");
                            return;
                        }
                        sessionManager.saveLoginUser(data.id);
                        SmartToast.show(LoginActivity.this, "登录成功");
                        openMain();
                    }
                });
            }
        });
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
