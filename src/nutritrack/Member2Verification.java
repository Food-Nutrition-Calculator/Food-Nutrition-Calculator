package nutritrack;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;
import nutritrack.service.MealService;

/**
 * Verification test suite for Member 2 (Meal Management).
 * Tests composite meal creation, portion scaling math,
 * duplicate item aggregation, and item removal logic.
 */
public class Member2Verification {

    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("  MEMBER 2 (MEAL MANAGEMENT): UNIT & SERVICE TESTS    ");
        System.out.println("======================================================");

        MealService service = new MealService(null, null); // in-memory testing
        int passed = 0;

        FoodItem oats = new FoodItem(1, "Rolled Oats", "grams", 100.0, 389.0, 16.9, 66.3, 6.9);
        FoodItem milk = new FoodItem(2, "Whole Milk", "ml", 100.0, 61.0, 3.2, 4.8, 3.3);
        FoodItem egg = new FoodItem(4, "Boiled Egg", "grams", 50.0, 77.0, 6.3, 0.6, 5.3);

        // Test 1: Meal Creation & Basic Addition
        System.out.print("[TEST 1] Initiating meal & adding food item... ");
        Meal breakfast = service.createNewMeal("Power Breakfast");
        service.addFoodToMeal(breakfast, oats, 100.0);
        if (breakfast.getMealItems().size() == 1 &&
            breakfast.getMealItems().get(0).getCalculatedCalories() == 389.0) {
            System.out.println("PASSED (100g Oats = 389.0 kcal)");
            passed++;
        } else {
            System.out.println("FAILED");
        }

        // Test 2: Portion Scaling Multiplier
        System.out.print("[TEST 2] Testing portion scaling (2x serving)... ");
        // Egg default serving is 50g (77 kcal). 100g of eggs = 2x serving = 154 kcal, 12.6g protein
        service.addFoodToMeal(breakfast, egg, 100.0);
        MealItem eggItem = breakfast.getMealItems().get(1);
        boolean eggScaled = Math.abs(eggItem.getCalculatedCalories() - 154.0) < 0.1 &&
                            Math.abs(eggItem.getCalculatedProtein() - 12.6) < 0.1;
        if (eggScaled) {
            System.out.println("PASSED (100g Eggs = " + eggItem.getCalculatedCalories() + " kcal, " + eggItem.getCalculatedProtein() + "g protein)");
            passed++;
        } else {
            System.out.println("FAILED: Got " + eggItem.getCalculatedCalories());
        }

        // Test 3: Duplicate Item Automatic Aggregation
        System.out.print("[TEST 3] Duplicate item portion aggregation... ");
        // Adding another 50g of oats should combine with existing 100g oats to make 150g oats
        service.addFoodToMeal(breakfast, oats, 50.0);
        MealItem combinedOats = breakfast.getMealItems().get(0);
        boolean combined = breakfast.getMealItems().size() == 2 && combinedOats.getQuantity() == 150.0;
        if (combined) {
            System.out.println("PASSED (Oats combined to " + combinedOats.getQuantity() + "g in single row)");
            passed++;
        } else {
            System.out.println("FAILED: Items count = " + breakfast.getMealItems().size());
        }

        // Test 4: Total Aggregated Meal Nutrition
        System.out.print("[TEST 4] Total meal nutrition calculation... ");
        // Oats 150g = 389 * 1.5 = 583.5 kcal
        // Eggs 100g = 77 * 2 = 154.0 kcal
        // Total expected: 737.5 kcal
        Nutrition totalNutr = breakfast.calculateTotalNutrition();
        boolean totalMatches = Math.abs(totalNutr.getTotalCalories() - 737.5) < 0.1;
        if (totalMatches) {
            System.out.println("PASSED (Total: " + totalNutr.getTotalCalories() + " kcal, Protein: " + totalNutr.getTotalProtein() + "g)");
            passed++;
        } else {
            System.out.println("FAILED: Got " + totalNutr.getTotalCalories());
        }

        // Test 5: Food Item Removal from Meal
        System.out.print("[TEST 5] Removing food item from meal... ");
        service.removeFoodFromMeal(breakfast, 4); // Remove Eggs (foodId = 4)
        boolean removed = breakfast.getMealItems().size() == 1 &&
                          breakfast.getMealItems().get(0).getFoodItem().getFoodId() == 1;
        if (removed) {
            System.out.println("PASSED (Eggs removed, remaining items: " + breakfast.getMealItems().size() + ")");
            passed++;
        } else {
            System.out.println("FAILED");
        }

        System.out.println("======================================================");
        System.out.println("  MEMBER 2 SUMMARY: " + passed + "/5 TESTS PASSED SUCCESSFULLY! ");
        System.out.println("======================================================");
    }
}
