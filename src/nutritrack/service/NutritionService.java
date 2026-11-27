package nutritrack.service;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.Nutrition;

import java.util.List;

/**
 * Service Layer for Nutrition Calculations.
 * Handles macro scaling, calorie-source percentage math, daily goal tracking,
 * and nutrition aggregation across multiple meals for the Dashboard.
 *
 * Owned and maintained by: Member 3 (Nutrition Calculation)
 */
public class NutritionService {

    // FDA standard 2000 kcal diet daily reference values
    private static final double DEFAULT_CALORIE_GOAL = 2000.0;
    private static final double DEFAULT_PROTEIN_GOAL = 50.0;   // grams
    private static final double DEFAULT_CARBS_GOAL   = 275.0;  // grams
    private static final double DEFAULT_FATS_GOAL    = 78.0;   // grams

    // Energy density constants (kcal per gram)
    public static final double KCAL_PER_G_PROTEIN = 4.0;
    public static final double KCAL_PER_G_CARBS   = 4.0;
    public static final double KCAL_PER_G_FATS    = 9.0;

    private double calorieGoal;
    private double proteinGoal;
    private double carbsGoal;
    private double fatsGoal;

    // ─── Constructors ─────────────────────────────────────────────────────────

    /** Default constructor using FDA standard 2000 kcal daily targets. */
    public NutritionService() {
        this(DEFAULT_CALORIE_GOAL, DEFAULT_PROTEIN_GOAL, DEFAULT_CARBS_GOAL, DEFAULT_FATS_GOAL);
    }

    /** Constructor for custom daily nutritional targets (e.g. user profile). */
    public NutritionService(double calorieGoal, double proteinGoal, double carbsGoal, double fatsGoal) {
        this.calorieGoal = (calorieGoal > 0) ? calorieGoal : DEFAULT_CALORIE_GOAL;
        this.proteinGoal = (proteinGoal > 0) ? proteinGoal : DEFAULT_PROTEIN_GOAL;
        this.carbsGoal   = (carbsGoal   > 0) ? carbsGoal   : DEFAULT_CARBS_GOAL;
        this.fatsGoal    = (fatsGoal    > 0) ? fatsGoal    : DEFAULT_FATS_GOAL;
    }

    // ─── Core Scaling ─────────────────────────────────────────────────────────

