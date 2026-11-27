package nutritrack;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;
import nutritrack.service.NutritionService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Verification test suite for Member 3 (Nutrition Calculation).
 * Tests macro scaling via service, daily aggregation across multiple meals,
 * calorie-source percentage breakdown, daily goal tracking, and edge cases.
 *
 * Run independently of any database — all in-memory.
 */
public class Member3Verification {

    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("  MEMBER 3 (NUTRITION ENGINE): UNIT & SERVICE TESTS   ");
        System.out.println("======================================================");

        NutritionService service = new NutritionService(2000.0, 50.0, 275.0, 78.0);
        int passed = 0;

        // ── Sample food items ──
        FoodItem oats    = new FoodItem(1, "Rolled Oats",    "grams", 100.0, 389.0, 16.9, 66.3, 6.9);
        FoodItem chicken = new FoodItem(2, "Chicken Breast", "grams", 100.0, 165.0, 31.0,  0.0, 3.6);
        FoodItem rice    = new FoodItem(3, "White Rice",     "grams", 100.0, 130.0,  2.7, 28.2, 0.3);

        // ── TEST 1: Portion scaling via NutritionService ──────────────────────
        System.out.print("[TEST 1] Nutrition scaling via service (50g oats)... ");
        Nutrition scaled = service.scaleNutrition(oats, 50.0);
        // Expected: 389 * 0.5 = 194.5 kcal, protein 16.9 * 0.5 = 8.45g
        boolean t1 = Math.abs(scaled.getTotalCalories() - 194.5) < 0.01 &&
                     Math.abs(scaled.getTotalProtein()  -   8.45) < 0.01;
        if (t1) {
            System.out.println("PASSED (" + scaled + ")");
            passed++;
        } else {
            System.out.println("FAILED: Got " + scaled);
        }

        // ── TEST 2: Daily nutrition aggregation across 2 meals ────────────────
        System.out.print("[TEST 2] Daily nutrition aggregation across 2 meals... ");
        Meal breakfast = new Meal("Breakfast", LocalDate.now(), LocalTime.of(8, 0));
        breakfast.addMealItem(new MealItem(oats, 100.0));     // 389 kcal

        Meal lunch = new Meal("Lunch", LocalDate.now(), LocalTime.of(13, 0));
        lunch.addMealItem(new MealItem(chicken, 100.0));       // 165 kcal
        lunch.addMealItem(new MealItem(rice,    100.0));       // 130 kcal

        List<Meal> meals = new ArrayList<>();
        meals.add(breakfast);
        meals.add(lunch);

        Nutrition daily = service.aggregateDailyNutrition(meals);
        // Expected total: 389 + 165 + 130 = 684 kcal
        boolean t2 = Math.abs(daily.getTotalCalories() - 684.0) < 0.1;
        if (t2) {
            System.out.println("PASSED (Total: " + daily.getTotalCalories() + " kcal, " +
                "Protein: " + daily.getTotalProtein() + "g, Carbs: " + daily.getTotalCarbs() + "g)");
            passed++;
        } else {
            System.out.println("FAILED: Got " + daily.getTotalCalories() + " kcal");
        }

        // ── TEST 3: Macro calorie percentage breakdown (Chicken Breast 100g) ──
        System.out.print("[TEST 3] Macro calorie percentage breakdown... ");
        // Chicken 100g: 165 kcal, 31g protein, 0g carbs, 3.6g fats
        // Protein%  = (31 * 4)   / 165 * 100 = 75.15%
        // Carbs%    = (0  * 4)   / 165 * 100 = 0.0%
        // Fats%     = (3.6 * 9)  / 165 * 100 = 19.64%
        Nutrition chickenNutr = service.scaleNutrition(chicken, 100.0);
        double protPct = service.getProteinCaloriePercent(chickenNutr);
        double carbPct = service.getCarbsCaloriePercent(chickenNutr);
        double fatPct  = service.getFatsCaloriePercent(chickenNutr);
        boolean t3 = protPct > 70.0 && carbPct == 0.0 && fatPct > 10.0;
        if (t3) {
            System.out.println("PASSED (Protein: " + protPct + "%, Carbs: " + carbPct
                + "%, Fats: " + fatPct + "%)");
            passed++;
        } else {
            System.out.println("FAILED: Protein=" + protPct + "%, Carbs=" + carbPct
                + "%, Fats=" + fatPct + "%");
        }

        // ── TEST 4: Daily goal tracking – remaining calories & progress % ─────
        System.out.print("[TEST 4] Daily goal tracking (goal=2000 kcal, consumed=684)... ");
        // Remaining = 2000 - 684 = 1316 kcal
        // Progress%  = 684/2000 * 100 = 34.2%
        double remaining = service.getRemainingCalories(daily);
        double calPct    = service.getCalorieGoalPercent(daily);
        boolean t4 = Math.abs(remaining - 1316.0) < 0.1 &&
                     Math.abs(calPct - 34.2) < 0.5;
        if (t4) {
            System.out.println("PASSED (Remaining: " + remaining + " kcal, Progress: " + calPct + "%)");
            passed++;
        } else {
            System.out.println("FAILED: Remaining=" + remaining + ", Progress=" + calPct + "%");
        }

        // ── TEST 5: Edge cases – empty meal list and null nutrition ───────────
        System.out.print("[TEST 5] Edge cases: empty list and null handling... ");
        Nutrition emptyAgg   = service.aggregateDailyNutrition(new ArrayList<>());
        double nullRemaining = service.getRemainingCalories(null);
        double zeroProtPct   = service.getProteinCaloriePercent(new Nutrition());
        boolean t5 = emptyAgg.getTotalCalories() == 0.0 &&
                     Math.abs(nullRemaining - 2000.0) < 0.01 &&
                     zeroProtPct == 0.0;
        if (t5) {
            System.out.println("PASSED (Empty=0 kcal, NullRemaining=" + nullRemaining
                + " kcal, ZeroPct=" + zeroProtPct + "%)");
            passed++;
        } else {
            System.out.println("FAILED");
        }

        System.out.println("======================================================");
        System.out.println("  MEMBER 3 SUMMARY: " + passed + "/5 TESTS PASSED SUCCESSFULLY! ");
        System.out.println("======================================================");
        System.out.println();
        System.out.println("  Summary: " + service.getDailySummaryText(daily));
        System.out.println("======================================================");
    }
}
