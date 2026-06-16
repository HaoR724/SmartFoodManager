package com.example.smartfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chapter01.R;

public abstract class BaseInfoFragment extends Fragment {
    protected TextView titleText;
    protected TextView subtitleText;
    protected TextView bodyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smart_simple, container, false);
        titleText = view.findViewById(R.id.tv_title);
        subtitleText = view.findViewById(R.id.tv_subtitle);
        bodyText = view.findViewById(R.id.tv_body);
        titleText.setText(getTitle());
        subtitleText.setText(getSubtitle());
        bodyText.setText("数据加载中...");
        loadContent();
        return view;
    }

    protected void setBodyOnUiThread(final String text) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bodyText.setText(text);
            }
        });
    }

    protected abstract String getTitle();

    protected abstract String getSubtitle();

    protected abstract void loadContent();
}
