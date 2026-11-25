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

    public Nutrition() {
        this(0.0, 0.0, 0.0, 0.0);
    }

    public Nutrition(double totalCalories, double totalProtein, double totalCarbs, double totalFats) {
        setTotalCalories(totalCalories);
        setTotalProtein(totalProtein);
        setTotalCarbs(totalCarbs);
        setTotalFats(totalFats);
    }

    /**
     * Factory method: Computes scaled nutrition for a given FoodItem based on entered quantity.
     * Formula: Scaled = Base * (Quantity / DefaultServingSize)
     */
    public static Nutrition scale(FoodItem food, double quantity) {
        if (food == null) {
            throw new IllegalArgumentException("FoodItem cannot be null for nutrition scaling.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        double serving = food.getDefaultServingSize();
        if (serving <= 0) {
            serving = 100.0;
        }

        double ratio = quantity / serving;
        double scaledCalories = round(food.getCalories() * ratio);
        double scaledProtein = round(food.getProtein() * ratio);
        double scaledCarbs = round(food.getCarbohydrates() * ratio);
        double scaledFats = round(food.getFats() * ratio);

        return new Nutrition(scaledCalories, scaledProtein, scaledCarbs, scaledFats);
    }

    /**
     * Overloaded helper for scaling directly with raw numbers.
     */
    public static Nutrition scale(double cal, double prot, double carbs, double fats, double defaultServing, double quantity) {
        if (defaultServing <= 0) defaultServing = 100.0;
        double ratio = quantity / defaultServing;
        return new Nutrition(round(cal * ratio), round(prot * ratio), round(carbs * ratio), round(fats * ratio));
    }

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

    public double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(double totalCalories) { this.totalCalories = Math.max(0.0, round(totalCalories)); }

    public double getTotalProtein() { return totalProtein; }
    public void setTotalProtein(double totalProtein) { this.totalProtein = Math.max(0.0, round(totalProtein)); }

    public double getTotalCarbs() { return totalCarbs; }
    public void setTotalCarbs(double totalCarbs) { this.totalCarbs = Math.max(0.0, round(totalCarbs)); }

    public double getTotalFats() { return totalFats; }
    public void setTotalFats(double totalFats) { this.totalFats = Math.max(0.0, round(totalFats)); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nutrition that = (Nutrition) o;
        return Double.compare(that.totalCalories, totalCalories) == 0 &&
               Double.compare(that.totalProtein, totalProtein) == 0 &&
               Double.compare(that.totalCarbs, totalCarbs) == 0 &&
               Double.compare(that.totalFats, totalFats) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalCalories, totalProtein, totalCarbs, totalFats);
    }

    @Override
    public String toString() {
        return String.format("Nutrition[Calories=%.1f, Protein=%.1fg, Carbs=%.1fg, Fats=%.1fg]",
                totalCalories, totalProtein, totalCarbs, totalFats);
    }
}