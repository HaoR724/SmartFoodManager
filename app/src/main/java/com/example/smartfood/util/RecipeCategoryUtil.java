package com.example.smartfood.util;

import com.example.smartfood.entity.RecipeEntity;

import java.util.ArrayList;
import java.util.List;

public class RecipeCategoryUtil {
    public static String getDisplayCategories(RecipeEntity recipe) {
        List<String> categories = getCategories(recipe);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            if (i > 0) builder.append(" / ");
            builder.append(categories.get(i));
        }
        return builder.toString();
    }

    public static boolean matchesCategory(RecipeEntity recipe, String selectedCategory) {
        if ("全部分类".equals(selectedCategory)) {
            return true;
        }
        return getCategories(recipe).contains(selectedCategory);
    }

    private static List<String> getCategories(RecipeEntity recipe) {
        List<String> categories = new ArrayList<>();
        addCategory(categories, recipe.category);
        String name = recipe.name == null ? "" : recipe.name;
        if (containsAny(name, "炒", "炖", "烧", "煎", "蒜蓉", "红烧", "醋溜", "鱼香")) {
            addCategory(categories, "家常菜");
        }
        if (containsAny(name, "清炒", "蒜蓉", "炒蛋", "炒鸡蛋", "香煎", "番茄豆腐")) {
            addCategory(categories, "快手菜");
        }
        if (containsAny(name, "汤", "羹")) {
            addCategory(categories, "汤类");
            addCategory(categories, "晚餐");
        }
        if (containsAny(name, "粥", "早餐", "燕麦", "牛奶", "酸奶", "面包", "三明治", "吐司")) {
            addCategory(categories, "早餐");
        }
        if (containsAny(name, "饭", "面", "馒头", "饼")) {
            addCategory(categories, "主食");
        }
        if (containsAny(name, "西兰花", "鸡胸肉", "沙拉", "轻食", "低脂", "龙利鱼", "三文鱼")) {
            addCategory(categories, "减脂餐");
        }
        if (containsAny(name, "软面", "玉米粥", "鸡蛋羹", "小米粥", "土豆泥")) {
            addCategory(categories, "儿童餐");
        }
        if (containsAny(name, "清炒", "蒜蓉", "豆腐汤", "青菜", "菠菜", "生菜", "西兰花")) {
            addCategory(categories, "晚餐");
        }
        if (containsAny(name, "酸奶", "水果杯", "燕麦杯", "红豆沙", "绿豆汤", "芝麻糊", "牛奶杯")) {
            addCategory(categories, "甜品/加餐");
        }
        return categories;
    }

    private static void addCategory(List<String> categories, String category) {
        if (category == null || category.trim().length() == 0) {
            return;
        }
        String[] parts = category.split("[/、,， ]+");
        for (String part : parts) {
            if (part.length() > 0 && !categories.contains(part)) {
                categories.add(part);
            }
        }
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
