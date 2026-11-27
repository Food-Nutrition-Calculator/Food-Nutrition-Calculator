package nutritrack.service;

import nutritrack.database.FoodDAO;
import nutritrack.database.MealDAO;
import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.utils.ValidationUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service Layer for Meal Management.
 * Coordinates meal composition, dynamic food item additions, portion updates,
 * and delegates transactional persistence to MealDAO.
 *
 * Owned and maintained by: Member 2 (Meal Management)
 */
public class MealService {

    private final MealDAO mealDAO;
    private final FoodDAO foodDAO;

    /**
     * Default constructor initializing default DAOs.
     */
    public MealService() {
        this(new MealDAO(), new FoodDAO());
    }

    /**
     * Constructor allowing dependency injection (for unit testing).
     */
    public MealService(MealDAO mealDAO, FoodDAO foodDAO) {
        this.mealDAO = (mealDAO != null) ? mealDAO : new MealDAO();
        this.foodDAO = (foodDAO != null) ? foodDAO : new FoodDAO();
    }

    /**
     * Initiates a new Meal instance with current date and time.
     *
     * @param mealName Friendly meal title (e.g., "Breakfast", "Lunch")
     * @return Fresh Meal instance
     */
    public Meal createNewMeal(String mealName) {
        if (!ValidationUtils.isNotEmpty(mealName)) {
            throw new IllegalArgumentException("Meal name cannot be empty.");
        }
        return new Meal(mealName.trim(), LocalDate.now(), LocalTime.now().withNano(0));
    }

    /**
     * Adds a food item with specified portion quantity to an active meal.
     * If the food already exists in the meal, quantity is automatically aggregated.
     *
     * @param meal Target active meal
     * @param food Food item to add
     * @param quantity Portion quantity
     * @return true if added successfully
     */
    public boolean addFoodToMeal(Meal meal, FoodItem food, double quantity) {
        if (meal == null) {
            throw new IllegalArgumentException("Target meal cannot be null.");
        }
        if (food == null) {
            throw new IllegalArgumentException("Food item cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Portion quantity must be greater than zero.");
        }

        meal.addMealItem(new MealItem(food, quantity));
        return true;
    }

    /**
     * Removes a food item from the active meal by its foodId.
     *
     * @param meal Target active meal
     * @param foodId ID of food to remove
     * @return true if item was found and removed
     */
    public boolean removeFoodFromMeal(Meal meal, int foodId) {
        if (meal == null) {
            throw new IllegalArgumentException("Target meal cannot be null.");
        }
        return meal.removeMealItem(foodId);
    }

    /**
     * Persists the complete active meal and its items to MySQL.
     *
     * @param meal The completed meal to save
     * @return true if database transaction succeeded
     */
    public boolean commitMeal(Meal meal) {
        if (meal == null) {
            throw new IllegalArgumentException("Cannot save a null meal.");
        }
        if (!ValidationUtils.isNotEmpty(meal.getMealName())) {
            throw new IllegalArgumentException("Meal name cannot be empty.");
        }
        if (meal.getMealItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot save a meal with no food items. Add at least one food item.");
        }

        return mealDAO.saveCompleteMeal(meal);
    }

    /**
     * Retrieves all recorded meals for a specified date.
     */
    public List<Meal> loadMealsForDate(LocalDate date) {
        return mealDAO.findMealsByDate(date);
    }

    /**
     * Loads a specific meal with details by its ID.
     */
    public Meal loadMealById(int mealId) {
        return mealDAO.findMealById(mealId);
    }

    /**
     * Deletes a recorded meal by ID.
     */
    public boolean deleteMeal(int mealId) {
        return mealDAO.deleteMeal(mealId);
    }

    /**
     * Retrieves available catalog foods from Member 1's DAO
     * to populate GUI food dropdowns.
     *
     * @return List of FoodItems
     */
    public List<FoodItem> getAvailableFoods() {
        return foodDAO.findAll();
    }
}
