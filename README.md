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
- Default Password: `password` (adjust via `DBConnection.setCredentials("root", "your_password")` or environment variables `DB_USER` and `DB_PASSWORD`)

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

---

## 👥 Team Workload & Ownership

| Member | Module | Key Responsibilities |
| :--- | :--- | :--- |
| **Member 1** | Food Management | `FoodItem`, `FoodDAO`, `FoodService`, `FoodManagementPanel` |
| **Member 2** | Meal Management | `Meal`, `MealItem`, `MealDAO`, `MealService`, `MealCreationPanel` |
| **Member 3** | Nutrition Engine | `Nutrition`, `NutritionService`, `NutritionDashboardPanel` |
| **Member 4** | DB & Integration | `DBConnection`, `HistoryDAO`, `HistoryService`, `MainFrame`, `MealHistoryPanel` |
## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
