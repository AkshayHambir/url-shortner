package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.service.UrlService;
import org.json.JSONObject;

import java.io.IOException;

public class ShortenHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes());
        JSONObject json = new JSONObject(body);

        String longUrl = json.getString("longUrl");
        String custom = json.optString("shortUrl", null);
        Integer userId = json.has("userId") ? json.getInt("userId") : null;

        try {
            String shortened = UrlService.shorten(longUrl, userId, custom);
            byte[] res = ("{\"shortUrl\":\"" + shortened + "\"}").getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, res.length);
            exchange.getResponseBody().write(res);
        } catch (IllegalArgumentException e) {
            String msg = "{\"error\":\"" + e.getMessage() + "\"}";
            exchange.sendResponseHeaders(400, msg.length());
            exchange.getResponseBody().write(msg.getBytes());
        } finally {
            exchange.getResponseBody().close();
        }
    }
}
