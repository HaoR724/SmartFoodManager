package com.example.smartfood.database;

import com.example.smartfood.entity.FoodNutritionEntity;
import com.example.smartfood.entity.IngredientEntity;
import com.example.smartfood.entity.NutritionTargetEntity;
import com.example.smartfood.entity.RecipeEntity;
import com.example.smartfood.entity.RecipeIngredientEntity;
import com.example.smartfood.entity.UserEntity;
import com.example.smartfood.util.DateUtil;
import com.example.smartfood.util.FoodCatalog;
import com.example.smartfood.util.ShelfLifeUtil;

import java.util.ArrayList;
import java.util.List;

public class DefaultDataSeeder {
    private static final String[][] INGREDIENTS = {
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

    private static final String[][] RECIPES = {
            {"牛奶燕麦粥","早餐","牛奶|燕麦|香蕉"},{"鸡蛋全麦三明治","早餐","鸡蛋|全麦面包|生菜"},{"香蕉酸奶燕麦杯","早餐","香蕉|酸奶|燕麦"},{"苹果牛奶早餐","早餐","苹果|牛奶|全麦面包"},{"鸡蛋蔬菜卷","早餐","鸡蛋|生菜|胡萝卜"},
            {"紫薯牛奶早餐","早餐","紫薯|牛奶|鸡蛋"},{"煎蛋吐司","早餐","鸡蛋|全麦面包|黄油"},{"番茄鸡蛋面","早餐","西红柿|鸡蛋|面条"},{"青菜鸡蛋面","早餐","小白菜|鸡蛋|面条"},{"玉米鸡蛋饼","早餐","玉米|鸡蛋|玉米面"},
            {"小米南瓜粥","早餐","小米|南瓜"},{"红薯鸡蛋早餐","早餐","红薯|鸡蛋|牛奶"},{"酸奶蓝莓杯","早餐","酸奶|蓝莓|燕麦"},{"全麦面包牛奶套餐","早餐","全麦面包|牛奶|鸡蛋"},{"鸡胸肉蔬菜三明治","早餐","鸡胸肉|全麦面包|生菜|西红柿"},
            {"西红柿炒鸡蛋","午餐","西红柿|鸡蛋|大葱|食盐"},{"土豆炖牛肉","午餐","土豆|牛肉|胡萝卜|洋葱"},{"胡萝卜牛肉饭","午餐","胡萝卜|牛肉|大米"},{"鸡胸肉西兰花饭","午餐","鸡胸肉|西兰花|大米"},{"青椒猪里脊","午餐","青椒|猪里脊|生抽"},
            {"洋葱牛肉","午餐","洋葱|牛肉|黑胡椒"},{"香菇鸡肉饭","午餐","香菇|鸡腿肉|大米"},{"茄子肉末饭","午餐","茄子|猪里脊|大米"},{"西兰花虾仁饭","午餐","西兰花|虾仁|大米"},{"土豆鸡块","午餐","土豆|鸡腿肉|生抽"},
            {"莲藕排骨饭","午餐","莲藕|猪排骨|大米"},{"彩椒鸡丁","午餐","彩椒|鸡胸肉|生抽"},{"西葫芦炒鸡蛋","午餐","西葫芦|鸡蛋|食盐"},{"鱼香茄子","午餐","茄子|青椒|米醋|白糖"},{"番茄牛腩饭","午餐","西红柿|牛腩|大米"},
            {"清炒西兰花","晚餐","西兰花|大蒜|食盐"},{"蒜蓉油麦菜","晚餐","油麦菜|大蒜|食盐"},{"菠菜鸡蛋汤","晚餐","菠菜|鸡蛋|香油"},{"香菇青菜","晚餐","香菇|上海青|食盐"},{"清炒紫甘蓝","晚餐","紫甘蓝|大蒜|米醋"},
            {"豆腐青菜汤","晚餐","豆腐|小白菜|食盐"},{"冬瓜虾仁汤","晚餐","冬瓜|虾仁|香菜"},{"苦瓜炒鸡蛋","晚餐","苦瓜|鸡蛋|食盐"},{"番茄豆腐汤","晚餐","西红柿|豆腐|香菜"},{"清炒空心菜","晚餐","空心菜|大蒜|食盐"},
            {"芹菜炒牛肉","晚餐","芹菜|牛肉|生抽"},{"西红柿龙利鱼","晚餐","西红柿|龙利鱼|生姜"},{"蒜蓉生菜","晚餐","生菜|大蒜|蚝油"},{"茼蒿豆腐汤","晚餐","茼蒿|豆腐|食盐"},{"香煎三文鱼配西兰花","晚餐","三文鱼|西兰花|黑胡椒"},
            {"鸡蛋炒饭","家常菜","鸡蛋|大米|大葱"},{"牛肉炒饭","家常菜","牛肉|大米|胡萝卜"},{"虾仁炒饭","家常菜","虾仁|大米|豌豆"},{"土豆丝","家常菜","土豆|青椒|米醋"},{"青椒土豆片","家常菜","青椒|土豆|生抽"},
            {"红烧茄子","家常菜","茄子|生抽|老抽|白糖"},{"醋溜白菜","家常菜","娃娃菜|米醋|大蒜"},{"韭菜炒鸡蛋","家常菜","韭菜|鸡蛋|食盐"},{"洋葱炒鸡蛋","家常菜","洋葱|鸡蛋|食盐"},{"香菇炒油菜","家常菜","香菇|油菜|蚝油"},
            {"木耳炒鸡蛋","家常菜","木耳|鸡蛋|青椒"},{"莴笋炒肉片","家常菜","莴笋|猪里脊|生抽"},{"胡萝卜炒鸡蛋","家常菜","胡萝卜|鸡蛋|大葱"},{"西红柿炖牛腩","家常菜","西红柿|牛腩|洋葱"},{"土豆烧排骨","家常菜","土豆|猪排骨|老抽"},
            {"番茄鸡蛋汤","汤类","西红柿|鸡蛋|香油"},{"紫菜蛋花汤","汤类","鸡蛋|香油|食盐"},{"冬瓜排骨汤","汤类","冬瓜|猪排骨|生姜"},{"山药排骨汤","汤类","山药|猪排骨|生姜"},{"莲藕排骨汤","汤类","莲藕|猪排骨|生姜"},
            {"香菇鸡汤","汤类","香菇|鸡腿肉|生姜"},{"西红柿牛肉汤","汤类","西红柿|牛肉|香菜"},{"豆腐蘑菇汤","汤类","豆腐|口蘑|小白菜"},{"银耳雪梨汤","汤类","银耳|梨|白糖"},{"青菜豆腐汤","汤类","上海青|豆腐|食盐"},
            {"玉米排骨汤","汤类","玉米|猪排骨|胡萝卜"},{"白萝卜牛肉汤","汤类","白萝卜|牛肉|生姜"},{"菠菜豆腐汤","汤类","菠菜|豆腐|食盐"},{"蛤蜊豆腐汤","汤类","蛤蜊|豆腐|生姜"},{"冬瓜虾仁汤","汤类","冬瓜|虾仁|香菜"},
            {"鸡胸肉西兰花","减脂餐","鸡胸肉|西兰花|黑胡椒"},{"牛肉生菜沙拉","减脂餐","牛肉|生菜|黄瓜"},{"虾仁西兰花","减脂餐","虾仁|西兰花|大蒜"},{"三文鱼蔬菜盘","减脂餐","三文鱼|罗马生菜|彩椒"},{"鸡蛋菠菜沙拉","减脂餐","鸡蛋|菠菜|黄瓜"},
            {"豆腐紫甘蓝沙拉","减脂餐","豆腐|紫甘蓝|生菜"},{"牛奶燕麦餐","减脂餐","牛奶|燕麦|苹果"},{"鸡胸肉彩椒碗","减脂餐","鸡胸肉|彩椒|大米"},{"龙利鱼西葫芦","减脂餐","龙利鱼|西葫芦|黑胡椒"},{"西红柿鸡蛋豆腐","减脂餐","西红柿|鸡蛋|豆腐"},
            {"低脂鸡肉蔬菜汤","减脂餐","鸡胸肉|小白菜|胡萝卜"},{"牛肉胡萝卜轻食","减脂餐","牛肉|胡萝卜|生菜"},{"西兰花鸡蛋沙拉","减脂餐","西兰花|鸡蛋|罗马生菜"},{"苹果酸奶碗","减脂餐","苹果|酸奶|燕麦"},{"燕麦香蕉杯","减脂餐","燕麦|香蕉|牛奶"},
            {"番茄鸡蛋软面","儿童餐","西红柿|鸡蛋|面条"},{"牛奶玉米粥","儿童餐","牛奶|玉米|小米"},{"胡萝卜鸡蛋饼","儿童餐","胡萝卜|鸡蛋|玉米面"},{"土豆泥鸡肉丸","儿童餐","土豆|鸡肉丸|牛奶"},{"香蕉酸奶杯","儿童餐","香蕉|酸奶|燕麦"},
            {"虾仁鸡蛋羹","儿童餐","虾仁|鸡蛋|香油"},{"南瓜小米粥","儿童餐","南瓜|小米"},{"牛肉土豆泥","儿童餐","牛肉|土豆|胡萝卜"},{"苹果燕麦粥","儿童餐","苹果|燕麦|牛奶"},{"西兰花鸡蛋饭","儿童餐","西兰花|鸡蛋|大米"},
            {"蒜蓉西兰花","快手菜","西兰花|大蒜|食盐"},{"清炒菠菜","快手菜","菠菜|大蒜|食盐"},{"青椒炒蛋","快手菜","青椒|鸡蛋|食盐"},{"番茄豆腐","快手菜","西红柿|豆腐|生抽"},{"香煎鸡胸肉","快手菜","鸡胸肉|黑胡椒|食盐"},
            {"黄瓜炒鸡蛋","快手菜","黄瓜|鸡蛋|食盐"},{"蒜蓉金针菇","快手菜","金针菇|大蒜|生抽"},{"洋葱炒牛肉","快手菜","洋葱|牛肉|黑胡椒"},{"西葫芦炒蛋","快手菜","西葫芦|鸡蛋|食盐"},{"香菇炒青菜","快手菜","香菇|上海青|蚝油"},
            {"番茄牛肉面","主食","西红柿|牛肉|面条"},{"青菜鸡蛋面","主食","小白菜|鸡蛋|面条"},{"虾仁意大利面","主食","虾仁|意大利面|西红柿"},{"鸡肉炒面","主食","鸡胸肉|面条|青椒"},{"牛肉荞麦面","主食","牛肉|荞麦面|上海青"},
            {"鸡蛋炒面","主食","鸡蛋|面条|大葱"},{"蔬菜燕麦粥","主食","燕麦|胡萝卜|小白菜"},{"红薯燕麦粥","主食","红薯|燕麦|牛奶"},{"玉米面饼","主食","玉米面|鸡蛋|牛奶"},{"鸡肉蔬菜饭","主食","鸡胸肉|大米|西兰花"},
            {"银耳雪梨羹","甜品/加餐","银耳|梨|白糖"},{"酸奶水果杯","甜品/加餐","酸奶|苹果|香蕉"},{"蓝莓酸奶碗","甜品/加餐","蓝莓|酸奶|燕麦"},{"香蕉燕麦饼","甜品/加餐","香蕉|燕麦|鸡蛋"},{"苹果酸奶沙拉","甜品/加餐","苹果|酸奶|核桃"},
            {"牛奶红豆沙","甜品/加餐","牛奶|红豆|白糖"},{"绿豆汤","甜品/加餐","绿豆|白糖"},{"核桃黑芝麻糊","甜品/加餐","核桃|黑芝麻|牛奶"},{"芒果酸奶杯","甜品/加餐","芒果|酸奶"},{"草莓牛奶杯","甜品/加餐","草莓|牛奶"},
            {"香菇豆腐煲","家常菜","香菇|豆腐|生抽"},{"彩椒虾仁","快手菜","彩椒|虾仁|大蒜"},{"清炒芥蓝","晚餐","芥蓝|大蒜|食盐"},{"牛肉丸青菜汤","汤类","牛肉丸|上海青|香菜"},{"杏鲍菇炒鸡胸肉","减脂餐","杏鲍菇|鸡胸肉|青椒"},
            {"红豆小米粥","早餐","红豆|小米|白糖"},{"黄豆豆腐汤","汤类","黄豆|豆腐|生姜"},{"腰果鸡丁","家常菜","腰果|鸡胸肉|彩椒"},{"蛤蜊冬瓜汤","汤类","蛤蜊|冬瓜|生姜"},{"黑芝麻燕麦杯","甜品/加餐","黑芝麻|燕麦|牛奶"}
    };

    public static void seed(AppDatabase db) {
        if (db == null) {
            return;
        }
        long userId;
        UserEntity admin = db.userDao().findByUsername("admin");
        if (admin == null) {
            admin = new UserEntity("admin", "123456", "测试用户", DateUtil.getToday());
            userId = db.userDao().insert(admin);
            db.nutritionTargetDao().insert(new NutritionTargetEntity(userId, 2000, 60, 60, 250, 25, 800, 12, 100));
        } else {
            userId = admin.id;
            if (db.nutritionTargetDao().getByUser(userId) == null) {
                db.nutritionTargetDao().insert(new NutritionTargetEntity(userId, 2000, 60, 60, 250, 25, 800, 12, 100));
            }
        }
        insertDefaultIngredients(db, userId);
        insertDefaultNutrition(db);
        insertDefaultRecipes(db);
    }

    private static void insertDefaultIngredients(AppDatabase db, long userId) {
        if (db.ingredientDao().countAll() > 0) {
            return;
        }
        List<IngredientEntity> list = new ArrayList<>();
        for (int i = 0; i < INGREDIENTS.length; i++) {
            int daysOffset = (i % 10 == 0) ? -1 : (i % 7 == 0 ? 2 : 7 + (i % 16));
            String buyDate = DateUtil.addDays(DateUtil.getToday(), -2 - (i % 5));
            String expireDate = DateUtil.addDays(DateUtil.getToday(), daysOffset);
            String bestEatDate = ShelfLifeUtil.calculateBestEatDate(buyDate, INGREDIENTS[i][0], INGREDIENTS[i][1], getDefaultPlace(INGREDIENTS[i][1]));
            String unit = getDefaultUnit(INGREDIENTS[i][1]);
            String place = getDefaultPlace(INGREDIENTS[i][1]);
            list.add(new IngredientEntity(userId, INGREDIENTS[i][0], INGREDIENTS[i][1],
                    1 + (i % 5), unit, buyDate, bestEatDate, expireDate, place, "系统初始化演示数据", DateUtil.getToday()));
        }
        db.ingredientDao().insertAll(list);
    }

    private static void insertDefaultNutrition(AppDatabase db) {
        if (db.foodNutritionDao().count() > 0) {
            return;
        }
        // 本系统营养数据用于课程设计演示和日常饮食参考，不作为医学诊断依据。
        List<FoodNutritionEntity> foods = new ArrayList<>();
        for (String[] item : INGREDIENTS) {
            foods.add(createNutrition(item[0], item[1]));
        }
        db.foodNutritionDao().insertAll(foods);
    }

    private static void insertDefaultRecipes(AppDatabase db) {
        if (db.recipeDao().count() > 0) {
            return;
        }
        for (String[] recipe : RECIPES) {
            String steps = "1. 清洗并处理主要食材。2. 按食材特性进行焯水、煎炒或炖煮。3. 加入适量调味品。4. 熟透后装盘，可根据个人口味微调。";
            long recipeId = db.recipeDao().insert(new RecipeEntity(recipe[0], recipe[1],
                    "适合家庭日常制作的" + recipe[1] + "菜谱。", steps, "", DateUtil.getToday()));
            List<RecipeIngredientEntity> relations = new ArrayList<>();
            String[] names = recipe[2].split("\\|");
            for (String name : names) {
                relations.add(new RecipeIngredientEntity(recipeId, name, guessAmount(name), getUnitByName(name)));
            }
            db.recipeIngredientDao().insertAll(relations);
        }
    }

    private static FoodNutritionEntity createNutrition(String name, String category) {
        double calories = 35, protein = 2, fat = 0.4, carb = 6, fiber = 2.2, calcium = 35, iron = 0.9, vc = 20;
        if ("水果类".equals(category)) { calories = 55; protein = 0.8; fat = 0.3; carb = 13; fiber = 2.5; calcium = 18; iron = 0.4; vc = 35; }
        else if ("肉禽类".equals(category)) { calories = 180; protein = 20; fat = 10; carb = 1; fiber = 0; calcium = 12; iron = 2.2; vc = 0; }
        else if ("水产类".equals(category)) { calories = 130; protein = 19; fat = 5; carb = 0.5; fiber = 0; calcium = 40; iron = 1.1; vc = 0; }
        else if ("蛋奶类".equals(category)) { calories = 95; protein = 7; fat = 6; carb = 5; fiber = 0; calcium = 120; iron = 0.5; vc = 1; }
        else if ("豆制品".equals(category)) { calories = 100; protein = 9; fat = 5; carb = 4; fiber = 1.2; calcium = 150; iron = 2.0; vc = 0; }
        else if ("主食谷物".equals(category)) { calories = 260; protein = 8; fat = 2; carb = 55; fiber = 4; calcium = 25; iron = 1.8; vc = 0; }
        else if ("调味品".equals(category)) { calories = 80; protein = 1; fat = 1; carb = 15; fiber = 0; calcium = 15; iron = 0.4; vc = 0; }
        else if ("坚果杂粮".equals(category)) { calories = 360; protein = 16; fat = 20; carb = 28; fiber = 8; calcium = 120; iron = 3.5; vc = 0; }
        else if ("根茎类".equals(category)) { calories = 70; protein = 1.8; fat = 0.3; carb = 15; fiber = 2.8; calcium = 28; iron = 0.8; vc = 12; }
        else if ("菌菇类".equals(category)) { calories = 28; protein = 2.7; fat = 0.4; carb = 5; fiber = 2.3; calcium = 8; iron = 1.0; vc = 2; }
        else if ("瓜茄类".equals(category)) { calories = 32; protein = 1.4; fat = 0.3; carb = 6; fiber = 1.8; calcium = 22; iron = 0.6; vc = 24; }
        return new FoodNutritionEntity(name, calories, protein, fat, carb, fiber, calcium, iron, vc);
    }

    private static String getDefaultUnit(String category) {
        return FoodCatalog.getDefaultUnit(category);
    }

    private static String getDefaultPlace(String category) {
        if ("肉禽类".equals(category) || "水产类".equals(category)) return "冷冻";
        if ("调味品".equals(category) || "主食谷物".equals(category) || "坚果杂粮".equals(category)) return "常温";
        return "冰箱";
    }

    private static double guessAmount(String name) {
        if ("食盐".equals(name) || "白糖".equals(name) || "黑胡椒".equals(name) || "辣椒粉".equals(name)) return 5;
        if ("生抽".equals(name) || "老抽".equals(name) || "蚝油".equals(name) || "米醋".equals(name) || "料酒".equals(name) || "香油".equals(name)) return 10;
        return 100;
    }

    private static String getUnitByName(String name) {
        if ("鸡蛋".equals(name) || "鸭蛋".equals(name)) return "个";
        if ("牛奶".equals(name) || "酸奶".equals(name) || "生抽".equals(name) || "老抽".equals(name) || "蚝油".equals(name) || "米醋".equals(name) || "料酒".equals(name) || "香油".equals(name)) return "毫升";
        return "克";
    }
}
