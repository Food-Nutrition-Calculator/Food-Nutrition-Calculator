package nutritrack.database;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for meals and meal_items tables.
 * Implements transactional persistence using JDBC commit and rollback.
 *
 * Owned and maintained by: Member 2 (Meal Management)
 */
public class MealDAO {

    /**
     * Persists a complete meal along with all its line items within a single database transaction.
     * Uses connection commit and rollback to preserve ACID properties.
     *
     * @param meal The Meal object containing items to save
     * @return true if meal and all items were successfully saved, false otherwise
     */
    public boolean saveCompleteMeal(Meal meal) {
        if (meal == null || meal.getMealItems().isEmpty()) {
            System.err.println("[MealDAO ERROR] Cannot save an empty meal or null meal.");
            return false;
        }

        String insertMealSql = "INSERT INTO meals (meal_name, meal_date, meal_time) VALUES (?, ?, ?)";
        String insertItemSql = "INSERT INTO meal_items (meal_id, food_id, quantity, calculated_calories, "
                             + "calculated_protein, calculated_carbs, calculated_fats) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // Begin manual transaction
            conn.setAutoCommit(false);

            int generatedMealId = -1;

            // 1. Insert parent Meal record
            try (PreparedStatement mealStmt = conn.prepareStatement(insertMealSql, Statement.RETURN_GENERATED_KEYS)) {
                mealStmt.setString(1, meal.getMealName());
                mealStmt.setDate(2, DateUtils.toSqlDate(meal.getMealDate()));
                mealStmt.setTime(3, DateUtils.toSqlTime(meal.getMealTime()));

                int affected = mealStmt.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet rs = mealStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedMealId = rs.getInt(1);
                        meal.setMealId(generatedMealId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Insert child MealItems
            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql, Statement.RETURN_GENERATED_KEYS)) {
                for (MealItem item : meal.getMealItems()) {
                    itemStmt.setInt(1, generatedMealId);
                    itemStmt.setInt(2, item.getFoodItem().getFoodId());
                    itemStmt.setDouble(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getCalculatedCalories());
                    itemStmt.setDouble(5, item.getCalculatedProtein());
                    itemStmt.setDouble(6, item.getCalculatedCarbs());
                    itemStmt.setDouble(7, item.getCalculatedFats());

                    itemStmt.executeUpdate();

                    try (ResultSet rs = itemStmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            item.setMealItemId(rs.getInt(1));
                        }
                    }
                }
            }

