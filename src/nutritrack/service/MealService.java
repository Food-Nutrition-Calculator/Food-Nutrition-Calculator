package nutritrack.service;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.utils.ValidationUtils;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Service Layer for Meal Management.
 * Handles meal creation, portion quantity scaling, adding food items,
 * and aggregating duplicates.
 *
 * Owned and maintained by: Member 2 (Meal Management)
 */
public class MealService {

    public Meal createNewMeal(String mealName) {
        if (!ValidationUtils.isNotEmpty(mealName)) {
            throw new IllegalArgumentException("Meal name cannot be empty.");
        }
        return new Meal(mealName.trim(), LocalDate.now(), LocalTime.now().withNano(0));
    }

    public boolean addFoodToMeal(Meal meal, FoodItem food, double quantity) {
        if (meal == null) {
            throw new IllegalArgumentException("Target meal cannot be null.");
        }
        if (food == null) {
            throw new IllegalArgumentException("Food item cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Portion quantity must be greater than zero.");
        }

        meal.addMealItem(new MealItem(food, quantity));
        return true;
    }

    public boolean removeFoodFromMeal(Meal meal, int foodId) {
        if (meal == null) {
            throw new IllegalArgumentException("Target meal cannot be null.");
        }
        return meal.removeMealItem(foodId);
    }
}