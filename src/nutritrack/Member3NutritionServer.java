package nutritrack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import nutritrack.model.Nutrition;
import nutritrack.service.NutritionService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Standalone Web Server for Member 3 (Nutrition Calculation & Dashboard).
 * Port: 8080
 */
public class Member3NutritionServer {

    private static final int PORT = 8080;
    private static final String WEB_DIR = "web";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        NutritionService nutritionService = new NutritionService();

        // 1. Core API Endpoint for Member 3
        server.createContext("/api/nutrition", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                String query = exchange.getRequestURI().getQuery();
                String dateStr = (query != null && query.contains("date=")) ? query.split("date=")[1].split("&")[0] : LocalDate.now().toString();

                // Compute nutrition using Member 3's calculation engine
                Nutrition breakfast = Nutrition.scale(78.0, 6.3, 0.6, 5.3, 1.0, 2.0); // 2 eggs
                Nutrition lunch     = Nutrition.scale(165.0, 31.0, 0.0, 3.6, 100.0, 200.0); // 200g chicken
                Nutrition dailyTotal = nutritionService.calculateDailyTotal(List.of(breakfast, lunch));

                Map<String, Double> pcts = nutritionService.calculateMacronutrientPercentages(dailyTotal);

                String json = String.format(
                    "{\"date\":\"%s\",\"totalCalories\":%.2f,\"totalProtein\":%.2f,\"totalCarbs\":%.2f,\"totalFats\":%.2f," +
                    "\"proteinPct\":%.1f,\"carbsPct\":%.1f,\"fatsPct\":%.1f,\"mealCount\":2," +
                    "\"meals\":[" +
                    "{\"mealId\":1,\"mealName\":\"Breakfast (2 Boiled Eggs)\",\"calories\":%.2f,\"protein\":%.2f,\"carbs\":%.2f,\"fats\":%.2f,\"itemCount\":1}," +
                    "{\"mealId\":2,\"mealName\":\"Lunch (200g Grilled Chicken)\",\"calories\":%.2f,\"protein\":%.2f,\"carbs\":%.2f,\"fats\":%.2f,\"itemCount\":1}" +
                    "]}",
                    dateStr,
                    dailyTotal.getTotalCalories(), dailyTotal.getTotalProtein(), dailyTotal.getTotalCarbs(), dailyTotal.getTotalFats(),
                    pcts.get("PROTEIN_PCT"), pcts.get("CARBS_PCT"), pcts.get("FATS_PCT"),
                    breakfast.getTotalCalories(), breakfast.getTotalProtein(), breakfast.getTotalCarbs(), breakfast.getTotalFats(),
                    lunch.getTotalCalories(), lunch.getTotalProtein(), lunch.getTotalCarbs(), lunch.getTotalFats()
                );

                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
        });

        // 2. Static Web Page Handler
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                String path = exchange.getRequestURI().getPath();
                if (path.equals("/") || path.isEmpty()) {
                    path = "/nutrition.html";
                }

                String filePath = WEB_DIR + path;
                try {
                    byte[] bytes = Files.readAllBytes(Paths.get(filePath));
                    String contentType = path.endsWith(".html") ? "text/html; charset=UTF-8" : "text/plain";
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                } catch (IOException e) {
                    String notFound = "<h2>404 Not Found</h2><p><a href='/nutrition.html'>Go to Nutrition Dashboard</a></p>";
                    byte[] bytes = notFound.getBytes();
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(404, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                }
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("==================================================");
        System.out.println("  NutriTrack - Member 3 Nutrition Server STARTED  ");
        System.out.println("  Dashboard URL: http://localhost:" + PORT + "/nutrition.html");
        System.out.println("  API Endpoint : http://localhost:" + PORT + "/api/nutrition");
        System.out.println("==================================================");
    }
}