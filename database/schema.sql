-- =====================================================================
-- DATABASE INITIALIZATION SCRIPT: Food Nutrition Calculator (NutriTrack)
-- Responsible: Member 4 (Database & Integration)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS nutritrack_db
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE nutritrack_db;

-- ---------------------------------------------------------------------
-- Table 1: food_items
-- Base catalog of food items and their standard nutritional profiles
-- Managed primarily by Member 1 (Food Management)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS food_items (
    food_id INT AUTO_INCREMENT PRIMARY KEY,
    food_name VARCHAR(100) NOT NULL UNIQUE,
    serving_unit VARCHAR(30) NOT NULL DEFAULT 'grams',
    default_serving_size DECIMAL(8, 2) NOT NULL DEFAULT 100.00,
    calories DECIMAL(8, 2) NOT NULL,
    protein DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    carbohydrates DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    fats DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_positive_nutrients CHECK (
        calories >= 0 AND protein >= 0 AND carbohydrates >= 0 AND fats >= 0 AND default_serving_size > 0
    )
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Table 2: meals
-- Log of meals recorded with specific date and time
-- Managed primarily by Member 2 (Meal Management)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS meals (
    meal_id INT AUTO_INCREMENT PRIMARY KEY,
    meal_name VARCHAR(60) NOT NULL,
    meal_date DATE NOT NULL,
    meal_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Table 3: meal_items
-- Junction / Line-item table linking meals to food items with portions
-- Managed by Member 2 (Persistence) & Member 4 (History Queries)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS meal_items (
    meal_item_id INT AUTO_INCREMENT PRIMARY KEY,
    meal_id INT NOT NULL,
    food_id INT NOT NULL,
    quantity DECIMAL(8, 2) NOT NULL,
    calculated_calories DECIMAL(8, 2) NOT NULL,
    calculated_protein DECIMAL(8, 2) NOT NULL,
    calculated_carbs DECIMAL(8, 2) NOT NULL,
    calculated_fats DECIMAL(8, 2) NOT NULL,
    CONSTRAINT fk_meal_items_meal FOREIGN KEY (meal_id)
        REFERENCES meals(meal_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_meal_items_food FOREIGN KEY (food_id)
        REFERENCES food_items(food_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT chk_positive_quantity CHECK (quantity > 0)
) ENGINE=InnoDB;

-- Indexes for optimized querying
CREATE INDEX idx_meals_date ON meals(meal_date);
CREATE INDEX idx_meal_items_meal ON meal_items(meal_id);
CREATE INDEX idx_food_name ON food_items(food_name);

-- ---------------------------------------------------------------------
-- Seed Data for Development & Testing
-- ---------------------------------------------------------------------
INSERT IGNORE INTO food_items (food_id, food_name, serving_unit, default_serving_size, calories, protein, carbohydrates, fats) VALUES
(1, 'Rolled Oats', 'grams', 100.00, 389.00, 16.90, 66.30, 6.90),
(2, 'Whole Milk', 'ml', 100.00, 61.00, 3.20, 4.80, 3.30),
(3, 'Banana', 'grams', 100.00, 89.00, 1.10, 22.80, 0.30),
(4, 'Boiled Egg', 'grams', 50.00, 77.00, 6.30, 0.60, 5.30),
(5, 'Chicken Breast (Grilled)', 'grams', 100.00, 165.00, 31.00, 0.00, 3.60),
(6, 'White Rice (Cooked)', 'grams', 100.00, 130.00, 2.70, 28.20, 0.30),
(7, 'Olive Oil', 'ml', 15.00, 119.00, 0.00, 0.00, 13.50),
(8, 'Almonds', 'grams', 30.00, 173.00, 6.00, 6.00, 15.00);
