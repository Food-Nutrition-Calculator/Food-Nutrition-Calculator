package nutritrack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import nutritrack.web.FoodHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Dedicated Independent Web Server for Member 1 (Food Management).
 * Serves Member 1's HTML/CSS Food Catalog (web/foods.html) and REST API (/api/foods).
 * Allows Member 1 to run and test their module without depending on other members.
 *
 * Owned and maintained by: Member 1 (Food Management)
 */
public class Member1FoodServer {

    private static final int PORT = 8080;
    private static final String WEB_ROOT = "web";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // 1. Mount Member 1's Food REST API
        server.createContext("/api/foods", new FoodHandler());

        // 2. Mount Member 1's Static Web Handler (HTML, CSS, JS)
        server.createContext("/", new StaticFileHandler());

        server.start();

        System.out.println("====================================================================");
        System.out.println("  MEMBER 1 FOOD MANAGEMENT WEB APPLICATION IS RUNNING!              ");
        System.out.println("  Open your browser and visit: http://localhost:" + PORT + "/foods.html  ");
        System.out.println("====================================================================");
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/foods.html";
            }

            File file = new File(WEB_ROOT + path);

            if (!file.exists() || file.isDirectory()) {
                String response = "404 Not Found: " + path;
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
            return "text/plain";
        }
    }
}