    /**
     * Scales a FoodItem's nutritional profile to the given portion quantity.
     * Delegates to Nutrition.scale() factory method.
     *
     * @param food     The base FoodItem with nutritional profile
     * @param quantity The portion quantity entered by the user
     * @return Scaled Nutrition object
     */
    public Nutrition scaleNutrition(FoodItem food, double quantity) {
        if (food == null) {
            throw new IllegalArgumentException("FoodItem cannot be null for scaling.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        return Nutrition.scale(food, quantity);
    }

    // ─── Aggregation ──────────────────────────────────────────────────────────

    /**
     * Totals the nutrition across all meals in the given list.
     * Returns an empty (zero) Nutrition if list is null or empty.
     *
     * @param meals List of Meal objects for a given day
     * @return Aggregated daily Nutrition object
     */
    public Nutrition aggregateDailyNutrition(List<Meal> meals) {
        Nutrition total = new Nutrition();
        if (meals == null || meals.isEmpty()) return total;
        for (Meal meal : meals) {
            if (meal != null) {
                total = total.add(meal.calculateTotalNutrition());
            }
        }
        return total;
    }

    // ─── Calorie-Source Percentage Breakdown ──────────────────────────────────

    /**
     * Calculates what percentage of total consumed calories came from protein.
     * Formula: (protein_g x 4 kcal) / totalCalories x 100
     *
     * @param n Nutrition object (daily total)
     * @return Percentage value (0.0 – 100.0)
     */
    public double getProteinCaloriePercent(Nutrition n) {
        if (n == null || n.getTotalCalories() == 0) return 0.0;
        return round((n.getTotalProtein() * KCAL_PER_G_PROTEIN / n.getTotalCalories()) * 100.0);
    }

    /**
     * Calculates what percentage of total consumed calories came from carbohydrates.
     * Formula: (carbs_g x 4 kcal) / totalCalories x 100
     */
    public double getCarbsCaloriePercent(Nutrition n) {
        if (n == null || n.getTotalCalories() == 0) return 0.0;
        return round((n.getTotalCarbs() * KCAL_PER_G_CARBS / n.getTotalCalories()) * 100.0);
    }

    /**
     * Calculates what percentage of total consumed calories came from fats.
     * Formula: (fats_g x 9 kcal) / totalCalories x 100
     */
    public double getFatsCaloriePercent(Nutrition n) {
        if (n == null || n.getTotalCalories() == 0) return 0.0;
        return round((n.getTotalFats() * KCAL_PER_G_FATS / n.getTotalCalories()) * 100.0);
    }

    // ─── Daily Goal Progress ──────────────────────────────────────────────────

    /**
     * Returns how many calories remain to reach the daily calorie goal.
     * A negative value indicates the user has exceeded the goal.
     *
     * @param consumed Today's aggregated Nutrition
     * @return Remaining kcal (may be negative)
     */
    public double getRemainingCalories(Nutrition consumed) {
        if (consumed == null) return calorieGoal;
        return round(calorieGoal - consumed.getTotalCalories());
    }

    /**
     * Returns calorie intake as a percentage of the daily goal.
     * Result is clamped to a maximum of 100.0 for progress display.
     */
    public double getCalorieGoalPercent(Nutrition consumed) {
        if (consumed == null || calorieGoal == 0) return 0.0;
        return Math.min(100.0, round((consumed.getTotalCalories() / calorieGoal) * 100.0));
    }

    /**
     * Returns protein intake as a percentage of the daily protein goal (clamped to 100.0).
     */
    public double getProteinGoalPercent(Nutrition consumed) {
        if (consumed == null || proteinGoal == 0) return 0.0;
        return Math.min(100.0, round((consumed.getTotalProtein() / proteinGoal) * 100.0));
    }

    /**
     * Returns carbohydrate intake as a percentage of the daily carbs goal (clamped to 100.0).
     */
    public double getCarbsGoalPercent(Nutrition consumed) {
        if (consumed == null || carbsGoal == 0) return 0.0;
        return Math.min(100.0, round((consumed.getTotalCarbs() / carbsGoal) * 100.0));
    }

    /**
     * Returns fat intake as a percentage of the daily fats goal (clamped to 100.0).
     */
    public double getFatsGoalPercent(Nutrition consumed) {
        if (consumed == null || fatsGoal == 0) return 0.0;
        return Math.min(100.0, round((consumed.getTotalFats() / fatsGoal) * 100.0));
    }

    /**
     * Returns a one-line formatted summary of consumed nutrition vs. all daily goals.
     * Useful for status bars or console output.
     *
     * @param consumed Today's aggregated Nutrition (or null for zero state)
     * @return Human-readable summary string
     */
    public String getDailySummaryText(Nutrition consumed) {
        if (consumed == null) consumed = new Nutrition();
        return String.format(
            "Calories: %.1f / %.0f kcal  |  Protein: %.1f / %.0fg  |  Carbs: %.1f / %.0fg  |  Fats: %.1f / %.0fg",
            consumed.getTotalCalories(), calorieGoal,
            consumed.getTotalProtein(),  proteinGoal,
            consumed.getTotalCarbs(),    carbsGoal,
            consumed.getTotalFats(),     fatsGoal
        );
    }

    // ─── Getters & Setters for Daily Goals ───────────────────────────────────

    public double getCalorieGoal() { return calorieGoal; }
    public void setCalorieGoal(double v) {
        if (v <= 0) throw new IllegalArgumentException("Calorie goal must be positive.");
        this.calorieGoal = v;
    }

    public double getProteinGoal() { return proteinGoal; }
    public void setProteinGoal(double v) {
        if (v <= 0) throw new IllegalArgumentException("Protein goal must be positive.");
        this.proteinGoal = v;
    }

    public double getCarbsGoal() { return carbsGoal; }
    public void setCarbsGoal(double v) {
        if (v <= 0) throw new IllegalArgumentException("Carbs goal must be positive.");
        this.carbsGoal = v;
    }

    public double getFatsGoal() { return fatsGoal; }
    public void setFatsGoal(double v) {
        if (v <= 0) throw new IllegalArgumentException("Fats goal must be positive.");
        this.fatsGoal = v;
    }

    // ─── Internal Helpers ─────────────────────────────────────────────────────

    private static double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
