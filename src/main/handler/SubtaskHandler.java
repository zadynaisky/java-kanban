package main.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.exception.IntersectionException;
import main.exception.NotFoundException;
import main.model.Endpoint;
import main.model.Subtask;
import main.service.TaskManager;

import java.io.IOException;

import static main.model.Endpoint.*;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, httpExchange.getRequestMethod());
        switch (endpoint) {
            case GET_LIST -> getSubtasks(httpExchange);
            case GET -> getSubtask(httpExchange, getId(path));
            case POST -> postSubtask(httpExchange);
            case DELETE -> removeSubtask(httpExchange, getId(path));
            default -> sendMethodNotAllowed(httpExchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 3 && paths[1].equals("subtasks")) {
            switch (requestMethod) {
                case "GET" -> { return (paths.length == 2) ? GET_LIST : GET; }
                case "POST" -> { return POST; }
                case "DELETE" -> { return DELETE; }
            }
        }
        return UNKNOWN;
    }

    private void postSubtask(HttpExchange httpExchange) throws IOException {
        var requestBody = new String(httpExchange.getRequestBody().readAllBytes());

        try {
            var subtask = gson.fromJson(requestBody, Subtask.class);
            if (taskManager.getSubtaskMap().containsKey(subtask.getId())) {
                taskManager.updateSubtask(subtask);
                sendText(httpExchange, "Subtask was updated", 201);
            }
            else{
                taskManager.addSubtask(subtask);
                sendText(httpExchange, "Subtask was created", 201);
            }
        } catch (IntersectionException e) {
            sendHasIntersections(httpExchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(httpExchange);
        }
    }

    private void getSubtask(HttpExchange httpExchange, int id) throws IOException {
        try {
            sendText(httpExchange, gson.toJson(taskManager.getSubtask(id)), 200);
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
        }
    }

    private void getSubtasks(HttpExchange httpExchange) throws IOException {
        String jsonSubtasks = gson.toJson(taskManager.getAllSubtasks());
        sendText(httpExchange, jsonSubtasks, 200);
    }

    private void removeSubtask(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getSubtaskMap().containsKey(id)) {
            taskManager.removeSubtaskById(id);
            sendText(httpExchange, "Subtask was removed", 200);
        }
        else
            sendNotFound(httpExchange);
    }
}
