package com.example.smartfood.util;

import android.content.Context;

import com.example.smartfood.entity.RecipeEntity;

import java.util.HashMap;
import java.util.Map;

public class RecipeImageUtil {
    private static final Map<String, String> FIXED_RECIPE_IMAGE_MAP = new HashMap<>();

    static {
        FIXED_RECIPE_IMAGE_MAP.put("\u756a\u8304\u9e21\u86cb\u9762", "recipe_breakfast_001");
        FIXED_RECIPE_IMAGE_MAP.put("\u7ea2\u85af\u9e21\u86cb\u65e9\u9910", "recipe_breakfast_002");
        FIXED_RECIPE_IMAGE_MAP.put("\u9e21\u86cb\u5168\u9ea6\u4e09\u660e\u6cbb", "recipe_breakfast_003");
        FIXED_RECIPE_IMAGE_MAP.put("\u9e21\u86cb\u852c\u83dc\u5377", "recipe_breakfast_004");
        FIXED_RECIPE_IMAGE_MAP.put("\u9e21\u80f8\u8089\u852c\u83dc\u4e09\u660e\u6cbb", "recipe_breakfast_005");
        FIXED_RECIPE_IMAGE_MAP.put("\u725b\u5976\u71d5\u9ea6\u7ca5", "recipe_breakfast_006");
        FIXED_RECIPE_IMAGE_MAP.put("\u82f9\u679c\u725b\u5976\u65e9\u9910", "recipe_breakfast_007");
        FIXED_RECIPE_IMAGE_MAP.put("\u5168\u9ea6\u9762\u5305\u725b\u5976\u5957\u9910", "recipe_breakfast_008");
        FIXED_RECIPE_IMAGE_MAP.put("\u9178\u5976\u84dd\u8393\u676f", "recipe_breakfast_009");
        FIXED_RECIPE_IMAGE_MAP.put("\u9999\u8549\u9178\u5976\u71d5\u9ea6\u676f", "recipe_breakfast_010");
        FIXED_RECIPE_IMAGE_MAP.put("\u5c0f\u7c73\u5357\u74dc\u7ca5", "recipe_breakfast_011");
        FIXED_RECIPE_IMAGE_MAP.put("\u7389\u7c73\u9e21\u86cb\u997c", "recipe_breakfast_012");
        FIXED_RECIPE_IMAGE_MAP.put("\u7d2b\u85af\u725b\u5976\u65e9\u9910", "recipe_breakfast_013");
    }

    private RecipeImageUtil() {
    }

    public static int getImageResId(Context context, RecipeEntity recipe) {
        if (recipe == null) {
            return getDrawableId(context, "recipe_default");
        }
        return getImageResId(context, recipe.name, recipe.category);
    }

    public static int getImageResId(Context context, String recipeName) {
        return getImageResId(context, recipeName, null);
    }

    public static int getImageResId(Context context, String recipeName, String category) {
        String fixedResourceName = FIXED_RECIPE_IMAGE_MAP.get(recipeName);
        int fixedImageResId = getDrawableId(context, fixedResourceName);
        if (fixedImageResId != 0) {
            return fixedImageResId;
        }
        String resourceName = resolveRecipeImageName(recipeName, category);
        int imageResId = getDrawableId(context, resourceName);
        if (imageResId != 0) {
            return imageResId;
        }
        return getDrawableId(context, "recipe_default");
    }

    private static String resolveRecipeImageName(String recipeName, String category) {
        String name = recipeName == null ? "" : recipeName;
        String type = category == null ? "" : category;
        String text = type + " " + name;

        if (containsAny(text, "\u6c64", "\u7fb9")) {
            return "recipe_soup";
        }
        if (containsAny(text, "\u9762\u6761", "\u610f\u5927\u5229\u9762", "\u7092\u9762", "\u835e\u9ea6\u9762", "\u725b\u8089\u9762", "\u9e21\u86cb\u9762", "\u8f6f\u9762")
                || name.endsWith("\u9762")) {
            return "recipe_noodles";
        }
        if (containsAny(text, "\u7ca5", "\u71d5\u9ea6", "\u7389\u7c73\u7ca5", "\u5c0f\u7c73\u7ca5", "\u7ea2\u85af\u71d5\u9ea6\u7ca5")) {
            return "recipe_grain_porridge";
        }
        if (containsAny(text, "\u751c\u54c1", "\u52a0\u9910", "\u9178\u5976\u676f", "\u6c34\u679c\u676f", "\u7ea2\u8c46\u6c99", "\u7eff\u8c46\u6c64", "\u829d\u9ebb\u7cca",
                "\u9178\u5976\u7897", "\u725b\u5976\u676f", "\u6c34\u679c", "\u84dd\u8393\u676f", "\u8292\u679c\u9178\u5976")) {
            return "recipe_dessert";
        }
        if (containsAny(text, "\u51cf\u8102", "\u6c99\u62c9", "\u8f7b\u98df", "\u4f4e\u8102", "\u852c\u83dc\u76d8", "\u5f69\u6912\u7897")) {
            return "recipe_healthy_salad";
        }
        if (containsAny(text, "\u7c73\u996d", "\u7092\u996d", "\u76d6\u996d", "\u9e21\u8089\u852c\u83dc\u996d", "\u996d", "\u5348\u9910")) {
            return "recipe_rice_bowl";
        }
        if (containsAny(text, "\u5bb6\u5e38\u83dc", "\u5feb\u624b\u83dc", "\u666e\u901a\u7092\u83dc", "\u6e05\u7092", "\u7092", "\u849c\u84c9", "\u7ea2\u70e7", "\u9999\u714e", "\u918b\u6e9c", "\u7096", "\u70e7")) {
            return "recipe_stir_fry";
        }
        return "recipe_default";
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static int getDrawableId(Context context, String resourceName) {
        if (context == null || resourceName == null) {
            return 0;
        }
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }
}
