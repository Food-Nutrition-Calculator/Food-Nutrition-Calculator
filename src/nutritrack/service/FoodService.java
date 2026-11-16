package nutritrack.service;

import nutritrack.database.FoodDAO;
import nutritrack.model.FoodItem;
import nutritrack.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service Layer for Food Management.
 * Handles business validation, delegates persistence to FoodDAO,
 * and provides sorting and searching algorithms using Java Collections.
 *
 * Owned and maintained by: Member 1 (Food Management)
 */
public class FoodService {

    private final FoodDAO foodDAO;

    /**
     * Default constructor creating an internal FoodDAO instance.
     */
    public FoodService() {
        this.foodDAO = new FoodDAO();
    }

    /**
     * Constructor allowing dependency injection (useful for unit testing).
     */
    public FoodService(FoodDAO foodDAO) {
        this.foodDAO = (foodDAO != null) ? foodDAO : new FoodDAO();
    }

    /**
     * Validates and registers a new FoodItem into the catalog.
     *
     * @param food The food item to add
     * @return true if successfully persisted, false otherwise
     */
    public boolean addFood(FoodItem food) {
        validateFoodItem(food);
        return foodDAO.insert(food);
    }

    /**
     * Validates and updates an existing FoodItem.
     *
     * @param food The food item with updated properties
     * @return true if updated successfully, false otherwise
     */
    public boolean updateFood(FoodItem food) {
        validateFoodItem(food);
        if (food.getFoodId() <= 0) {
            throw new IllegalArgumentException("Cannot update food without a valid positive food ID.");
        }
        return foodDAO.update(food);
    }

    /**
     * Deletes a food item by ID.
     *
     * @param foodId ID of food to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteFood(int foodId) {
        if (foodId <= 0) {
            throw new IllegalArgumentException("Food ID must be positive.");
        }
        return foodDAO.delete(foodId);
    }

    /**
     * Fetches a single food item by its ID.
     */
    public FoodItem getFoodById(int foodId) {
        if (foodId <= 0) {
            return null;
        }
        return foodDAO.findById(foodId);
    }

    /**
     * Retrieves all food items from the catalog.
     */
    public List<FoodItem> getAllFoods() {
        return foodDAO.findAll();
    }

    /**
     * Searches foods by keyword.
     */
    public List<FoodItem> searchFood(String name) {
        return foodDAO.searchByName(name);
    }

    /**
     * Sorts a list of food items based on the chosen nutritional criterion.
     * Implements sorting using Java Collections and Comparator.
     *
     * @param items List of FoodItems to sort
     * @param criterion Attribute to sort by: "NAME", "CALORIES", "PROTEIN", "CARBS", "FATS"
     * @param ascending true for ascending, false for descending
     * @return New list sorted according to the criterion
     */
    public List<FoodItem> getSortedFoods(List<FoodItem> items, String criterion, boolean ascending) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        List<FoodItem> sortedList = new ArrayList<>(items);
        Comparator<FoodItem> comparator;

        String key = (criterion != null) ? criterion.trim().toUpperCase() : "NAME";

        switch (key) {
            case "CALORIES":
                comparator = Comparator.comparingDouble(FoodItem::getCalories);
                break;
            case "PROTEIN":
                comparator = Comparator.comparingDouble(FoodItem::getProtein);
                break;
            case "CARBS":
            case "CARBOHYDRATES":
                comparator = Comparator.comparingDouble(FoodItem::getCarbohydrates);
                break;
            case "FATS":
                comparator = Comparator.comparingDouble(FoodItem::getFats);
                break;
            case "NAME":
            default:
                comparator = Comparator.comparing(FoodItem::getFoodName, String.CASE_INSENSITIVE_ORDER);
                break;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        sortedList.sort(comparator);
        return sortedList;
    }

    /**
     * Performs business rule validations on a FoodItem.
     */
    private void validateFoodItem(FoodItem food) {
        if (food == null) {
            throw new IllegalArgumentException("Food item cannot be null.");
        }
        if (!ValidationUtils.isNotEmpty(food.getFoodName())) {
            throw new IllegalArgumentException("Food name is required and cannot be empty.");
        }
        if (food.getDefaultServingSize() <= 0) {
            throw new IllegalArgumentException("Default serving size must be greater than zero.");
        }
        if (food.getCalories() < 0) {
            throw new IllegalArgumentException("Calories cannot be negative.");
        }
        if (food.getProtein() < 0 || food.getCarbohydrates() < 0 || food.getFats() < 0) {
            throw new IllegalArgumentException("Macronutrients (Protein, Carbs, Fats) cannot be negative.");
        }
    }
}
