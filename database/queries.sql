USE nutritrack_db;

-- 1. Daily Aggregated Nutrition Summary
SELECT 
    m.meal_date,
    COUNT(DISTINCT m.meal_id) AS total_meals,
    COALESCE(SUM(mi.calculated_calories), 0.00) AS total_calories,
    COALESCE(SUM(mi.calculated_protein), 0.00) AS total_protein,
    COALESCE(SUM(mi.calculated_carbs), 0.00) AS total_carbs,
    COALESCE(SUM(mi.calculated_fats), 0.00) AS total_fats
FROM meals m
JOIN meal_items mi ON m.meal_id = mi.meal_id
WHERE m.meal_date = CURRENT_DATE
GROUP BY m.meal_date;

-- 2. 3-Table JOIN for Detailed Breakdown
SELECT 
    m.meal_id, m.meal_name, m.meal_time,
    f.food_name, mi.quantity, f.serving_unit,
    mi.calculated_calories, mi.calculated_protein, mi.calculated_carbs, mi.calculated_fats
FROM meals m
JOIN meal_items mi ON m.meal_id = mi.meal_id
JOIN food_items f ON mi.food_id = f.food_id
ORDER BY m.meal_date DESC, m.meal_time ASC;