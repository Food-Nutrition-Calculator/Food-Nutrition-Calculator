<<<<<<< HEAD
# Food Nutrition Calculator (NutriTrack)

A desktop dietary management and nutrition calculation application built with **Java Swing, JDBC, and MySQL**, developed by a 4-member student team.

---

## 📋 Project Structure

```
Food-Nutrition-Calculator/
│
├── .gitignore
├── README.md
│
├── database/
│   ├── schema.sql
│   └── queries.sql
│
├── web/
│   ├── foods.html
│   ├── meals.html
│   ├── nutrition.html
│   └── history.html
│
└── src/
    └── nutritrack/
        ├── NutriTrackApp.java
        │
        ├── model/
        │   ├── FoodItem.java
        │   ├── MealItem.java
        │   ├── Meal.java
        │   └── Nutrition.java
        │
        ├── database/
        │   ├── DBConnection.java
        │   ├── FoodDAO.java
        │   ├── MealDAO.java
        │   └── HistoryDAO.java
        │
        ├── service/
        │   ├── FoodService.java
        │   ├── MealService.java
        │   ├── NutritionService.java
        │   └── HistoryService.java
        │
        ├── ui/
        │   ├── MainFrame.java
        │   ├── FoodManagementPanel.java
        │   ├── MealCreationPanel.java
        │   ├── NutritionDashboardPanel.java
        │   └── MealHistoryPanel.java
        │
        └── utils/
            ├── ValidationUtils.java
            ├── DateUtils.java
            └── UIConstants.java
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
| **Member 1** | Food Management | `FoodItem`, `FoodDAO`, `FoodService`, `FoodManagementPanel` ,`food.html`|
| **Member 2** | Meal Management | `Meal`, `MealItem`, `MealDAO`, `MealService`, `MealCreationPanel`,`meal.html` |
| **Member 3** | Nutrition Engine | `Nutrition`, `NutritionService`, `NutritionDashboardPanel` ,`nutrition.html`|
| **Member 4** | DB & Integration | `DBConnection`, `HistoryDAO`, `HistoryService`, `MainFrame`, `MealHistoryPanel`,'history.html' |
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
=======
# Food-Nutrition-Calculator
Food Nutrition Calculator is a Java-based application designed to help users understand the nutritional value of food. It calculates nutritional information such as calories, proteins, carbohydrates, and fats based on food items and their quantities. The project aims to make food nutrition tracking easier and promote awareness of health.
>>>>>>> 2bc070703e59d0eb5386bf631a50386d96e31317
