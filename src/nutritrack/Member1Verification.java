package nutritrack;

import nutritrack.model.FoodItem;
import nutritrack.service.FoodService;

import java.util.ArrayList;
import java.util.List;

/**
 * Verification test suite for Member 1 (Food Management).
 * Tests business validation rules, searching algorithms,
 * and multi-attribute sorting logic.
 */
public class Member1Verification {

    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("  MEMBER 1 (FOOD MANAGEMENT): UNIT & SERVICE TESTS    ");
        System.out.println("======================================================");

        FoodService service = new FoodService(null); // tests in-memory sorting logic without live DB
        int passed = 0;

        // Sample test catalog
        List<FoodItem> items = new ArrayList<>();
        items.add(new FoodItem(1, "Chicken Breast", "grams", 100.0, 165.0, 31.0, 0.0, 3.6));
        items.add(new FoodItem(2, "White Rice", "grams", 100.0, 130.0, 2.7, 28.2, 0.3));
        items.add(new FoodItem(3, "Rolled Oats", "grams", 100.0, 389.0, 16.9, 66.3, 6.9));
        items.add(new FoodItem(4, "Olive Oil", "ml", 15.0, 119.0, 0.0, 0.0, 13.5));

        // Test 1: Sorting by Calories Descending
        System.out.print("[TEST 1] Sorting by Calories (Descending)... ");
        List<FoodItem> sortedCalDesc = service.getSortedFoods(items, "CALORIES", false);
        if (sortedCalDesc.get(0).getFoodName().equals("Rolled Oats") && // 389 kcal
            sortedCalDesc.get(sortedCalDesc.size() - 1).getFoodName().equals("Olive Oil")) { // 119 kcal
            System.out.println("PASSED (Top: " + sortedCalDesc.get(0).getFoodName() + " - " + sortedCalDesc.get(0).getCalories() + " kcal)");
            passed++;
        } else {
            System.out.println("FAILED");
        }

        // Test 2: Sorting by Protein Descending
        System.out.print("[TEST 2] Sorting by Protein (Descending)... ");
        List<FoodItem> sortedProtein = service.getSortedFoods(items, "PROTEIN", false);
        if (sortedProtein.get(0).getFoodName().equals("Chicken Breast") && sortedProtein.get(0).getProtein() == 31.0) {
            System.out.println("PASSED (Top: Chicken Breast with 31.0g protein)");
            passed++;
        } else {
            System.out.println("FAILED");
        }

        // Test 3: Sorting by Name Ascending (Alphabetical)
        System.out.print("[TEST 3] Sorting by Name (Alphabetical)... ");
        List<FoodItem> sortedName = service.getSortedFoods(items, "NAME", true);
        if (sortedName.get(0).getFoodName().equals("Chicken Breast") &&
            sortedName.get(sortedName.size() - 1).getFoodName().equals("White Rice")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
        }

        // Test 4: Validation Rule - Blank Name Rejection
        System.out.print("[TEST 4] Validating empty food name rejection... ");
        try {
            service.addFood(new FoodItem("", "grams", 100.0, 100.0, 5.0, 5.0, 2.0));
            System.out.println("FAILED (Did not reject empty name)");
        } catch (IllegalArgumentException e) {
            System.out.println("PASSED (Caught expected: " + e.getMessage() + ")");
            passed++;
        }

        // Test 5: Validation Rule - Negative Calories Rejection
        System.out.print("[TEST 5] Validating negative calories rejection... ");
        try {
            service.addFood(new FoodItem("Bad Food", "grams", 100.0, -50.0, 5.0, 5.0, 2.0));
            System.out.println("FAILED (Did not reject negative calories)");
        } catch (IllegalArgumentException e) {
            System.out.println("PASSED (Caught expected: " + e.getMessage() + ")");
            passed++;
        }

        System.out.println("======================================================");
        System.out.println("  MEMBER 1 SUMMARY: " + passed + "/5 TESTS PASSED SUCCESSFULLY! ");
        System.out.println("======================================================");
    }
}
