package com.example.smartfood.util;

import android.content.Context;

public class IngredientImageUtil {
    private IngredientImageUtil() {
    }

    public static int getImageResId(Context context, String ingredientName) {
        return getImageResId(context, ingredientName, findCategoryByName(ingredientName));
    }

    public static int getImageResId(Context context, String ingredientName, String category) {
        if (context == null) {
            return 0;
        }
        if (ingredientName != null) {
            for (int i = 0; i < FoodCatalog.INGREDIENTS.length; i++) {
                if (ingredientName.equals(FoodCatalog.INGREDIENTS[i][0])) {
                    String resourceName = String.format("ingredient_%03d", i + 1);
                    int imageResId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
                    if (imageResId != 0) {
                        return imageResId;
                    }
                    if (category == null || category.length() == 0) {
                        category = FoodCatalog.INGREDIENTS[i][1];
                    }
                }
            }
        }
        return getCategoryImageResId(context, category);
    }

    public static int getCategoryImageResId(Context context, String category) {
        if (context == null || category == null) {
            return 0;
        }
        String resourceName = null;
        if ("\u53f6\u83dc\u7c7b".equals(category) || "\u74dc\u8304\u7c7b".equals(category) || "\u83cc\u83c7\u7c7b".equals(category)) {
            resourceName = "leafy_vegetable";
        } else if ("\u6839\u830e\u7c7b".equals(category)) {
            resourceName = "root_vegetable";
        } else if ("\u6c34\u679c\u7c7b".equals(category)) {
            resourceName = "fruit";
        } else if ("\u8089\u79bd\u7c7b".equals(category)) {
            resourceName = "meat";
        } else if ("\u6c34\u4ea7\u7c7b".equals(category)) {
            resourceName = "seafood";
        } else if ("\u86cb\u5976\u7c7b".equals(category)) {
            resourceName = "dairy";
        } else if ("\u4e3b\u98df\u8c37\u7269".equals(category) || "\u8c46\u5236\u54c1".equals(category) || "\u575a\u679c\u6742\u7cae".equals(category)) {
            resourceName = "grain";
        } else if ("\u8c03\u5473\u54c1".equals(category)) {
            resourceName = "seasoning";
        }
        if (resourceName == null) {
            return 0;
        }
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    private static String findCategoryByName(String ingredientName) {
        if (ingredientName == null) {
            return null;
        }
        for (String[] item : FoodCatalog.INGREDIENTS) {
            if (ingredientName.equals(item[0])) {
                return item[1];
            }
        }
        return null;
    }
}
