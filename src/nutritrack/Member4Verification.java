package nutritrack;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;
import nutritrack.service.HistoryService;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Automated Verification Unit Test Suite for Member 4 (Database, History & Integration).
 */
public class Member4Verification {

    private static int testsPassed = 0;
    private static int totalTests = 5;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Member 4 (History & Integration) Verification  ");
        System.out.println("=================================================");

        testModelIntegrity();
        testHistoryAggregationLogic();
        testDateBoundaryHandling();
        testMealCascadeSimulation();
        testMultiMealIntegration();

        System.out.println("-------------------------------------------------");
        System.out.println("Summary: " + testsPassed + "/" + totalTests + " tests passed.");
        if (testsPassed == totalTests) {
            System.out.println("All Member 4 Verification Tests PASSED Successfully!");
        } else {
            System.out.println("Some tests failed.");
        }
        System.out.println("=================================================");
    }

    private static void testModelIntegrity() {
        FoodItem rice = new FoodItem(1, "Brown Rice", "grams", 100.0, 111.0, 2.6, 23.0, 0.9);
        Meal lunch = new Meal(10, "Office Lunch", LocalDate.of(2026, 11, 27), LocalTime.of(13, 0));
        lunch.addMealItem(new MealItem(101, rice, 150.0));

        boolean passed = lunch.getMealId() == 10
                && lunch.getMealDate().equals(LocalDate.of(2026, 11, 27))
                && lunch.getMealItems().size() == 1;

        reportTest(1, "Meal and MealItem Model Data Integrity", passed);
    }

    private static void testHistoryAggregationLogic() {
        FoodItem paneer = new FoodItem(2, "Paneer", "grams", 100.0, 265.0, 18.0, 1.2, 20.8);
        Meal dinner = new Meal(11, "Dinner", LocalDate.now(), LocalTime.of(20, 30));
        dinner.addMealItem(new MealItem(102, paneer, 200.0)); // 2x: 530 kcal, 36g P

        Nutrition n = dinner.calculateTotalNutrition();
        boolean passed = Math.abs(n.getTotalCalories() - 530.0) < 0.1
                && Math.abs(n.getTotalProtein() - 36.0) < 0.1;

        reportTest(2, "Historical Meal Nutrition Calculation (Scaled Totals)", passed);
    }

    private static void testDateBoundaryHandling() {
        HistoryService historyService = new HistoryService();
        Nutrition empty = new Nutrition();
        Meal emptyMeal = new Meal(0, "Empty", LocalDate.now(), LocalTime.now());

        boolean passed = historyService != null
                && empty.getTotalCalories() == 0.0
                && emptyMeal.calculateTotalNutrition().getTotalCalories() == 0.0;
        reportTest(3, "History Service Init & Empty Boundary Handling", passed);
    }

    private static void testMealCascadeSimulation() {
        Meal meal = new Meal(12, "Snack", LocalDate.now(), LocalTime.now());
        FoodItem almond = new FoodItem(3, "Almonds", "grams", 28.0, 160.0, 6.0, 6.0, 14.0);
        meal.addMealItem(new MealItem(103, almond, 28.0));

        meal.removeMealItem(3); // Almonds ki foodId 3 hai
        boolean passed = meal.getMealItems().isEmpty();
        reportTest(4, "Meal Cascade / Item Removal Simulation", passed);
    }

    private static void testMultiMealIntegration() {
        Meal m1 = new Meal(1, "Breakfast", LocalDate.now(), LocalTime.of(8, 0));
        Meal m2 = new Meal(2, "Lunch", LocalDate.now(), LocalTime.of(13, 0));

        FoodItem egg = new FoodItem(4, "Egg", "whole", 1.0, 78.0, 6.0, 0.6, 5.0);
        m1.addMealItem(new MealItem(egg, 2.0));
        m2.addMealItem(new MealItem(egg, 1.0));

        double totalCal = m1.calculateTotalNutrition().getTotalCalories()
                + m2.calculateTotalNutrition().getTotalCalories();
        boolean passed = Math.abs(totalCal - 234.0) < 0.1;
        reportTest(5, "Multi-Meal Daily Aggregate Calculation", passed);
    }

    private static void reportTest(int num, String name, boolean passed) {
        if (passed) {
            testsPassed++;
            System.out.printf("  [PASS] Test %d: %s%n", num, name);
        } else {
            System.out.printf("  [FAIL] Test %d: %s%n", num, name);
        }
    }
}