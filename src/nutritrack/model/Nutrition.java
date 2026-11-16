package nutritrack.model;

import java.util.Objects;

/**
 * Encapsulates nutritional totals (calories, protein, carbohydrates, fats).
 * Used for portion-scaled items, composite meal totals, and daily intake summaries.
 *
 * Owned and maintained by: Member 3 (Nutrition Calculation)
 */
public class Nutrition {
    private double totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFats;

    /**
     * Default constructor initializing all macronutrients to 0.0.
     */
    public Nutrition() {
        this(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Parameterized constructor.
     */
    public Nutrition(double totalCalories, double totalProtein, double totalCarbs, double totalFats) {
        setTotalCalories(totalCalories);
        setTotalProtein(totalProtein);
        setTotalCarbs(totalCarbs);
        setTotalFats(totalFats);
    }

    /**
     * Factory method: Computes scaled nutrition for a given FoodItem based on entered quantity.
     * Formula: Scaled = Base * (Quantity / DefaultServingSize)
     *
     * @param food The base FoodItem
     * @param quantity The portion quantity entered by the user
     * @return A new Nutrition instance with calculated values
     */
    public static Nutrition scale(FoodItem food, double quantity) {
        if (food == null) {
            throw new IllegalArgumentException("FoodItem cannot be null for nutrition scaling.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (food.getDefaultServingSize() <= 0) {
            throw new IllegalArgumentException("Default serving size of food must be positive.");
        }

        double ratio = quantity / food.getDefaultServingSize();
        double scaledCalories = round(food.getCalories() * ratio);
        double scaledProtein = round(food.getProtein() * ratio);
        double scaledCarbs = round(food.getCarbohydrates() * ratio);
        double scaledFats = round(food.getFats() * ratio);

        return new Nutrition(scaledCalories, scaledProtein, scaledCarbs, scaledFats);
    }

    /**
     * Adds another Nutrition object to this one and returns a new combined Nutrition object.
     *
     * @param other The other Nutrition object to sum
     * @return A new aggregated Nutrition instance
     */
    public Nutrition add(Nutrition other) {
        if (other == null) {
            return new Nutrition(this.totalCalories, this.totalProtein, this.totalCarbs, this.totalFats);
        }
        return new Nutrition(
            round(this.totalCalories + other.totalCalories),
            round(this.totalProtein + other.totalProtein),
            round(this.totalCarbs + other.totalCarbs),
            round(this.totalFats + other.totalFats)
        );
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // --- Getters and Setters ---

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        if (totalCalories < 0) {
            throw new IllegalArgumentException("Calories cannot be negative.");
        }
        this.totalCalories = round(totalCalories);
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        if (totalProtein < 0) {
            throw new IllegalArgumentException("Protein cannot be negative.");
        }
        this.totalProtein = round(totalProtein);
    }

    public double getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(double totalCarbs) {
        if (totalCarbs < 0) {
            throw new IllegalArgumentException("Carbohydrates cannot be negative.");
        }
        this.totalCarbs = round(totalCarbs);
    }

    public double getTotalFats() {
        return totalFats;
    }

    public void setTotalFats(double totalFats) {
        if (totalFats < 0) {
            throw new IllegalArgumentException("Fats cannot be negative.");
        }
        this.totalFats = round(totalFats);
    }

    @Override
    public String toString() {
        return String.format("Calories: %.1f kcal | Protein: %.1fg | Carbs: %.1fg | Fats: %.1fg",
                totalCalories, totalProtein, totalCarbs, totalFats);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nutrition nutrition = (Nutrition) o;
        return Double.compare(nutrition.totalCalories, totalCalories) == 0 &&
               Double.compare(nutrition.totalProtein, totalProtein) == 0 &&
               Double.compare(nutrition.totalCarbs, totalCarbs) == 0 &&
               Double.compare(nutrition.totalFats, totalFats) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalCalories, totalProtein, totalCarbs, totalFats);
    }
}
