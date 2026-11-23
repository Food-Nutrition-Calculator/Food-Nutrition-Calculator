
import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;
import nutritrack.service.NutritionService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Member3Verification {

    public static void main(String[] args) {

        NutritionService service = new NutritionService();

        // Sample food items
        FoodItem rice = new FoodItem(1, "Rice", "g", 100, 130, 2.7, 28, 0.3);
        FoodItem egg = new FoodItem(2, "Egg", "piece", 1, 78, 6, 0.6, 5);

        // Create a meal
        Meal lunch = new Meal(1, "Lunch", LocalDate.now(), LocalTime.now());

        // Add food to meal
        lunch.addMealItem(new MealItem(1, rice, 150));
        lunch.addMealItem(new MealItem(2, egg, 2));

        // Calculate meal nutrition
        Nutrition mealNutrition = service.calculateMealNutrition(lunch);

        System.out.println("===== MEAL NUTRITION =====");
        System.out.println("Calories: " + mealNutrition.getTotalCalories());
        System.out.println("Protein: " + mealNutrition.getTotalProtein());
        System.out.println("Carbs: " + mealNutrition.getTotalCarbs());
        System.out.println("Fats: " + mealNutrition.getTotalFats());

        // Daily calculation
        List<Meal> meals = new ArrayList<>();
        meals.add(lunch);

        Nutrition daily = service.calculateDailyNutrition(meals);

        System.out.println("\n===== DAILY NUTRITION =====");
        System.out.println("Calories: " + daily.getTotalCalories());
        System.out.println("Protein: " + daily.getTotalProtein());
        System.out.println("Carbs: " + daily.getTotalCarbs());
        System.out.println("Fats: " + daily.getTotalFats());

        // Calorie limit test
        System.out.println("\n===== CALORIE LIMIT =====");
        System.out.println("Exceeds 300 calories: "
                + service.exceedsCalorieLimit(meals, 300));

        System.out.println("\nMember 3 Verification Completed!");
    }
}
