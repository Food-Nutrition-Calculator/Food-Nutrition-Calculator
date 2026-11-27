# Food-Nutrition-Calculator
Food Nutrition Calculator is a Java-based application designed to help users understand the nutritional value of food. It calculates nutritional information such as calories, proteins, carbohydrates, and fats based on food items and their quantities. The project aims to make food nutrition tracking easier and promote awareness of health.

# Food Nutrition Calculator (NutriTrack)

A desktop dietary management and nutrition calculation application built with **Java Swing, JDBC, and MySQL**, developed by a 4-member student team.

---

## 📋 Project Structure

```
Food-Nutrition-Calculator/
|-- database/
|   |-- schema.sql                       # Database initialization script + seed data
|-- src/
|   |-- nutritrack/
|       |-- Phase0Verification.java      # Phase 0 verification test runner
|       |
|       |-- model/                       # Domain Entity Layer
|       |   |-- FoodItem.java            # [Member 1] Food item profile & serving size
|       |   |-- MealItem.java            # [Member 2] Food item portion wrapper
|       |   |-- Meal.java                # [Member 2] Composite meal collection & timestamp
|       |   |-- Nutrition.java           # [Member 3] Calories & macronutrient calculations
|       |
|       |-- database/                    # Data Access Layer (DAO)
|       |   |-- DBConnection.java        # [Member 4] Singleton JDBC connection manager
|       |   |-- FoodDAO.java             # [Member 1] CRUD for food_items
|       |   |-- MealDAO.java             # [Member 2] Transactional meal persistence
|       |   |-- HistoryDAO.java          # [Member 4] Analytical SQL queries (SUM, JOIN)
|       |
|       |-- service/                     # Business Logic Layer
|       |   |-- FoodService.java         # [Member 1] Food search, sorting & validation
|       |   |-- MealService.java         # [Member 2] Meal composition & rules
|       |   |-- NutritionService.java    # [Member 3] Macro scaling & percentage math
|       |   |-- HistoryService.java      # [Member 4] Daily nutrition aggregations
|       |
|       |-- ui/                          # Presentation Layer (Java Swing)
|       |   |-- MainFrame.java           # [Member 4] Main application window & navigation
|       |   |-- FoodManagementPanel.java # [Member 1] Food CRUD table & search GUI
|       |   |-- MealCreationPanel.java   # [Member 2] Interactive meal builder GUI
|       |   |-- NutritionDashboardPanel.java # [Member 3] Daily metrics & macro breakdown
|       |   |-- MealHistoryPanel.java    # [Member 4] Historical calendar log GUI
|       |
|       |-- utils/                       # Shared Utilities
|           |-- ValidationUtils.java     # User input sanitization
|           |-- DateUtils.java           # LocalDate to SQL Date conversions
|           |-- UIConstants.java         # Color palette & font styles
```

---

## 🚀 Setup & Execution Guide

### 1. Database Initialization
Make sure you have MySQL installed and running locally. Run the setup script using MySQL CLI or MySQL Workbench:

```bash
mysql -u root -p < database/schema.sql
```

Default connection parameters in `DBConnection.java`:
- Host: `localhost:3306`
- Database: `nutritrack_db`
- Default User: `root`
- Password: set `NUTRITRACK_DB_PASSWORD` to the password for your MySQL account. The app does not assume a default password.
- Optional overrides: `NUTRITRACK_DB_USER` and `NUTRITRACK_DB_URL`

For PowerShell, set these in the terminal before launching the app:

```powershell
$env:NUTRITRACK_DB_USER = "root"
$env:NUTRITRACK_DB_PASSWORD = "your_mysql_password"
```

If you start the app from an IDE, add the same names and values to its Run/Debug environment variables. You can also pass them as Java system properties with `-DNUTRITRACK_DB_USER=...` and `-DNUTRITRACK_DB_PASSWORD=...`.

### 2. Required External Library
Download and include the **MySQL Connector/J** driver jar (version `8.0.x` or `9.x`):
- [Download MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)
- Place `mysql-connector-j-8.x.x.jar` in a `lib/` folder or add it to your IDE's classpath.

### 3. Compile & Run Phase 0 Verification
```bash
# Compile all source files
javac -d bin -sourcepath src $(find src -name "*.java")

# Run Phase 0 verification suite
java -cp bin nutritrack.Phase0Verification
```

### 4. Run the integrated web application

From the project root in PowerShell, compile the Java sources and start the single web server:

```powershell
$javaFiles = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d bin $javaFiles
java -cp "bin;lib\mysql-connector-j-26.10.0.jar" nutritrack.NutriTrackServer
```

Keep that terminal open and visit **http://localhost:8081/**. The Food Catalog, Meal Builder, Nutrition Dashboard, and Meal History use the same server and database. Configure `NUTRITRACK_DB_USER` and `NUTRITRACK_DB_PASSWORD` in the same PowerShell terminal before starting Java if your MySQL login is different from the local defaults. Do not start the separate `Member2MealServer` or `Member3NutritionServer` for this integrated web app.

---

## 👥 Team Workload & Ownership

| Member | Module | Key Responsibilities |
| :--- | :--- | :--- |
| **Member 1** | Food Management | `FoodItem`, `FoodDAO`, `FoodService`, `FoodManagementPanel` |
| **Member 2** | Meal Management | `Meal`, `MealItem`, `MealDAO`, `MealService`, `MealCreationPanel` |
| **Member 3** | Nutrition Engine | `Nutrition`, `NutritionService`, `NutritionDashboardPanel` |
| **Member 4** | DB & Integration | `DBConnection`, `HistoryDAO`, `HistoryService`, `MainFrame`, `MealHistoryPanel` |
 main
