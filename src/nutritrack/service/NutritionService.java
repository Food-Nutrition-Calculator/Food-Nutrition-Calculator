package nutritrack.service;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.Nutrition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service Layer for Nutritional Calculations and Macronutrient Distribution.
 *
 * Owned and maintained by: Member 3 (Nutrition Calculation)
 */
public class NutritionService {

    public Nutrition calculatePortionNutrition(FoodItem food, double quantity) {
        return Nutrition.scale(food, quantity);
    }

    public Nutrition calculateMealNutrition(Meal meal) {
        if (meal == null) return new Nutrition();
        return meal.calculateTotalNutrition();
    }

    public Nutrition calculateDailyNutrition(List<Meal> dailyMeals) {
        Nutrition dailyTotal = new Nutrition();
        if (dailyMeals == null || dailyMeals.isEmpty()) {
            return dailyTotal;
        }
        for (Meal meal : dailyMeals) {
            dailyTotal = dailyTotal.add(meal.calculateTotalNutrition());
        }
        return dailyTotal;
    }

    public Nutrition calculateDailyTotal(List<Nutrition> mealNutritions) {
        Nutrition total = new Nutrition();
        if (mealNutritions == null || mealNutritions.isEmpty()) {
            return total;
        }
        for (Nutrition n : mealNutritions) {
            total = total.add(n);
        }
        return total;
    }

    /**
     * Computes the percentage of total calories contributed by each macronutrient:
     * - Protein: 4 kcal per gram
     * - Carbohydrates: 4 kcal per gram
     * - Fats: 9 kcal per gram
     */
    public Map<String, Double> calculateMacronutrientPercentages(Nutrition nutrition) {
        Map<String, Double> percentages = new HashMap<>();
        if (nutrition == null || nutrition.getTotalCalories() <= 0) {
            percentages.put("PROTEIN_PCT", 0.0);
            percentages.put("CARBS_PCT", 0.0);
            percentages.put("FATS_PCT", 0.0);
            return percentages;
        }

        double totalCalories = nutrition.getTotalCalories();
        double proteinCalories = nutrition.getTotalProtein() * 4.0;
        double carbCalories = nutrition.getTotalCarbs() * 4.0;
        double fatCalories = nutrition.getTotalFats() * 9.0;

        double proteinPct = round((proteinCalories / totalCalories) * 100.0);
        double carbPct = round((carbCalories / totalCalories) * 100.0);
        double fatPct = round((fatCalories / totalCalories) * 100.0);

        percentages.put("PROTEIN_PCT", proteinPct);
        percentages.put("CARBS_PCT", carbPct);
        percentages.put("FATS_PCT", fatPct);

        return percentages;
    }

    private static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
