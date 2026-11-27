package nutritrack.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nutritrack.database.HistoryDAO;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/** Read and delete persisted meal history. */
public class HistoryHandler implements HttpHandler {
    private final HistoryDAO dao = new HistoryDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        String method = exchange.getRequestMethod();
        if ("DELETE".equalsIgnoreCase(method)) {
            String query = exchange.getRequestURI().getQuery();
            int id;
            try {
                id = Integer.parseInt(query == null ? "" : query.replaceFirst("^.*(?:^|&)id=", ""));
            } catch (NumberFormatException e) {
                respond(exchange, 400, "{\"success\":false,\"message\":\"A valid meal ID is required.\"}");
                return;
            }
            boolean deleted = dao.deleteMeal(id);
            respond(exchange, deleted ? 200 : 404, "{\"success\":" + deleted + "}");
            return;
        }
        if (!"GET".equalsIgnoreCase(method)) {
            respond(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        String query = exchange.getRequestURI().getQuery();
        String value = queryValue(query, "date");
        LocalDate date;
        try { date = value.isEmpty() ? LocalDate.now() : LocalDate.parse(value); }
        catch (RuntimeException e) { respond(exchange, 400, "{\"error\":\"Date must use YYYY-MM-DD.\"}"); return; }

        List<Meal> meals = dao.getMealsWithItemsByDate(date);
        StringBuilder json = new StringBuilder("{\"date\":\"").append(date).append("\",\"meals\":[");
        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.get(i);
            Nutrition n = meal.calculateTotalNutrition();
            if (i > 0) json.append(',');
            json.append("{\"id\":").append(meal.getMealId())
                .append(",\"name\":\"").append(escape(meal.getMealName())).append("\"")
                .append(",\"date\":\"").append(meal.getMealDate()).append("\"")
                .append(",\"time\":\"").append(meal.getMealTime()).append("\"")
                .append(",\"cal\":").append(n.getTotalCalories())
                .append(",\"p\":").append(n.getTotalProtein())
                .append(",\"c\":").append(n.getTotalCarbs())
                .append(",\"f\":").append(n.getTotalFats())
                .append(",\"items\":[");
            for (int j = 0; j < meal.getMealItems().size(); j++) {
                MealItem item = meal.getMealItems().get(j);
                if (j > 0) json.append(',');
                json.append("{\"name\":\"").append(escape(item.getFoodItem().getFoodName()))
                    .append("\",\"quantity\":").append(item.getQuantity())
                    .append(",\"unit\":\"").append(escape(item.getFoodItem().getServingUnit())).append("\"}");
            }
            json.append("]}");
        }
        json.append("]}");
        respond(exchange, 200, json.toString());
    }

    static String queryValue(String query, String key) {
        if (query == null) return "";
        for (String part : query.split("&")) {
            String[] pair = part.split("=", 2);
            if (pair.length == 2 && pair[0].equals(key)) return pair[1];
        }
        return "";
    }

    static String escape(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"); }
    static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var out = exchange.getResponseBody()) { out.write(bytes); }
    }
}
