package nutritrack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import nutritrack.web.FoodHandler;
import nutritrack.web.MealHandler;
import nutritrack.web.HistoryHandler;
import nutritrack.web.NutritionHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.BindException;
import java.nio.file.Files;
import java.util.concurrent.Executors;

/**
 * Built-in Lightweight Java Web Server for NutriTrack.
 * Serves static HTML/CSS/JS files and mounts REST API endpoints.
 * Requires ZERO third-party web frameworks or Tomcat installation.
 *
 * Owned and maintained by: Member 4 (Integration) & Team
 */
public class NutriTrackServer {

    private static final int PORT = 8081;
    private static final String WEB_ROOT = "web";

    public static void main(String[] args) throws IOException {
        // Keep this stable: the UI calls its API on the same origin, and the
        // startup instructions advertise this port to the browser.
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (BindException e) {
            System.out.println("NutriTrackServer is already running or port " + PORT + " is occupied.");
            System.out.println("Use the existing server at http://localhost:" + PORT + "/, or stop it before restarting.");
            return;
        }

        // Mount API Endpoints
        server.createContext("/api/foods", new FoodHandler());
        server.createContext("/api/meals", new MealHandler());
        server.createContext("/api/history", new HistoryHandler());
        server.createContext("/api/nutrition", new NutritionHandler());

        // Mount Static File Handler for HTML, CSS, JavaScript
        server.createContext("/", new StaticFileHandler());

        // Use multi-threaded executor for handling concurrent browser requests
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("Server started at: http://localhost:" + server.getAddress().getPort());

        System.out.println("====================================================================");
        System.out.println("  NUTRITRACK WEB APPLICATION IS RUNNING!                            ");
        System.out.println("  Open your browser and visit: http://localhost:" + PORT + "/             ");
        System.out.println("====================================================================");
    }

    /**
     * Serves static HTML, CSS, and JS files from the 'web/' directory.
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            File file = new File(WEB_ROOT + path);

            if (!file.exists() || file.isDirectory()) {
                String response = "404 (Not Found): The requested resource does not exist.";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            String contentType = getMimeType(file.getName());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = exchange.getResponseBody()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }

        private String getMimeType(String filename) {
            if (filename.endsWith(".html")) return "text/html; charset=UTF-8";
            if (filename.endsWith(".css")) return "text/css; charset=UTF-8";
            if (filename.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (filename.endsWith(".json")) return "application/json; charset=UTF-8";
            if (filename.endsWith(".png")) return "image/png";
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
            if (filename.endsWith(".svg")) return "image/svg+xml";
            return "text/plain";
        }
    }
}
