package nutritrack.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nutritrack.database.HistoryDAO;
import nutritrack.model.Meal;
import nutritrack.model.Nutrition;
import nutritrack.service.NutritionService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Calculates a nutrition dashboard from meals saved in MySQL. */
public class NutritionHandler implements HttpHandler {
    private final HistoryDAO historyDAO = new HistoryDAO();
    private final NutritionService nutritionService = new NutritionService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        String rawDate = HistoryHandler.queryValue(exchange.getRequestURI().getQuery(), "date");
        LocalDate date;
        try { date = rawDate.isEmpty() ? LocalDate.now() : LocalDate.parse(rawDate); }
        catch (RuntimeException e) { HistoryHandler.respond(exchange, 400, "{\"error\":\"Date must use YYYY-MM-DD.\"}"); return; }

        List<Meal> meals = historyDAO.getMealsWithItemsByDate(date);
        Nutrition total = nutritionService.calculateDailyNutrition(meals);
        Map<String, Double> percentages = nutritionService.calculateMacronutrientPercentages(total);
        StringBuilder json = new StringBuilder("{\"date\":\"").append(date)
            .append("\",\"totalCalories\":").append(total.getTotalCalories())
            .append(",\"totalProtein\":").append(total.getTotalProtein())
            .append(",\"totalCarbs\":").append(total.getTotalCarbs())
            .append(",\"totalFats\":").append(total.getTotalFats())
            .append(",\"proteinPct\":").append(percentages.get("PROTEIN_PCT"))
            .append(",\"carbsPct\":").append(percentages.get("CARBS_PCT"))
            .append(",\"fatsPct\":").append(percentages.get("FATS_PCT"))
            .append(",\"mealCount\":").append(meals.size()).append(",\"meals\":[");
        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.get(i);
            Nutrition n = meal.calculateTotalNutrition();
            if (i > 0) json.append(',');
            json.append("{\"mealId\":").append(meal.getMealId())
                .append(",\"mealName\":\"").append(HistoryHandler.escape(meal.getMealName())).append("\"")
                .append(",\"calories\":").append(n.getTotalCalories())
                .append(",\"protein\":").append(n.getTotalProtein())
                .append(",\"carbs\":").append(n.getTotalCarbs())
                .append(",\"fats\":").append(n.getTotalFats())
                .append(",\"itemCount\":").append(meal.getMealItems().size()).append('}');
        }
        json.append("]}");
        HistoryHandler.respond(exchange, 200, json.toString());
    }
}
