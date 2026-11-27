package nutritrack.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nutritrack.database.FoodDAO;
import nutritrack.database.MealDAO;
import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.service.MealService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** API for creating persisted meals from catalog food IDs and quantities. */
public class MealHandler implements HttpHandler {
    private final FoodDAO foodDAO = new FoodDAO();
    private final MealDAO mealDAO = new MealDAO();
    private final MealService mealService = new MealService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String name = stringField(body, "mealName");
            String date = stringField(body, "mealDate");
            Meal meal = new Meal(name, date.isEmpty() ? LocalDate.now() : LocalDate.parse(date), LocalTime.now().withNano(0));
            Matcher itemMatcher = Pattern.compile("\\{[^{}]*\\}").matcher(body);
            while (itemMatcher.find()) {
                String item = itemMatcher.group();
                if (!item.contains("foodId") || !item.contains("quantity")) continue;
                int foodId = (int) numberField(item, "foodId");
                double quantity = numberField(item, "quantity");
                FoodItem food = foodDAO.findById(foodId);
                if (food == null) throw new IllegalArgumentException("Food item " + foodId + " was not found.");
                mealService.addFoodToMeal(meal, food, quantity);
            }
            if (meal.getMealItems().isEmpty()) throw new IllegalArgumentException("Add at least one food item.");
            if (!mealDAO.saveCompleteMeal(meal)) {
                respond(exchange, 500, "{\"success\":false,\"message\":\"Could not save meal. Check the database connection.\"}");
                return;
            }
            respond(exchange, 201, "{\"success\":true,\"mealId\":" + meal.getMealId() + "}");
        } catch (RuntimeException e) {
            String message = e.getMessage() == null ? "Invalid meal data." : e.getMessage().replace("\\", "\\\\").replace("\"", "\\\"");
            respond(exchange, 400, "{\"success\":false,\"message\":\"" + message + "\"}");
        }
    }

    private static String stringField(String json, String key) {
        Matcher m = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private static double numberField(String json, String key) {
        Matcher m = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(json);
        if (!m.find()) throw new IllegalArgumentException("Missing numeric field: " + key);
        return Double.parseDouble(m.group(1));
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var out = exchange.getResponseBody()) { out.write(bytes); }
    }
}
