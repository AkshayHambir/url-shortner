package org.example.service;

import org.example.database.Database;

import java.util.UUID;

public class UrlService {
    public static String shorten(String longUrl, Integer userId, String customShortUrl) {
        String shortUrl = (customShortUrl != null && !customShortUrl.isBlank())
                ? customShortUrl
                : generateCode();

        if (Database.shortUrlExists(shortUrl)) {
            throw new IllegalArgumentException("Short URL already exists");
        }

        Database.insertUrl(shortUrl, longUrl, userId);
        return "http://localhost:8080/r/" + shortUrl;
    }

    public static String resolve(String shortUrl) {
        return Database.getLongUrl(shortUrl);
    }

    private static String generateCode() {
        return UUID.randomUUID().toString().substring(0, 7);
    }
}
