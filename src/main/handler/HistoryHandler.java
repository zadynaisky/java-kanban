package main.handler;

import com.sun.net.httpserver.HttpExchange;
import main.model.Endpoint;
import main.service.TaskManager;

import java.io.IOException;

import static main.model.Endpoint.GET_LIST;
import static main.model.Endpoint.UNKNOWN;

public class HistoryHandler extends BaseHttpHandler{
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
        if (endpoint == GET_LIST)
            getHistory(httpExchange);
        else
            sendMethodNotAllowed(httpExchange);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 2 && paths[1].equals("history") && requestMethod.equals("GET"))
            return GET_LIST;
        return UNKNOWN;
    }

    private void getHistory(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, gson.toJson(taskManager.getHistory()), 200);
    }
}
