package com.example.smartfood.util;

import java.util.ArrayList;
import java.util.List;

public class FoodCatalog {
    public static final String CATEGORY_ALL = "全部分类";

    public static final String[] CATEGORIES = {
            "叶菜类", "根茎类", "瓜茄类", "菌菇类", "水果类", "肉禽类",
            "水产类", "蛋奶类", "豆制品", "主食谷物", "调味品", "坚果杂粮"
    };

    public static final String[][] INGREDIENTS = {
            {"西兰花","叶菜类"},{"紫甘蓝","叶菜类"},{"油麦菜","叶菜类"},{"小白菜","叶菜类"},{"上海青","叶菜类"},
            {"娃娃菜","叶菜类"},{"菠菜","叶菜类"},{"生菜","叶菜类"},{"苦菊","叶菜类"},{"空心菜","叶菜类"},
            {"茼蒿","叶菜类"},{"芹菜","叶菜类"},{"韭菜","叶菜类"},{"香菜","叶菜类"},{"油菜","叶菜类"},
            {"苋菜","叶菜类"},{"芥蓝","叶菜类"},{"莴苣叶","叶菜类"},{"甘蓝菜","叶菜类"},{"罗马生菜","叶菜类"},
            {"土豆","根茎类"},{"胡萝卜","根茎类"},{"白萝卜","根茎类"},{"红薯","根茎类"},{"山药","根茎类"},
            {"莲藕","根茎类"},{"芋头","根茎类"},{"莴笋","根茎类"},{"竹笋","根茎类"},{"洋葱","根茎类"},
            {"大葱","根茎类"},{"生姜","根茎类"},{"大蒜","根茎类"},{"紫薯","根茎类"},{"甜菜根","根茎类"},
            {"西红柿","瓜茄类"},{"黄瓜","瓜茄类"},{"茄子","瓜茄类"},{"青椒","瓜茄类"},{"红椒","瓜茄类"},
            {"彩椒","瓜茄类"},{"南瓜","瓜茄类"},{"冬瓜","瓜茄类"},{"丝瓜","瓜茄类"},{"苦瓜","瓜茄类"},
            {"西葫芦","瓜茄类"},{"佛手瓜","瓜茄类"},{"秋葵","瓜茄类"},{"玉米","瓜茄类"},{"豌豆","瓜茄类"},
            {"香菇","菌菇类"},{"金针菇","菌菇类"},{"杏鲍菇","菌菇类"},{"平菇","菌菇类"},{"口蘑","菌菇类"},
            {"海鲜菇","菌菇类"},{"白玉菇","菌菇类"},{"茶树菇","菌菇类"},{"木耳","菌菇类"},{"银耳","菌菇类"},
            {"苹果","水果类"},{"香蕉","水果类"},{"橙子","水果类"},{"猕猴桃","水果类"},{"草莓","水果类"},
            {"蓝莓","水果类"},{"葡萄","水果类"},{"梨","水果类"},{"桃子","水果类"},{"西瓜","水果类"},
            {"哈密瓜","水果类"},{"柚子","水果类"},{"柠檬","水果类"},{"火龙果","水果类"},{"芒果","水果类"},
            {"鸡胸肉","肉禽类"},{"鸡腿肉","肉禽类"},{"鸡翅","肉禽类"},{"牛肉","肉禽类"},{"牛腩","肉禽类"},
            {"猪里脊","肉禽类"},{"猪五花肉","肉禽类"},{"猪排骨","肉禽类"},{"羊肉","肉禽类"},{"鸭肉","肉禽类"},
            {"火腿","肉禽类"},{"午餐肉","肉禽类"},{"培根","肉禽类"},{"牛肉丸","肉禽类"},{"鸡肉丸","肉禽类"},
            {"三文鱼","水产类"},{"鳕鱼","水产类"},{"带鱼","水产类"},{"鲈鱼","水产类"},{"龙利鱼","水产类"},
            {"虾仁","水产类"},{"基围虾","水产类"},{"扇贝","水产类"},{"鱿鱼","水产类"},{"蛤蜊","水产类"},
            {"鸡蛋","蛋奶类"},{"鸭蛋","蛋奶类"},{"牛奶","蛋奶类"},{"酸奶","蛋奶类"},{"奶酪","蛋奶类"},{"黄油","蛋奶类"},
            {"豆腐","豆制品"},{"嫩豆腐","豆制品"},{"老豆腐","豆制品"},{"豆皮","豆制品"},{"腐竹","豆制品"},{"豆干","豆制品"},{"千张","豆制品"},
            {"大米","主食谷物"},{"小米","主食谷物"},{"面条","主食谷物"},{"意大利面","主食谷物"},{"燕麦","主食谷物"},
            {"全麦面包","主食谷物"},{"馒头","主食谷物"},{"玉米面","主食谷物"},{"荞麦面","主食谷物"},
            {"生抽","调味品"},{"老抽","调味品"},{"蚝油","调味品"},{"食盐","调味品"},{"白糖","调味品"},
            {"黑胡椒","调味品"},{"辣椒粉","调味品"},{"香油","调味品"},{"米醋","调味品"},{"料酒","调味品"},
            {"花生","坚果杂粮"},{"核桃","坚果杂粮"},{"杏仁","坚果杂粮"},{"腰果","坚果杂粮"},{"黑芝麻","坚果杂粮"},
            {"红豆","坚果杂粮"},{"绿豆","坚果杂粮"},{"黄豆","坚果杂粮"}
    };

    public static String[] getCategoriesWithAll() {
        String[] result = new String[CATEGORIES.length + 1];
        result[0] = CATEGORY_ALL;
        System.arraycopy(CATEGORIES, 0, result, 1, CATEGORIES.length);
        return result;
    }

    public static String[] getNamesByCategory(String category) {
        List<String> names = new ArrayList<>();
        for (String[] item : INGREDIENTS) {
            if (CATEGORY_ALL.equals(category) || item[1].equals(category)) {
                names.add(item[0]);
            }
        }
        return names.toArray(new String[0]);
    }

    public static String getCategoryByName(String name) {
        for (String[] item : INGREDIENTS) {
            if (item[0].equals(name)) {
                return item[1];
            }
        }
        return CATEGORIES[0];
    }

    public static boolean isVegetableCategory(String category) {
        return "叶菜类".equals(category)
                || "根茎类".equals(category)
                || "瓜茄类".equals(category)
                || "菌菇类".equals(category);
    }

    public static String getDefaultUnit(String category) {
        if (isVegetableCategory(category)) {
            return "克";
        }
        if ("肉禽类".equals(category) || "水产类".equals(category) || "主食谷物".equals(category) || "坚果杂粮".equals(category)) {
            return "克";
        }
        if ("蛋奶类".equals(category)) {
            return "个/瓶";
        }
        if ("调味品".equals(category)) {
            return "瓶";
        }
        return "份";
    }

    public static String getDefaultStoragePlace(String category) {
        if ("肉禽类".equals(category) || "水产类".equals(category)) {
            return "冷冻";
        }
        if ("主食谷物".equals(category) || "调味品".equals(category) || "坚果杂粮".equals(category)) {
            return "常温";
        }
        return "冰箱";
    }
}
