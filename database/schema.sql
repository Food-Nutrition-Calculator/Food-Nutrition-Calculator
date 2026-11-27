CREATE DATABASE IF NOT EXISTS nutritrack_db
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nutritrack_db;

CREATE TABLE IF NOT EXISTS food_items (
    food_id INT AUTO_INCREMENT PRIMARY KEY,
    food_name VARCHAR(100) NOT NULL UNIQUE,
    serving_unit VARCHAR(30) NOT NULL DEFAULT 'grams',
    default_serving_size DECIMAL(8, 2) NOT NULL DEFAULT 100.00,
    calories DECIMAL(8, 2) NOT NULL,
    protein DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    carbohydrates DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    fats DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS meals (
    meal_id INT AUTO_INCREMENT PRIMARY KEY,
    meal_name VARCHAR(60) NOT NULL,
    meal_date DATE NOT NULL,
    meal_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS meal_items (
    meal_item_id INT AUTO_INCREMENT PRIMARY KEY,
    meal_id INT NOT NULL,
    food_id INT NOT NULL,
    quantity DECIMAL(8, 2) NOT NULL,
    calculated_calories DECIMAL(8, 2) NOT NULL,
    calculated_protein DECIMAL(8, 2) NOT NULL,
    calculated_carbs DECIMAL(8, 2) NOT NULL,
    calculated_fats DECIMAL(8, 2) NOT NULL,
    CONSTRAINT fk_meal_items_meal FOREIGN KEY (meal_id) REFERENCES meals(meal_id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_items_food FOREIGN KEY (food_id) REFERENCES food_items(food_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

INSERT INTO food_items (food_name, serving_unit, default_serving_size, calories, protein, carbohydrates, fats) VALUES
('Rolled Oats', 'grams', 100.00, 389.00, 16.90, 66.30, 6.90),
('Whole Milk', 'ml', 100.00, 61.00, 3.20, 4.80, 3.30),
('Banana', 'grams', 100.00, 89.00, 1.10, 22.80, 0.30),
('Boiled Egg', 'grams', 50.00, 77.00, 6.30, 0.60, 5.30),
('Chicken Breast', 'grams', 100.00, 165.00, 31.00, 0.00, 3.60),
('White Rice', 'grams', 100.00, 130.00, 2.70, 28.20, 0.30)
ON DUPLICATE KEY UPDATE food_name=food_name;