            // Commit transaction if all inserts succeeded
            conn.commit();
            System.out.println("[MealDAO] Successfully committed meal ID: " + generatedMealId + " with " + meal.getMealItems().size() + " items.");
            return true;

        } catch (SQLException e) {
            System.err.println("[MealDAO ERROR] Transaction failed while saving meal: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[MealDAO] Rolled back transaction successfully.");
                } catch (SQLException rollbackEx) {
                    System.err.println("[MealDAO ERROR] Rollback failed: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                } catch (SQLException e) {
                    System.err.println("[MealDAO ERROR] Failed to reset auto-commit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Loads a specific meal and all its associated line items by meal ID.
     */
    public Meal findMealById(int mealId) {
        if (mealId <= 0) return null;

        String mealSql = "SELECT meal_id, meal_name, meal_date, meal_time FROM meals WHERE meal_id = ?";
        String itemsSql = "SELECT mi.meal_item_id, mi.quantity, "
                        + "f.food_id, f.food_name, f.serving_unit, f.default_serving_size, "
                        + "f.calories, f.protein, f.carbohydrates, f.fats "
                        + "FROM meal_items mi "
                        + "JOIN food_items f ON mi.food_id = f.food_id "
                        + "WHERE mi.meal_id = ?";

        Meal meal = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement mealStmt = conn.prepareStatement(mealSql)) {

            mealStmt.setInt(1, mealId);
            try (ResultSet rs = mealStmt.executeQuery()) {
                if (rs.next()) {
                    meal = new Meal(
                        rs.getInt("meal_id"),
                        rs.getString("meal_name"),
                        DateUtils.toLocalDate(rs.getDate("meal_date")),
                        DateUtils.toLocalTime(rs.getTime("meal_time"))
                    );
                }
            }

            if (meal != null) {
                try (PreparedStatement itemsStmt = conn.prepareStatement(itemsSql)) {
                    itemsStmt.setInt(1, mealId);
                    try (ResultSet itemsRs = itemsStmt.executeQuery()) {
                        while (itemsRs.next()) {
                            FoodItem food = new FoodItem(
                                itemsRs.getInt("food_id"),
                                itemsRs.getString("food_name"),
                                itemsRs.getString("serving_unit"),
                                itemsRs.getDouble("default_serving_size"),
                                itemsRs.getDouble("calories"),
                                itemsRs.getDouble("protein"),
                                itemsRs.getDouble("carbohydrates"),
                                itemsRs.getDouble("fats")
                            );
                            MealItem item = new MealItem(
                                itemsRs.getInt("meal_item_id"),
                                food,
                                itemsRs.getDouble("quantity")
                            );
                            meal.addMealItem(item);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[MealDAO ERROR] Error retrieving meal ID " + mealId + ": " + e.getMessage());
        }
        return meal;
    }

    /**
     * Retrieves all meals logged for a specific calendar date.
     */
    public List<Meal> findMealsByDate(LocalDate date) {
        List<Meal> meals = new ArrayList<>();
        if (date == null) return meals;

        String sql = "SELECT meal_id, meal_name, meal_date, meal_time FROM meals WHERE meal_date = ? ORDER BY meal_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, DateUtils.toSqlDate(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Meal meal = new Meal(
                        rs.getInt("meal_id"),
                        rs.getString("meal_name"),
                        DateUtils.toLocalDate(rs.getDate("meal_date")),
                        DateUtils.toLocalTime(rs.getTime("meal_time"))
                    );
                    meals.add(meal);
                }
            }

            // Populate items for each meal
            for (Meal meal : meals) {
                populateItemsForMeal(meal);
            }

        } catch (SQLException e) {
            System.err.println("[MealDAO ERROR] Error finding meals by date " + date + ": " + e.getMessage());
        }
        return meals;
    }

    /**
     * Deletes a meal by ID (associated meal_items will be deleted automatically via ON DELETE CASCADE).
     */
    public boolean deleteMeal(int mealId) {
        if (mealId <= 0) return false;

        String sql = "DELETE FROM meals WHERE meal_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mealId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MealDAO ERROR] Error deleting meal ID " + mealId + ": " + e.getMessage());
        }
        return false;
    }

    private void populateItemsForMeal(Meal meal) {
        String itemsSql = "SELECT mi.meal_item_id, mi.quantity, "
                        + "f.food_id, f.food_name, f.serving_unit, f.default_serving_size, "
                        + "f.calories, f.protein, f.carbohydrates, f.fats "
                        + "FROM meal_items mi "
                        + "JOIN food_items f ON mi.food_id = f.food_id "
                        + "WHERE mi.meal_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(itemsSql)) {

            stmt.setInt(1, meal.getMealId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FoodItem food = new FoodItem(
                        rs.getInt("food_id"),
                        rs.getString("food_name"),
                        rs.getString("serving_unit"),
                        rs.getDouble("default_serving_size"),
                        rs.getDouble("calories"),
                        rs.getDouble("protein"),
                        rs.getDouble("carbohydrates"),
                        rs.getDouble("fats")
                    );
                    meal.addMealItem(new MealItem(
                        rs.getInt("meal_item_id"),
                        food,
                        rs.getDouble("quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[MealDAO ERROR] Error loading items for meal ID " + meal.getMealId() + ": " + e.getMessage());
        }
    }
}
