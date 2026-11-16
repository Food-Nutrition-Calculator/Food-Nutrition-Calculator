package nutritrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import nutritrack.model.FoodItem;

/**
 * Data Access Object (DAO) for food_items table.
 * Executes SQL CRUD operations via JDBC and maps ResultSets to FoodItem domain objects.
 *
 * Owned and maintained by: Member 1 (Food Management)
 */
public class FoodDAO {

    /**
     * Inserts a new FoodItem into the database.
     * Sets the generated food_id onto the passed FoodItem object.
     *
     * @param food The FoodItem to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insert(FoodItem food) {
        if (food == null) {
            return false;
        }

        String sql = "INSERT INTO food_items (food_name, serving_unit, default_serving_size, calories, protein, carbohydrates, fats) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, food.getFoodName());
            stmt.setString(2, food.getServingUnit());
            stmt.setDouble(3, food.getDefaultServingSize());
            stmt.setDouble(4, food.getCalories());
            stmt.setDouble(5, food.getProtein());
            stmt.setDouble(6, food.getCarbohydrates());
            stmt.setDouble(7, food.getFats());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        food.setFoodId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[FoodDAO ERROR] Failed to insert food item: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates an existing FoodItem in the database based on food_id.
     *
     * @param food The FoodItem with updated values
     * @return true if record was updated, false otherwise
     */
    public boolean update(FoodItem food) {
        if (food == null || food.getFoodId() <= 0) {
            return false;
        }

        String sql = "UPDATE food_items SET food_name = ?, serving_unit = ?, default_serving_size = ?, "
                   + "calories = ?, protein = ?, carbohydrates = ?, fats = ? WHERE food_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, food.getFoodName());
            stmt.setString(2, food.getServingUnit());
            stmt.setDouble(3, food.getDefaultServingSize());
            stmt.setDouble(4, food.getCalories());
            stmt.setDouble(5, food.getProtein());
            stmt.setDouble(6, food.getCarbohydrates());
            stmt.setDouble(7, food.getFats());
            stmt.setInt(8, food.getFoodId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FoodDAO ERROR] Failed to update food item ID " + food.getFoodId() + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a FoodItem from the database by its ID.
     * Note: Deletion will be rejected if the food is referenced in a meal_items record.
     *
     * @param foodId ID of the food item to delete
     * @return true if deleted, false otherwise
     */
    public boolean delete(int foodId) {
        if (foodId <= 0) {
            return false;
        }

        String sql = "DELETE FROM food_items WHERE food_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, foodId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FoodDAO ERROR] Failed to delete food item ID " + foodId + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a single FoodItem by its primary key ID.
     *
     * @param foodId Database primary key
     * @return FoodItem object or null if not found
     */
    public FoodItem findById(int foodId) {
        if (foodId <= 0) {
            return null;
        }

        String sql = "SELECT food_id, food_name, serving_unit, default_serving_size, calories, protein, carbohydrates, fats "
                   + "FROM food_items WHERE food_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, foodId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFoodItem(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[FoodDAO ERROR] Error finding food item ID " + foodId + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all food items from the catalog sorted by name.
     *
     * @return List of all FoodItems (never null)
     */
    public List<FoodItem> findAll() {
        List<FoodItem> list = new ArrayList<>();
        String sql = "SELECT food_id, food_name, serving_unit, default_serving_size, calories, protein, carbohydrates, fats "
                   + "FROM food_items ORDER BY food_name ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToFoodItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("[FoodDAO ERROR] Error fetching all food items: " + e.getMessage());
        }
        return list;
    }

    /**
     * Searches for food items whose name contains the search keyword (case-insensitive).
     *
     * @param keyword Partial or full food name
     * @return List of matching FoodItems (never null)
     */
    public List<FoodItem> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        List<FoodItem> list = new ArrayList<>();
        String sql = "SELECT food_id, food_name, serving_unit, default_serving_size, calories, protein, carbohydrates, fats "
                   + "FROM food_items WHERE LOWER(food_name) LIKE LOWER(?) ORDER BY food_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword.trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToFoodItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[FoodDAO ERROR] Error searching food items with keyword '" + keyword + "': " + e.getMessage());
        }
        return list;
    }

    /**
     * Helper method to map a ResultSet row to a FoodItem domain instance.
     */
    private FoodItem mapRowToFoodItem(ResultSet rs) throws SQLException {
        return new FoodItem(
            rs.getInt("food_id"),
            rs.getString("food_name"),
            rs.getString("serving_unit"),
            rs.getDouble("default_serving_size"),
            rs.getDouble("calories"),
            rs.getDouble("protein"),
            rs.getDouble("carbohydrates"),
            rs.getDouble("fats")
        );
    }
}
