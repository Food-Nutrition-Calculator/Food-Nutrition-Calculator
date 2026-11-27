package nutritrack.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connectivity using JDBC and MySQL.
 * Implements a Singleton-style connection provider with lifecycle management.
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
    private static String user = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";

    private static Connection connection = null;

    // Prevent direct instantiation (Utility / Singleton Pattern)
    private DBConnection() { }

    /**
     * Retrieves an active JDBC Connection to the MySQL database.
     * Reopens the connection if closed or not yet created.
     *
     * @return java.sql.Connection object
     * @throws SQLException if a database access error occurs
     */
    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Ensure MySQL JDBC Driver is registered
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, user, password);
                System.out.println("[DBConnection] Connected successfully to database: " + DB_NAME);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection ERROR] MySQL JDBC Driver not found. Ensure mysql-connector-j jar is on classpath.");
            throw new SQLException("MySQL JDBC Driver missing: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DBConnection ERROR] Could not connect to MySQL at " + URL);
            System.err.println("[DBConnection ERROR] Message: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    /**
     * Safely closes the active database connection.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("[DBConnection] Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("[DBConnection ERROR] Error while closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
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
        closeConnection(); // Re-establish with new credentials on next call
    }
}
