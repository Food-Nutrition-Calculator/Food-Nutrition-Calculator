package nutritrack.model;

import java.util.Objects;

/**
 * Represents a single food item in the catalog with its base nutritional profile
 * calibrated per standard serving size.
 *
 * Owned and maintained by: Member 1 (Food Management)
 */
public class FoodItem {
    private int foodId;
    private String foodName;
    private String servingUnit;
    private double defaultServingSize;
    private double calories;
    private double protein;
    private double carbohydrates;
    private double fats;

    /**
     * Default constructor initializing default values.
     */
    public FoodItem() {
        this.foodId = 0;
        this.foodName = "";
        this.servingUnit = "grams";
        this.defaultServingSize = 100.0;
        this.calories = 0.0;
        this.protein = 0.0;
        this.carbohydrates = 0.0;
        this.fats = 0.0;
    }

    /**
     * Constructor for existing records retrieved from the database (includes foodId).
     */
    public FoodItem(int foodId, String foodName, String servingUnit, double defaultServingSize,
                    double calories, double protein, double carbohydrates, double fats) {
        setFoodId(foodId);
        setFoodName(foodName);
        setServingUnit(servingUnit);
        setDefaultServingSize(defaultServingSize);
        setCalories(calories);
        setProtein(protein);
        setCarbohydrates(carbohydrates);
        setFats(fats);
    }

    /**
     * Constructor for newly created records before database insertion (foodId omitted/0).
     */
    public FoodItem(String foodName, String servingUnit, double defaultServingSize,
                    double calories, double protein, double carbohydrates, double fats) {
        this(0, foodName, servingUnit, defaultServingSize, calories, protein, carbohydrates, fats);
    }

    // --- Getters and Setters with Validation ---

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        if (foodName == null || foodName.trim().isEmpty()) {
            throw new IllegalArgumentException("Food name cannot be empty.");
        }
        this.foodName = foodName.trim();
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        if (servingUnit == null || servingUnit.trim().isEmpty()) {
            this.servingUnit = "grams";
        } else {
            this.servingUnit = servingUnit.trim();
        }
    }

    public double getDefaultServingSize() {
        return defaultServingSize;
    }

    public void setDefaultServingSize(double defaultServingSize) {
        if (defaultServingSize <= 0) {
            throw new IllegalArgumentException("Default serving size must be greater than zero.");
        }
        this.defaultServingSize = defaultServingSize;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        if (calories < 0) {
            throw new IllegalArgumentException("Calories cannot be negative.");
        }
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        if (protein < 0) {
            throw new IllegalArgumentException("Protein cannot be negative.");
        }
        this.protein = protein;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        if (carbohydrates < 0) {
            throw new IllegalArgumentException("Carbohydrates cannot be negative.");
        }
        this.carbohydrates = carbohydrates;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        if (fats < 0) {
            throw new IllegalArgumentException("Fats cannot be negative.");
        }
        this.fats = fats;
    }

    @Override
    public String toString() {
        // Formatted for Swing JComboBox dropdown representation
        return String.format("%s (%.0f %s - %.0f kcal)", foodName, defaultServingSize, servingUnit, calories);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItem foodItem = (FoodItem) o;
        return foodId == foodItem.foodId && Objects.equals(foodName, foodItem.foodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodId, foodName);
    }
}
