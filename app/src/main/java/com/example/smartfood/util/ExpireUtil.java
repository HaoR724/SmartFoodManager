package com.example.smartfood.util;

import android.graphics.Color;

public class ExpireUtil {
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_EXPIRING = 1;
    public static final int STATUS_EXPIRED = 2;

    public static long getDaysLeft(String expireDate) {
        return DateUtil.daysBetween(DateUtil.getToday(), expireDate);
    }

    public static int getExpireStatus(String expireDate) {
        long daysLeft = getDaysLeft(expireDate);
        if (daysLeft < 0) {
            return STATUS_EXPIRED;
        }
        if (daysLeft <= 3) {
            return STATUS_EXPIRING;
        }
        return STATUS_NORMAL;
    }

    public static String getExpireStatusText(String expireDate) {
        int status = getExpireStatus(expireDate);
        if (status == STATUS_EXPIRED) {
            return "已过期";
        }
        if (status == STATUS_EXPIRING) {
            return "即将过期";
        }
        return "正常";
    }

    public static int getExpireColor(String expireDate) {
        int status = getExpireStatus(expireDate);
        if (status == STATUS_EXPIRED) {
            return Color.parseColor("#FF6B5F");
        }
        if (status == STATUS_EXPIRING) {
            return Color.parseColor("#FFA55B");
        }
        return Color.parseColor("#8FD694");
    }
}
