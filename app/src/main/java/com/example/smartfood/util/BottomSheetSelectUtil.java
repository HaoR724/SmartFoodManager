package com.example.smartfood.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.chapter01.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class BottomSheetSelectUtil {
    public interface OnItemSelectedListener {
        void onSelected(String value);
    }

    private BottomSheetSelectUtil() {
    }

    public static void show(Context context, String title, List<String> options, String currentValue,
                            final OnItemSelectedListener listener) {
        if (context == null || options == null || options.isEmpty()) {
            return;
        }
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(context, 18), dp(context, 14), dp(context, 18), dp(context, 18));
        root.setBackgroundColor(context.getResources().getColor(R.color.smart_background));

        TextView titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(context.getResources().getColor(R.color.smart_text_primary));
        titleView.setTextSize(20);
        titleView.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        titleView.setGravity(Gravity.CENTER);
        root.addView(titleView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(context, 42)
        ));

        ScrollView scrollView = new ScrollView(context);
        LinearLayout list = new LinearLayout(context);
        list.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(list, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        for (final String option : options) {
            TextView row = new TextView(context);
            row.setText(option);
            row.setGravity(Gravity.CENTER);
            row.setTextSize(16);
            row.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
            row.setTextColor(option.equals(currentValue)
                    ? Color.BLACK
                    : context.getResources().getColor(R.color.smart_text_primary));
            row.setBackgroundResource(option.equals(currentValue)
                    ? R.drawable.bg_filter_chip_selected
                    : R.drawable.bg_smart_button_secondary);
            row.setPadding(dp(context, 16), 0, dp(context, 16), 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(context, 48)
            );
            params.setMargins(0, dp(context, 7), 0, 0);
            list.addView(row, params);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onSelected(option);
                    }
                    dialog.dismiss();
                }
            });
        }

        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Math.min(dp(context, 420), dp(context, 64) + options.size() * dp(context, 55))
        ));
        dialog.setContentView(root);
        dialog.show();
    }

    private static int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
