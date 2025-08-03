package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.service.UrlService;

import java.io.IOException;

public class RedirectHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String code = path.substring("/r/".length());

        String longUrl = UrlService.resolve(code);
        if (longUrl != null) {
            exchange.getResponseHeaders().add("Location", longUrl);
            exchange.sendResponseHeaders(302, -1);
        } else {
            String msg = "URL not found";
            exchange.sendResponseHeaders(404, msg.length());
            exchange.getResponseBody().write(msg.getBytes());
            exchange.getResponseBody().close();
        }
    }
}
