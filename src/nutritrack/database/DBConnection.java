package nutritrack.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connectivity using JDBC and MySQL.
 * Creates a fresh JDBC connection for each DAO operation. DAO callers own and
 * close each returned connection with try-with-resources.
 *
 * Owned and maintained by: Member 4 (Database, History & Integration)
 */
public class DBConnection {

    // Database connection parameters - customize according to your local MySQL setup
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "nutritrack_db";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    
    // Default MySQL credentials (can be overridden via environment variables or modified here)
    private static volatile String user = System.getenv("NUTRITRACK_DB_USER") != null
            ? System.getenv("NUTRITRACK_DB_USER")
            : (System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root");
    private static volatile String password = System.getenv("NUTRITRACK_DB_PASSWORD") != null
            ? System.getenv("NUTRITRACK_DB_PASSWORD")
            : (System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "");

    // Prevent direct instantiation (Utility / Singleton Pattern)
    private DBConnection() { }

    /**
     * Opens a JDBC Connection to the MySQL database. The caller must close it.
     *
     * @return java.sql.Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, user, password);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection ERROR] MySQL JDBC Driver not found. Ensure mysql-connector-j jar is on classpath.");
            throw new SQLException("MySQL JDBC Driver missing: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DBConnection ERROR] Could not connect to MySQL at " + URL);
            System.err.println("[DBConnection ERROR] Message: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Tests whether the database connection can be established.
     *
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Allows custom credentials configuration at runtime (e.g. from an initial setup dialog).
     */
    public static void setCredentials(String newUser, String newPassword) {
        user = newUser;
        password = newPassword;
    }
}
