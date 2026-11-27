package nutritrack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Standalone Web Server for Member 2 — Meal Management.
 * Serves meals.html on Port 8080.
 */
public class Member2MealServer {

    private static final int PORT = 8080;
    private static final String WEB_DIR = "web";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                String path = exchange.getRequestURI().getPath();
                if (path.equals("/") || path.isEmpty()) {
                    path = "/meals.html";
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
                    String notFound = "<h2>404 Not Found</h2><p><a href='/meals.html'>Go to Meal Builder</a></p>";
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
        System.out.println("   NutriTrack - Member 2 Meal Server STARTED      ");
        System.out.println("   URL: http://localhost:" + PORT + "/meals.html  ");
        System.out.println("==================================================");
    }
}