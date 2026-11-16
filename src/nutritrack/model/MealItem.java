package nutritrack.model;

import java.util.Objects;

/**
 * Represents a single portion of a FoodItem included within a Meal.
 * Wraps the base food item with a user-specified quantity and computes scaled nutrients.
 *
 * Owned and maintained by: Member 2 (Meal Management)
 */
public class MealItem {
    private int mealItemId;
    private FoodItem foodItem;
    private double quantity;

    /**
     * Constructor for creating a new line item in a meal before persistence.
     *
     * @param foodItem The base food item
     * @param quantity The entered portion quantity
     */
    public MealItem(FoodItem foodItem, double quantity) {
        this(0, foodItem, quantity);
    }

    /**
     * Constructor for loading existing line items from the database.
     *
     * @param mealItemId Database primary key
     * @param foodItem The base food item
     * @param quantity The portion quantity
     */
    public MealItem(int mealItemId, FoodItem foodItem, double quantity) {
        setMealItemId(mealItemId);
        setFoodItem(foodItem);
        setQuantity(quantity);
    }

    // --- Domain Calculation Methods ---

    /**
     * Returns the portion scaling ratio relative to default serving size.
     */
    public double getRatio() {
        if (foodItem == null || foodItem.getDefaultServingSize() <= 0) {
            return 1.0;
        }
        return quantity / foodItem.getDefaultServingSize();
    }

    /**
     * Returns the scaled nutrition profile for this item.
     */
    public Nutrition getScaledNutrition() {
        return Nutrition.scale(foodItem, quantity);
    }

    public double getCalculatedCalories() {
        return getScaledNutrition().getTotalCalories();
    }

    public double getCalculatedProtein() {
        return getScaledNutrition().getTotalProtein();
    }

    public double getCalculatedCarbs() {
        return getScaledNutrition().getTotalCarbs();
    }

    public double getCalculatedFats() {
        return getScaledNutrition().getTotalFats();
    }

    // --- Getters and Setters with Validation ---

    public int getMealItemId() {
        return mealItemId;
    }

    public void setMealItemId(int mealItemId) {
        this.mealItemId = mealItemId;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        if (foodItem == null) {
            throw new IllegalArgumentException("FoodItem cannot be null in MealItem.");
        }
        this.foodItem = foodItem;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.quantity = Math.round(quantity * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s - %.1f %s (%.1f kcal)",
                foodItem.getFoodName(), quantity, foodItem.getServingUnit(), getCalculatedCalories());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealItem mealItem = (MealItem) o;
        return mealItemId == mealItem.mealItemId &&
               Double.compare(mealItem.quantity, quantity) == 0 &&
               Objects.equals(foodItem, mealItem.foodItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealItemId, foodItem, quantity);
    }
}
