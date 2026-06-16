package com.example.smartfood.util;

import com.example.smartfood.entity.FoodNutritionEntity;
import com.example.smartfood.entity.MealRecordEntity;
import com.example.smartfood.entity.NutritionTargetEntity;
import com.example.smartfood.model.NutritionAnalyzeResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NutritionUtil {
    public static NutritionAnalyzeResult analyzeTodayNutrition(
            List<MealRecordEntity> records,
            List<FoodNutritionEntity> nutritionList,
            NutritionTargetEntity target
    ) {
        return analyzePeriodNutrition(records, nutritionList, target, 1, DateUtil.getToday(), DateUtil.getToday());
    }

    public static NutritionAnalyzeResult analyzePeriodNutrition(
            List<MealRecordEntity> records,
            List<FoodNutritionEntity> nutritionList,
            NutritionTargetEntity target,
            int dayCount,
            String periodStart,
            String periodEnd
    ) {
        NutritionTargetEntity effectiveTarget = resolveTarget(target);
        NutritionAnalyzeResult result = new NutritionAnalyzeResult();
        result.dayCount = Math.max(1, dayCount);
        result.periodStart = periodStart;
        result.periodEnd = periodEnd;
        result.recordCount = records == null ? 0 : records.size();
        Set<String> mealDays = new HashSet<>();
        for (MealRecordEntity record : records) {
            mealDays.add(record.mealDate);
            FoodNutritionEntity food = findFood(record.foodName, nutritionList);
            if (food == null) {
                continue;
            }
            double ratio = record.amountGram / 100.0;
            result.calories += ratio * food.calories;
            result.protein += ratio * food.protein;
            result.fat += ratio * food.fat;
            result.carbohydrate += ratio * food.carbohydrate;
            result.fiber += ratio * food.fiber;
            result.calcium += ratio * food.calcium;
            result.iron += ratio * food.iron;
            result.vitaminC += ratio * food.vitaminC;
        }
        result.mealDayCount = mealDays.size();
        result.suggestion = buildSuggestion(result, effectiveTarget, result.dayCount);
        result.nextMealRecommend = buildNextMealRecommend(result, effectiveTarget, result.dayCount);
        return result;
    }

    private static NutritionTargetEntity resolveTarget(NutritionTargetEntity target) {
        if (target != null) {
            return target;
        }
        return new NutritionTargetEntity(-1, 2000, 60, 60, 250, 25, 800, 12, 100);
    }

    private static FoodNutritionEntity findFood(String name, List<FoodNutritionEntity> list) {
        for (FoodNutritionEntity item : list) {
            if (item.foodName.equals(name)) {
                return item;
            }
        }
        return null;
    }

    private static String buildSuggestion(NutritionAnalyzeResult result, NutritionTargetEntity target, int days) {
        double proteinGap = target.proteinTarget * days - result.protein;
        double fiberGap = target.fiberTarget * days - result.fiber;
        double vitaminCGap = target.vitaminCTarget * days - result.vitaminC;
        if (proteinGap > 0 || fiberGap > 0 || vitaminCGap > 0) {
            return "根据已记录饮食估算，当前时间段可能存在部分营养摄入不足。系统会按实际已食用食物计算缺口。";
        }
        return "根据已记录饮食估算，主要营养摄入已接近或达到当前时间段目标。";
    }

    private static String buildNextMealRecommend(NutritionAnalyzeResult result, NutritionTargetEntity target, int days) {
        double proteinRatio = target.proteinTarget <= 0 ? 1 : result.protein / (target.proteinTarget * days);
        double fiberRatio = target.fiberTarget <= 0 ? 1 : result.fiber / (target.fiberTarget * days);
        double vitaminCRatio = target.vitaminCTarget <= 0 ? 1 : result.vitaminC / (target.vitaminCTarget * days);
        double calciumRatio = target.calciumTarget <= 0 ? 1 : result.calcium / (target.calciumTarget * days);
        double ironRatio = target.ironTarget <= 0 ? 1 : result.iron / (target.ironTarget * days);

        String weakest = "protein";
        double minRatio = proteinRatio;
        if (fiberRatio < minRatio) {
            weakest = "fiber";
            minRatio = fiberRatio;
        }
        if (vitaminCRatio < minRatio) {
            weakest = "vitaminC";
            minRatio = vitaminCRatio;
        }
        if (calciumRatio < minRatio) {
            weakest = "calcium";
            minRatio = calciumRatio;
        }
        if (ironRatio < minRatio) {
            weakest = "iron";
        }

        if ("fiber".equals(weakest)) {
            return "根据已食用食物计算，膳食纤维相对不足。下一餐建议搭配西兰花、胡萝卜、燕麦、苹果或其他蔬果。";
        }
        if ("vitaminC".equals(weakest)) {
            return "根据已食用食物计算，维生素 C 相对不足。下一餐建议搭配西红柿、橙子、青椒、猕猴桃等。";
        }
        if ("calcium".equals(weakest)) {
            return "根据已食用食物计算，钙摄入相对不足。下一餐或加餐可选择牛奶、酸奶、豆腐等。";
        }
        if ("iron".equals(weakest)) {
            return "根据已食用食物计算，铁摄入相对不足。下一餐可选择牛肉、菠菜、豆制品等搭配。";
        }
        if (minRatio >= 1) {
            return "当前记录显示主要营养目标完成较好。下一餐可保持均衡，并优先消耗临期食材。";
        }
        return "根据已食用食物计算，蛋白质相对不足。下一餐建议选择鸡蛋、牛奶、豆腐、鸡胸肉、鱼肉等。";
    }
}
