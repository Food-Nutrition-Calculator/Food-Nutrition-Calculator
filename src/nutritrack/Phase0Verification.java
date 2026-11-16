package nutritrack;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;
import nutritrack.utils.DateUtils;
import nutritrack.utils.ValidationUtils;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Phase 0 Integration Verification Runner.
 * Runs automated sanity checks across all Phase 0 models and utilities
 * to confirm that work from all 4 members compiles and executes consistently.
 */
public class Phase0Verification {

    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("  NUTRI-TRACK PHASE 0: FOUNDATION VERIFICATION TEST   ");
        System.out.println("======================================================");

        int testsPassed = 0;
        int totalTests = 5;

        // --- TEST 1: FoodItem Instantiation & Validation (Member 1) ---
        System.out.print("[TEST 1] Verifying FoodItem model (Member 1)... ");
        FoodItem oats = new FoodItem(1, "Rolled Oats", "grams", 100.0, 389.0, 16.9, 66.3, 6.9);
        FoodItem milk = new FoodItem(2, "Whole Milk", "ml", 100.0, 61.0, 3.2, 4.8, 3.3);
        FoodItem banana = new FoodItem(3, "Banana", "grams", 100.0, 89.0, 1.1, 22.8, 0.3);
        if (oats.getCalories() == 389.0 && oats.getFoodName().equals("Rolled Oats")) {
            System.out.println("PASSED");
            testsPassed++;
        } else {
            System.out.println("FAILED");
        }

        // --- TEST 2: Portion Scaling & Nutrition Factory (Member 3) ---
        System.out.print("[TEST 2] Verifying Nutrition scaling math (Member 3)... ");
        // 50g of oats = 0.5x serving size (100g)
        Nutrition halfOatsNutrition = Nutrition.scale(oats, 50.0);
        // Expected calories: 389.0 * 0.5 = 194.5
        // Expected protein: 16.9 * 0.5 = 8.45
        boolean scalingAccurate = Math.abs(halfOatsNutrition.getTotalCalories() - 194.5) < 0.01 &&
                                  Math.abs(halfOatsNutrition.getTotalProtein() - 8.45) < 0.01;
        if (scalingAccurate) {
            System.out.println("PASSED (" + halfOatsNutrition + ")");
            testsPassed++;
        } else {
            System.out.println("FAILED: Got " + halfOatsNutrition);
        }

        // --- TEST 3: MealItem & Meal Aggregation (Member 2) ---
        System.out.print("[TEST 3] Verifying MealItem & Meal composition (Member 2)... ");
        Meal breakfast = new Meal("Power Oatmeal", LocalDate.now(), LocalTime.of(8, 30));
        breakfast.addMealItem(new MealItem(oats, 50.0));   // 50g oats: 194.5 kcal, 8.45g P, 33.15g C, 3.45g F
        breakfast.addMealItem(new MealItem(milk, 200.0));  // 200ml milk: 122.0 kcal, 6.4g P, 9.6g C, 6.6g F
        breakfast.addMealItem(new MealItem(banana, 100.0));// 100g banana: 89.0 kcal, 1.1g P, 22.8g C, 0.3g F

        Nutrition totalBreakfastNutrition = breakfast.calculateTotalNutrition();
        // Total Calories expected: 194.5 + 122.0 + 89.0 = 405.5
        // Total Protein expected: 8.45 + 6.4 + 1.1 = 15.95
        boolean mealAccurate = Math.abs(totalBreakfastNutrition.getTotalCalories() - 405.5) < 0.1 &&
                               breakfast.getMealItems().size() == 3;
        if (mealAccurate) {
            System.out.println("PASSED (" + totalBreakfastNutrition + ")");
            testsPassed++;
        } else {
            System.out.println("FAILED: Got " + totalBreakfastNutrition);
        }

        // --- TEST 4: Date & String Utilities (Member 4 / Shared) ---
        System.out.print("[TEST 4] Verifying DateUtils and ValidationUtils... ");
        LocalDate sampleDate = LocalDate.of(2026, 11, 16);
        String formattedDate = DateUtils.formatDisplayDate(sampleDate);
        boolean dateValid = formattedDate.equals("16 Nov 2026");
        boolean validationValid = ValidationUtils.parsePositiveDouble("150.5", "Serving") == 150.5;
        if (dateValid && validationValid) {
            System.out.println("PASSED (Formatted: " + formattedDate + ")");
            testsPassed++;
        } else {
            System.out.println("FAILED");
        }

        // --- TEST 5: Duplicate Item Handling in Meal (Member 2) ---
        System.out.print("[TEST 5] Verifying duplicate item aggregation in Meal... ");
        // Adding another 50g of oats should aggregate the quantity to 100g without duplicating list entry
        breakfast.addMealItem(new MealItem(oats, 50.0));
        boolean aggregationPassed = breakfast.getMealItems().size() == 3 &&
                                    breakfast.getMealItems().get(0).getQuantity() == 100.0;
        if (aggregationPassed) {
            System.out.println("PASSED (Oats quantity aggregated to 100.0g)");
            testsPassed++;
        } else {
            System.out.println("FAILED");
        }

        System.out.println("======================================================");
        System.out.println("  SUMMARY: " + testsPassed + "/" + totalTests + " TESTS PASSED SUCCESSFULLY!  ");
        System.out.println("======================================================");
    }
}
