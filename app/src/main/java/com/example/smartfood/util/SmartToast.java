package com.example.smartfood.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter01.R;

public class SmartToast {
    private SmartToast() {
    }

    public static void show(Context context, String message) {
        if (context == null) {
            return;
        }
        String safeMessage = message == null ? "" : message.trim();
        View view = LayoutInflater.from(context).inflate(R.layout.view_smart_toast, null);
        TextView titleText = view.findViewById(R.id.tv_toast_title);
        TextView messageText = view.findViewById(R.id.tv_toast_message);

        titleText.setText(buildTitle(safeMessage));
        messageText.setText(buildDetail(safeMessage));

        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, dp(context, 54));
        toast.setView(view);
        toast.show();
    }

    private static String buildTitle(String message) {
        if (message.length() == 0) {
            return "智膳管家";
        }
        int commaIndex = message.indexOf('，');
        if (commaIndex > 0 && commaIndex <= 8) {
            return message.substring(0, commaIndex);
        }
        if (message.contains("成功") || message.startsWith("已")) {
            return "操作成功";
        }
        if (message.contains("失败") || message.contains("错误") || message.contains("不存在")) {
            return "操作失败";
        }
        if (message.contains("不能为空") || message.contains("请选择") || message.contains("不一致") || message.contains("必须")) {
            return "请检查输入";
        }
        return "智膳管家";
    }

    private static String buildDetail(String message) {
        if (message.length() == 0) {
            return "请稍后重试";
        }
        int commaIndex = message.indexOf('，');
        if (commaIndex > 0 && commaIndex + 1 < message.length()) {
            return message.substring(commaIndex + 1);
        }
        return message;
    }

    private static int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
