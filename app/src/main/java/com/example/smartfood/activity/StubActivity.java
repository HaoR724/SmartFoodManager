package com.example.smartfood.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter01.R;

public abstract class StubActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_form_stub);
        TextView title = findViewById(R.id.tv_title);
        TextView body = findViewById(R.id.tv_body);
        title.setText(getPageTitle());
        body.setText(getPageBody());
    }

    protected abstract String getPageTitle();

    protected abstract String getPageBody();
}
