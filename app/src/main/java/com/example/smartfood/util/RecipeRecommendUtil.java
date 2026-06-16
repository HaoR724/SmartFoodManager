package com.example.smartfood.util;

import com.example.smartfood.entity.IngredientEntity;
import com.example.smartfood.entity.RecipeEntity;
import com.example.smartfood.entity.RecipeIngredientEntity;
import com.example.smartfood.model.MissingIngredient;
import com.example.smartfood.model.RecipeRecommendResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeRecommendUtil {
    public static List<RecipeRecommendResult> calculateRecommendations(
            List<IngredientEntity> userIngredients,
            List<RecipeEntity> recipes,
            List<RecipeIngredientEntity> relations,
            int limit
    ) {
        Set<String> ownedNames = new HashSet<>();
        Set<String> expiringNames = new HashSet<>();
        for (IngredientEntity ingredient : userIngredients) {
            if (ingredient.name == null) {
                continue;
            }
            String ingredientName = ingredient.name.trim();
            ownedNames.add(ingredientName);
            if (ExpireUtil.getExpireStatus(ingredient.expireDate) == ExpireUtil.STATUS_EXPIRING) {
                expiringNames.add(ingredientName);
            }
        }

        List<RecipeRecommendResult> results = new ArrayList<>();
        for (RecipeEntity recipe : recipes) {
            List<RecipeIngredientEntity> needed = findRelations(recipe.id, relations);
            if (needed.isEmpty()) {
                continue;
            }
            RecipeRecommendResult result = new RecipeRecommendResult();
            result.recipe = recipe;
            int expiringUsed = 0;
            for (RecipeIngredientEntity relation : needed) {
                String neededName = relation.ingredientName == null ? "" : relation.ingredientName.trim();
                if (ownedNames.contains(neededName)) {
                    result.ownedIngredients.add(neededName);
                    result.ownedCount++;
                    if (expiringNames.contains(neededName)) {
                        expiringUsed++;
                    }
                } else {
                    result.missingIngredients.add(new MissingIngredient(neededName, relation.amount, relation.unit));
                    result.missingCount++;
                }
            }
            // 只要菜谱中包含至少一个用户已经添加的库存食材，就显示为推荐。
            if (result.ownedCount == 0) {
                continue;
            }
            result.matchRate = result.ownedCount * 1.0 / needed.size();
            result.expireUseRate = expiringNames.isEmpty() ? 0 : expiringUsed * 1.0 / expiringNames.size();
            result.score = result.matchRate * 0.7 + result.expireUseRate * 0.3;
            result.usesExpiringIngredient = expiringUsed > 0;
            result.reason = buildReason(result);
            results.add(result);
        }

        Collections.sort(results, new Comparator<RecipeRecommendResult>() {
            @Override
            public int compare(RecipeRecommendResult o1, RecipeRecommendResult o2) {
                return Double.compare(o2.score, o1.score);
            }
        });
        if (results.size() > limit) {
            return new ArrayList<>(results.subList(0, limit));
        }
        return results;
    }

    private static List<RecipeIngredientEntity> findRelations(long recipeId, List<RecipeIngredientEntity> relations) {
        List<RecipeIngredientEntity> list = new ArrayList<>();
        for (RecipeIngredientEntity relation : relations) {
            if (relation.recipeId == recipeId) {
                list.add(relation);
            }
        }
        return list;
    }

    private static String buildReason(RecipeRecommendResult result) {
        String matched = result.ownedIngredients.isEmpty() ? "库存食材" : joinFirst(result.ownedIngredients, 3);
        if (result.usesExpiringIngredient) {
            return "该菜谱包含已添加的" + matched + "，并可优先消耗即将过期的食材。";
        }
        return "该菜谱包含已添加的" + matched + "，可根据缺少食材补充采购。";
    }

    private static String joinFirst(List<String> list, int maxCount) {
        StringBuilder builder = new StringBuilder();
        int count = Math.min(maxCount, list.size());
        for (int i = 0; i < count; i++) {
            if (i > 0) builder.append("、");
            builder.append(list.get(i));
        }
        if (list.size() > maxCount) {
            builder.append("等");
        }
        return builder.toString();
    }
}
