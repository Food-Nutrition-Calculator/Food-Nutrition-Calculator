package nutritrack.database;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {

    public List<Meal> getMealsWithItemsByDate(LocalDate date) {
        List<Meal> meals = new ArrayList<>();
        String sql = "SELECT m.meal_id, m.meal_name, m.meal_date, m.meal_time, " +
                     "mi.meal_item_id, mi.quantity, mi.calculated_calories, mi.calculated_protein, mi.calculated_carbs, mi.calculated_fats, " +
                     "f.food_id, f.food_name, f.serving_unit, f.default_serving_size, f.calories, f.protein, f.carbohydrates, f.fats " +
                     "FROM meals m " +
                     "JOIN meal_items mi ON m.meal_id = mi.meal_id " +
                     "JOIN food_items f ON mi.food_id = f.food_id " +
                     "WHERE m.meal_date = ? " +
                     "ORDER BY m.meal_time ASC, mi.meal_item_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                Meal currentMeal = null;
                while (rs.next()) {
                    int mealId = rs.getInt("meal_id");
                    if (currentMeal == null || currentMeal.getMealId() != mealId) {
                        currentMeal = new Meal(
                            mealId,
                            rs.getString("meal_name"),
                            rs.getDate("meal_date").toLocalDate(),
                            rs.getTime("meal_time").toLocalTime()
                        );
                        meals.add(currentMeal);
                    }

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

                    MealItem item = new MealItem(rs.getInt("meal_item_id"), food, rs.getDouble("quantity"));
                    currentMeal.addMealItem(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in HistoryDAO: " + e.getMessage());
        }
        return meals;
    }

    public Nutrition getDailyAggregateNutrition(LocalDate date) {
        String sql = "SELECT " +
                     "COALESCE(SUM(mi.calculated_calories), 0) AS total_cal, " +
                     "COALESCE(SUM(mi.calculated_protein), 0) AS total_prot, " +
                     "COALESCE(SUM(mi.calculated_carbs), 0) AS total_carbs, " +
                     "COALESCE(SUM(mi.calculated_fats), 0) AS total_fats " +
                     "FROM meals m " +
                     "JOIN meal_items mi ON m.meal_id = mi.meal_id " +
                     "WHERE m.meal_date = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Nutrition(
                        rs.getDouble("total_cal"),
                        rs.getDouble("total_prot"),
                        rs.getDouble("total_carbs"),
                        rs.getDouble("total_fats")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in HistoryDAO: " + e.getMessage());
        }
        return new Nutrition();
    }

    public boolean deleteMeal(int mealId) {
        String sql = "DELETE FROM meals WHERE meal_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mealId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting meal: " + e.getMessage());
            return false;
        }
    }
}
