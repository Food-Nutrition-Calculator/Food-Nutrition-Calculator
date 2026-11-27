package nutritrack;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;
import nutritrack.service.NutritionService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Automated Verification Unit Test Suite for Member 3.
 */
public class Member3Verification {

    private static int testsPassed = 0;
    private static int totalTests = 5;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Member 3 (Nutrition Calculation) Verification  ");
        System.out.println("=================================================");

        NutritionService service = new NutritionService();

        // Test 1: Portion Scaling with FoodItem
        FoodItem oats = new FoodItem(1, "Oatmeal", "g", 100.0, 389.0, 16.9, 66.3, 6.9);
        Nutrition scaled = service.calculatePortionNutrition(oats, 50.0);
        reportTest(1, "Portion Scaling Math (50g from 100g baseline)", Math.abs(scaled.getTotalCalories() - 194.5) < 0.2);

        // Test 2: Meal Nutrition Aggregation
        FoodItem egg = new FoodItem(2, "Egg", "unit", 1.0, 78.0, 6.3, 0.6, 5.3);
        Meal breakfast = new Meal(1, "Breakfast", LocalDate.now(), LocalTime.of(8, 30));
        breakfast.addMealItem(new MealItem(1, egg, 2.0));
        Nutrition mealNutrition = service.calculateMealNutrition(breakfast);
        reportTest(2, "Meal Nutrition Aggregation across Items", Math.abs(mealNutrition.getTotalCalories() - 156.0) < 0.2);

        // Test 3: Daily Total calculation
        Nutrition daily = service.calculateDailyNutrition(List.of(breakfast));
        reportTest(3, "Daily Total Aggregation across Meals", Math.abs(daily.getTotalCalories() - 156.0) < 0.2);

        // Test 4: Macro Percentages (4-4-9 Rule)
        Nutrition balanced = new Nutrition(1000.0, 100.0, 100.0, 22.22);
        Map<String, Double> pcts = service.calculateMacronutrientPercentages(balanced);
        reportTest(4, "Macronutrient 4-4-9 Percentage Distribution", Math.abs(pcts.get("PROTEIN_PCT") - 40.0) <= 0.5);

        // Test 5: Zero Division Edge Case
        Nutrition zero = new Nutrition(0.0, 0.0, 0.0, 0.0);
        Map<String, Double> zeroPcts = service.calculateMacronutrientPercentages(zero);
        reportTest(5, "Zero Calories Edge Case Handling", zeroPcts.get("PROTEIN_PCT") == 0.0);

        System.out.println("-------------------------------------------------");
        System.out.println("Summary: " + testsPassed + "/" + totalTests + " tests passed.");
        if (testsPassed == totalTests) {
            System.out.println("🎉 All Member 3 Verification Tests PASSED Successfully!");
        }
        System.out.println("=================================================");
    }

    private static void reportTest(int num, String name, boolean passed) {
        if (passed) {
            System.out.println("  [PASS] Test " + num + ": " + name);
            testsPassed++;
        } else {
            System.out.println("  [FAIL] Test " + num + ": " + name);
        }
    }
}
