package com.example.chapter01;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private WebView webView;
    private TextView titleText;
    private ProgressBar progressBar;

    private final String[] upNames = {
            "小约翰",
            "燕三嘤嘤嘤",
            "麻薯波比呀",
            "精罗伯爵",
            "我真没想重生啊"
    };

    private final String[] bvids = {
            "BV1kHLU6jE2Q",
            "BV1Dw4m1a7yk",
            "BV1NoVm62EFd",
            "BV1yTVr6dETL",
            "BV1j5cQzZEot"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);
        webView = findViewById(R.id.web_view);
        titleText = findViewById(R.id.title_text);
        progressBar = findViewById(R.id.progress_bar);
        Button btnBack = findViewById(R.id.btn_back);
        Button btnWeb = findViewById(R.id.btn_web);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, txt.class);
                startActivity(intent);
            }
        });

        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.neuq.edu.cn");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        setupWebView();
        listView.setAdapter(new UpAdapter());
        listView.setOnItemClickListener((parent, view, position, id) -> {
            titleText.setText("正在播放：" + upNames[position]);
            loadBilibiliVideo(bvids[position]);
            listView.setItemChecked(position, true);
        });

        titleText.setText("正在播放：" + upNames[0]);
        listView.setItemChecked(0, true);
        loadBilibiliVideo(bvids[0]);
    }

    private void loadBilibiliVideo(String bvid) {
        progressBar.setVisibility(View.VISIBLE);
        String html = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>"
                + "<style>"
                + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                + "body { background: #000; display: flex; justify-content: center; align-items: center; height: 100vh; overflow: hidden; }"
                + ".video-container { width: 100%; height: 100%; position: relative; }"
                + "iframe { width: 100%; height: 100%; border: none; position: absolute; top: 0; left: 0; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='video-container'>"
                + "<iframe src='https://player.bilibili.com/player.html?bvid=" + bvid
                + "&page=1&high_quality=1&autoplay=1&as_wide=1' "
                + "allow='autoplay; encrypted-media; fullscreen' "
                + "allowfullscreen='true' scrolling='no'></iframe>"
                + "</div>"
                + "</body>"
                + "</html>";
        webView.loadDataWithBaseURL("https://www.bilibili.com/", html, "text/html", "UTF-8", null);
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setUserAgentString(
                "Mozilla/5.0 (Linux; Android 10; SM-G960F) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/88.0.4324.93 Mobile Safari/537.36"
        );

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "加载失败：" + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
    }

    class UpAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return upNames.length;
        }

        @Override
        public Object getItem(int position) {
            return upNames[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this)
                        .inflate(android.R.layout.simple_list_item_activated_1, parent, false);
            }
            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(upNames[position]);
            textView.setPadding(30, 20, 30, 20);
            textView.setTextSize(14);
            textView.setTextColor(0xFF333333);
            return convertView;
        }
    }
}
