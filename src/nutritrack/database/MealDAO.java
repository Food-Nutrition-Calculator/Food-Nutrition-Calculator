package nutritrack.database;

import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Access Object (DAO) for Meals and MealItems.
 * Handles ACID transactions to save composite meals.
 *
 * Owned and maintained by: Member 2 (Meal Management)
 */
public class MealDAO {

    /**
     * Saves a complete meal and all its food items in a single database transaction.
     */
    public boolean saveCompleteMeal(Meal meal) {
        if (meal == null || meal.getMealItems() == null || meal.getMealItems().isEmpty()) {
            return false;
        }

        String insertMealSql = "INSERT INTO meals (meal_name, meal_date, meal_time) VALUES (?, ?, ?)";
        String insertItemSql = "INSERT INTO meal_items (meal_id, food_id, quantity, calculated_calories, calculated_protein, calculated_carbs, calculated_fats) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // ACID Transaction Start

            int generatedMealId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(insertMealSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, meal.getMealName());

                LocalDate d = meal.getMealDate() != null ? meal.getMealDate() : LocalDate.now();
                stmt.setDate(2, Date.valueOf(d));

                LocalTime t = meal.getMealTime() != null ? meal.getMealTime() : LocalTime.now();
                stmt.setTime(3, Time.valueOf(t));

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedMealId = rs.getInt(1);
                        meal.setMealId(generatedMealId);
                    }
                }
            }

            if (generatedMealId == -1) {
                conn.rollback();
                return false;
            }

            // Insert each line item in batch
            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                for (MealItem item : meal.getMealItems()) {
                    Nutrition n = item.getScaledNutrition();
                    itemStmt.setInt(1, generatedMealId);
                    itemStmt.setInt(2, item.getFoodItem().getFoodId());
                    itemStmt.setDouble(3, item.getQuantity());
                    itemStmt.setDouble(4, n.getTotalCalories());
                    itemStmt.setDouble(5, n.getTotalProtein());
                    itemStmt.setDouble(6, n.getTotalCarbs());
                    itemStmt.setDouble(7, n.getTotalFats());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit(); // Transaction Complete
            return true;

        } catch (SQLException e) {
            System.err.println("[MealDAO ERROR] Failed to save complete meal: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Deletes a recorded meal by ID.
     */
    public boolean deleteMeal(int mealId) {
        String sql = "DELETE FROM meals WHERE meal_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mealId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MealDAO ERROR] Delete failed: " + e.getMessage());
            return false;
        }
    }
}