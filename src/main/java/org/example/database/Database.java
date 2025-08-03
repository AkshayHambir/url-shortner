package org.example.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static Connection conn;

    public static void init() {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./data/urlshortener", "sa", "");
            Statement stmt = conn.createStatement();

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS urls (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    short_url VARCHAR(255) UNIQUE NOT NULL,
                    long_url TEXT NOT NULL,
                    user_id INT,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
            """);

        } catch (SQLException e) {
            logger.error("Error : {} - {}", e.getErrorCode(), e.getMessage());
            throw new RuntimeException("Failed to connect/init H2", e);
        }
    }

    public static void insertUrl(String shortUrl, String longUrl, Integer userId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO urls (short_url, long_url, user_id) VALUES (?, ?, ?)")) {
            ps.setString(1, shortUrl);
            ps.setString(2, longUrl);
            if (userId != null) ps.setInt(3, userId);
            else ps.setNull(3, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error : {} - {}", e.getErrorCode(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static boolean shortUrlExists(String shortUrl) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM urls WHERE short_url = ?")) {
            ps.setString(1, shortUrl);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.error("Error : {} - {}", e.getErrorCode(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String getLongUrl(String shortUrl) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT long_url FROM urls WHERE short_url = ?")) {
            ps.setString(1, shortUrl);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : null;
        } catch (SQLException e) {
            logger.error("Error : {} - {}", e.getErrorCode(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static boolean register(String username, String password) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (username, password) VALUES (?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("Error : {} - {}", e.getErrorCode(), e.getMessage());
            return false;
        }
    }

    public static Integer authenticate(String username, String password) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM users WHERE username = ? AND password = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("id") : null;
        } catch (SQLException e) {
            logger.error("Error : {} - {}", e.getErrorCode(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
