package com.example.smartfood.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter01.R;
import com.example.smartfood.repository.DataCallback;
import com.example.smartfood.repository.UserRepository;
import com.example.smartfood.util.SmartToast;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText confirmPasswordEdit;
    private EditText nicknameEdit;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_register);
        userRepository = new UserRepository(this);
        usernameEdit = findViewById(R.id.et_username);
        passwordEdit = findViewById(R.id.et_password);
        confirmPasswordEdit = findViewById(R.id.et_confirm_password);
        nicknameEdit = findViewById(R.id.et_nickname);
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        String username = usernameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String confirmPassword = confirmPasswordEdit.getText().toString().trim();
        String nickname = nicknameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            SmartToast.show(this, "用户名和密码不能为空");
            return;
        }
        if (!password.equals(confirmPassword)) {
            SmartToast.show(this, "两次输入的密码不一致");
            return;
        }
        if (TextUtils.isEmpty(nickname)) {
            nickname = username;
        }
        userRepository.register(username, password, nickname, new DataCallback<String>() {
            @Override
            public void onResult(final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartToast.show(RegisterActivity.this, data);
                        if ("注册成功".equals(data)) {
                            finish();
                        }
                    }
                });
            }
        });
    }
}
