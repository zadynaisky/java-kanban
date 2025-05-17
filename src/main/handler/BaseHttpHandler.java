package main.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.utils.GsonFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final Gson gson = GsonFactory.getGson();

    protected void sendText(HttpExchange httpExchange, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(code, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Not Found", 404);
    }

    protected void sendHasIntersections(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Task interval intersect with existed task", 406);
    }

    protected void sendMethodNotAllowed(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Method Not Allowed", 405);
    }

    protected void sendBadRequest(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Bad Request", 400);
    }

    protected int getId(String path) throws IOException {
        String[] arrayPath = path.split("/");
        return Integer.parseInt(arrayPath[2]);
    }
}
