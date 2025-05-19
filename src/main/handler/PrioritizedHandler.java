package main.handler;

import com.sun.net.httpserver.HttpExchange;
import main.model.Endpoint;
import main.service.TaskManager;

import java.io.IOException;

import static main.model.Endpoint.GET_LIST;
import static main.model.Endpoint.UNKNOWN;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoints(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
        switch (endpoint) {
            case GET_LIST -> getPrioritized(httpExchange);
            default -> sendMethodNotAllowed(httpExchange);
        }
    }

    private Endpoint getEndpoints(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 2 && paths[1].equals("prioritized") && requestMethod.equals("GET"))
            return GET_LIST;
        return UNKNOWN;
    }

    private void getPrioritized(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
    }
}
