package nutritrack.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nutritrack.model.FoodItem;
import nutritrack.service.FoodService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP REST API Handler for Food Management (/api/foods).
 * Processes HTTP GET (search/sort/view), POST (add), and DELETE requests.
 * Uses standard Java without requiring external web frameworks.
 *
 * Owned and maintained by: Member 1 (Food Management - Web Edition)
 */
public class FoodHandler implements HttpHandler {

    private final FoodService foodService;

    public FoodHandler() {
        this.foodService = new FoodService();
    }

    public FoodHandler(FoodService foodService) {
        this.foodService = (foodService != null) ? foodService : new FoodService();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        // Enable CORS (Cross-Origin Resource Sharing)
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if (method.equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, query);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, query);
                    break;
                default:
                    sendJsonResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                    break;
            }
        } catch (Exception e) {
            System.err.println("[FoodHandler ERROR] " + e.getMessage());
            sendJsonResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Handles GET /api/foods?search=...&sortBy=...&asc=...
     */
    private void handleGet(HttpExchange exchange, String query) throws IOException {
        Map<String, String> queryParams = parseQueryParams(query);
        String search = queryParams.getOrDefault("search", "");
        String sortBy = queryParams.getOrDefault("sortBy", "NAME");
        boolean ascending = Boolean.parseBoolean(queryParams.getOrDefault("asc", "true"));

        List<FoodItem> foods = foodService.searchFood(search);
        List<FoodItem> sortedFoods = foodService.getSortedFoods(foods, sortBy, ascending);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < sortedFoods.size(); i++) {
            json.append(foodToJson(sortedFoods.get(i)));
            if (i < sortedFoods.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        sendJsonResponse(exchange, 200, json.toString());
    }

    /**
     * Handles POST /api/foods (Creates a new FoodItem from JSON or form payload)
     */
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            body.append(line);
        }

        String rawData = body.toString();
        Map<String, String> params = parseBodyParams(rawData);

        try {
            String name = params.get("foodName");
            String unit = params.getOrDefault("servingUnit", "grams");
            double servingSize = Double.parseDouble(params.getOrDefault("defaultServingSize", "100.0"));
            double calories = Double.parseDouble(params.getOrDefault("calories", "0.0"));
            double protein = Double.parseDouble(params.getOrDefault("protein", "0.0"));
            double carbs = Double.parseDouble(params.getOrDefault("carbohydrates", "0.0"));
            double fats = Double.parseDouble(params.getOrDefault("fats", "0.0"));

            FoodItem food = new FoodItem(name, unit, servingSize, calories, protein, carbs, fats);
            boolean success = foodService.addFood(food);

            if (success) {
                String responseJson = String.format("{\"success\":true,\"message\":\"Food item added successfully\",\"foodId\":%d}", food.getFoodId());
                sendJsonResponse(exchange, 201, responseJson);
            } else {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Failed to add food. Name may already exist or database is offline.\"}");
            }
        } catch (IllegalArgumentException e) {
            sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Handles DELETE /api/foods?id=...
     */
    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        Map<String, String> queryParams = parseQueryParams(query);
        String idStr = queryParams.get("id");

        if (idStr == null || idStr.isEmpty()) {
            sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing food id parameter\"}");
            return;
        }

        try {
            int foodId = Integer.parseInt(idStr);
            boolean deleted = foodService.deleteFood(foodId);
            if (deleted) {
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Food item deleted successfully\"}");
            } else {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Cannot delete food item. It may be linked to saved meals.\"}");
            }
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Invalid food id\"}");
        }
    }

    // --- Helper Serialization & Parsing Methods ---

    private String foodToJson(FoodItem f) {
        return String.format(
            "{\"foodId\":%d,\"foodName\":\"%s\",\"servingUnit\":\"%s\",\"defaultServingSize\":%.2f,"
            + "\"calories\":%.2f,\"protein\":%.2f,\"carbohydrates\":%.2f,\"fats\":%.2f}",
            f.getFoodId(),
            escapeJson(f.getFoodName()),
            escapeJson(f.getServingUnit()),
            f.getDefaultServingSize(),
            f.getCalories(),
            f.getProtein(),
            f.getCarbohydrates(),
            f.getFats()
        );
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;

        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                map.put(urlDecode(entry[0]), urlDecode(entry[1]));
            } else if (entry.length == 1) {
                map.put(urlDecode(entry[0]), "");
            }
        }
        return map;
    }

    private Map<String, String> parseBodyParams(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.trim().isEmpty()) return map;

        String trimmed = body.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            // Simple JSON key-value extraction without external library
            String content = trimmed.substring(1, trimmed.length() - 1);
            String[] pairs = content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim().replace("\"", "");
                    String val = kv[1].trim().replace("\"", "");
                    map.put(key, val);
                }
            }
        } else {
            // URL Encoded form data: name=val&name2=val2
            map = parseQueryParams(trimmed);
        }
        return map;
    }

    private String urlDecode(String val) {
        try {
            return URLDecoder.decode(val, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return val;
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
