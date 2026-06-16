package com.example.smartfood.util;

public class ShelfLifeUtil {
    public static class ShelfLife {
        public final int bestDays;
        public final int expireDays;

        public ShelfLife(int bestDays, int expireDays) {
            this.bestDays = bestDays;
            this.expireDays = expireDays;
        }
    }

    public static ShelfLife getShelfLife(String name, String category, String storagePlace) {
        if ("冷冻".equals(storagePlace)) {
            if ("肉禽类".equals(category) || "水产类".equals(category)) return new ShelfLife(30, 90);
            return new ShelfLife(20, 45);
        }
        if ("调味品".equals(category)) return new ShelfLife(180, 365);
        if ("主食谷物".equals(category) || "坚果杂粮".equals(category)) return new ShelfLife(60, 180);
        if ("蛋奶类".equals(category)) return getEggMilkShelfLife(name);
        if ("肉禽类".equals(category)) return new ShelfLife(2, 4);
        if ("水产类".equals(category)) return new ShelfLife(1, 3);
        if ("豆制品".equals(category)) return new ShelfLife(2, 4);
        if ("水果类".equals(category)) return getFruitShelfLife(name);
        if ("叶菜类".equals(category)) return new ShelfLife(2, 4);
        if ("菌菇类".equals(category)) return new ShelfLife(2, 5);
        if ("根茎类".equals(category)) return getRootShelfLife(name);
        if ("瓜茄类".equals(category)) return getMelonShelfLife(name);
        return new ShelfLife(3, 7);
    }

    public static String calculateBestEatDate(String buyDate, String name, String category, String storagePlace) {
        return DateUtil.addDays(buyDate, getShelfLife(name, category, storagePlace).bestDays);
    }

    public static String calculateExpireDate(String buyDate, String name, String category, String storagePlace) {
        return DateUtil.addDays(buyDate, getShelfLife(name, category, storagePlace).expireDays);
    }

    public static String getRuleDescription(String name, String category, String storagePlace) {
        ShelfLife shelfLife = getShelfLife(name, category, storagePlace);
        return "按" + category + "在" + storagePlace + "存放估算：建议 " + shelfLife.bestDays
                + " 天内食用，约 " + shelfLife.expireDays + " 天后过期。";
    }

    private static ShelfLife getEggMilkShelfLife(String name) {
        if ("牛奶".equals(name) || "酸奶".equals(name)) return new ShelfLife(3, 7);
        if ("奶酪".equals(name) || "黄油".equals(name)) return new ShelfLife(15, 30);
        return new ShelfLife(10, 20);
    }

    private static ShelfLife getFruitShelfLife(String name) {
        if ("香蕉".equals(name) || "草莓".equals(name) || "蓝莓".equals(name) || "桃子".equals(name)) return new ShelfLife(2, 5);
        if ("西瓜".equals(name) || "哈密瓜".equals(name) || "芒果".equals(name)) return new ShelfLife(3, 7);
        if ("柠檬".equals(name) || "柚子".equals(name) || "苹果".equals(name) || "梨".equals(name)) return new ShelfLife(7, 20);
        return new ShelfLife(4, 10);
    }

    private static ShelfLife getRootShelfLife(String name) {
        if ("大葱".equals(name) || "生姜".equals(name) || "大蒜".equals(name) || "洋葱".equals(name)) return new ShelfLife(10, 30);
        if ("土豆".equals(name) || "红薯".equals(name) || "紫薯".equals(name) || "山药".equals(name)) return new ShelfLife(7, 20);
        return new ShelfLife(4, 10);
    }

    private static ShelfLife getMelonShelfLife(String name) {
        if ("南瓜".equals(name) || "冬瓜".equals(name)) return new ShelfLife(7, 20);
        if ("西红柿".equals(name) || "黄瓜".equals(name) || "青椒".equals(name) || "红椒".equals(name) || "彩椒".equals(name)) return new ShelfLife(3, 7);
        return new ShelfLife(3, 8);
    }
}
