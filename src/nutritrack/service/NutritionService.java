
package nutritrack.service;

import nutritrack.model.Meal;
import nutritrack.model.Nutrition;

import java.util.List;

/**
 * Service layer for Nutrition Calculation.
 * Calculates meal-wise and daily nutrition totals.
 *
 * Owned and maintained by: Member 3 (Nutrition Calculation)
 */
public class NutritionService {

    /**
     * Calculates total nutrition of a single meal.
     */
    public Nutrition calculateMealNutrition(Meal meal) {
        if (meal == null) {
            throw new IllegalArgumentException("Meal cannot be null.");
        }

        return meal.calculateTotalNutrition();
    }

    /**
     * Calculates combined nutrition of all meals for a day.
     */
    public Nutrition calculateDailyNutrition(List<Meal> meals) {
        if (meals == null) {
            throw new IllegalArgumentException("Meal list cannot be null.");
        }

        Nutrition total = new Nutrition();

        for (Meal meal : meals) {
            if (meal != null) {
                total = total.add(calculateMealNutrition(meal));
            }
        }

        return total;
    }

    /**
     * Checks whether the daily calorie intake exceeds a given limit.
     */
    public boolean exceedsCalorieLimit(List<Meal> meals, double calorieLimit) {
        if (calorieLimit < 0) {
            throw new IllegalArgumentException("Calorie limit cannot be negative.");
        }

        Nutrition dailyNutrition = calculateDailyNutrition(meals);
        return dailyNutrition.getTotalCalories() > calorieLimit;
    }
}
