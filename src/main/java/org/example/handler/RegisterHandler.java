package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.database.Database;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes());
        JSONObject json = new JSONObject(body);
        String username = json.getString("username");
        String password = json.getString("password");

        boolean success = Database.register(username, password);
        if (success) {
            String res = "{\"message\":\"Registration successful\"}";
            exchange.sendResponseHeaders(201, res.length());
            exchange.getResponseBody().write(res.getBytes());
        } else {
            String res = "{\"error\":\"Username already taken\"}";
            exchange.sendResponseHeaders(409, res.length());
            exchange.getResponseBody().write(res.getBytes());
        }

        exchange.getResponseBody().close();
    }
}
