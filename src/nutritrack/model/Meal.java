package nutritrack.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a composite meal (e.g., Breakfast, Lunch) composed of multiple MealItems.
 * Contains date, time, and encapsulated collection operations.
 *
 * Owned and maintained by: Member 2 (Meal Management)
 */
public class Meal {
    private int mealId;
    private String mealName;
    private LocalDate mealDate;
    private LocalTime mealTime;
    private List<MealItem> mealItems;

    /**
     * Default constructor: initializes empty meal item list and sets timestamp to now.
     */
    public Meal() {
        this.mealId = 0;
        this.mealName = "New Meal";
        this.mealDate = LocalDate.now();
        this.mealTime = LocalTime.now().withNano(0);
        this.mealItems = new ArrayList<>();
    }

    /**
     * Parameterized constructor without ID (for newly created meals before DB insert).
     */
    public Meal(String mealName, LocalDate mealDate, LocalTime mealTime) {
        this(0, mealName, mealDate, mealTime);
    }

    /**
     * Full constructor for existing meals loaded from database.
     */
    public Meal(int mealId, String mealName, LocalDate mealDate, LocalTime mealTime) {
        setMealId(mealId);
        setMealName(mealName);
        setMealDate(mealDate);
        setMealTime(mealTime);
        this.mealItems = new ArrayList<>();
    }

    // --- Encapsulated Collection Management ---

    /**
     * Adds a MealItem to this meal.
     * If the food item already exists in this meal, aggregates the portion quantity.
     *
     * @param item The MealItem to add
     */
    public void addMealItem(MealItem item) {
        if (item == null) {
            throw new IllegalArgumentException("MealItem cannot be null.");
        }
        for (MealItem existing : mealItems) {
            if (existing.getFoodItem().getFoodId() == item.getFoodItem().getFoodId()) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        mealItems.add(item);
    }

    /**
     * Removes a food item from the meal by its foodId.
     *
     * @param foodId Identifier of the food to remove
     * @return true if an item was removed, false otherwise
     */
    public boolean removeMealItem(int foodId) {
        return mealItems.removeIf(item -> item.getFoodItem().getFoodId() == foodId);
    }

    /**
     * Clears all items currently in this meal.
     */
    public void clearItems() {
        mealItems.clear();
    }

    /**
     * Computes the aggregated total nutrition for all items within this meal.
     *
     * @return Aggregated Nutrition object
     */
    public Nutrition calculateTotalNutrition() {
        Nutrition total = new Nutrition();
        for (MealItem item : mealItems) {
            total = total.add(item.getScaledNutrition());
        }
        return total;
    }

    // --- Getters and Setters ---

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        if (mealName == null || mealName.trim().isEmpty()) {
            throw new IllegalArgumentException("Meal name cannot be empty.");
        }
        this.mealName = mealName.trim();
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        if (mealDate == null) {
            this.mealDate = LocalDate.now();
        } else {
            this.mealDate = mealDate;
        }
    }

    public LocalTime getMealTime() {
        return mealTime;
    }

    public void setMealTime(LocalTime mealTime) {
        if (mealTime == null) {
            this.mealTime = LocalTime.now().withNano(0);
        } else {
            this.mealTime = mealTime;
        }
    }

    public List<MealItem> getMealItems() {
        return new ArrayList<>(mealItems); // Return defensive copy to maintain encapsulation
    }

    public void setMealItems(List<MealItem> items) {
        this.mealItems = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format("%s (%s %s) - %d items", mealName, mealDate, mealTime, mealItems.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal = (Meal) o;
        return mealId == meal.mealId && Objects.equals(mealName, meal.mealName) && Objects.equals(mealDate, meal.mealDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealId, mealName, mealDate);
    }
}
