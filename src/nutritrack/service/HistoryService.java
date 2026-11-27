package nutritrack.service;

import nutritrack.database.HistoryDAO;
import nutritrack.model.Meal;
import nutritrack.model.Nutrition;

import java.time.LocalDate;
import java.util.List;

public class HistoryService {
    private final HistoryDAO historyDAO;

    public HistoryService() {
        this.historyDAO = new HistoryDAO();
    }

    public List<Meal> getDailyMealHistory(LocalDate date) {
        if (date == null) date = LocalDate.now();
        return historyDAO.getMealsWithItemsByDate(date);
    }

    public Nutrition getDailyNutritionReport(LocalDate date) {
        if (date == null) date = LocalDate.now();
        return historyDAO.getDailyAggregateNutrition(date);
    }

    public boolean deleteHistoricalMeal(int mealId) {
        return historyDAO.deleteMeal(mealId);
    }
}
