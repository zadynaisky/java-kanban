package main.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.exception.IntersectionException;
import main.exception.NotFoundException;
import main.model.Endpoint;
import main.model.Task;
import main.service.TaskManager;

import java.io.IOException;

import static main.model.Endpoint.*;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, httpExchange.getRequestMethod());
        switch (endpoint) {
            case GET_LIST -> getTasks(httpExchange);
            case GET -> getTask(httpExchange, getId(path));
            case POST -> postTask(httpExchange);
            case DELETE -> removeTask(httpExchange, getId(path));
            default -> sendMethodNotAllowed(httpExchange);
        }

    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 3 && paths[1].equals("tasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return (paths.length == 2) ? GET_LIST : GET;
                }
                case "POST" -> {
                    return POST;
                }
                case "DELETE" -> {
                    return DELETE;
                }
            }
        }
        return UNKNOWN;
    }

    private void postTask(HttpExchange httpExchange) throws IOException {
        var requestBody = new String(httpExchange.getRequestBody().readAllBytes());
        try {
            var task = gson.fromJson(requestBody, Task.class);
            if (taskManager.containsTaskId(task.getId())) {
                taskManager.updateTask(task);
                sendText(httpExchange, "Task was updated", 201);
            } else {
                taskManager.addTask(task);
                sendText(httpExchange, "Task was created", 201);
            }
        } catch (IntersectionException e) {
            sendHasIntersections(httpExchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(httpExchange);
        } catch (Exception e) {
            sendBadRequest(httpExchange);
        }
    }

    private void getTask(HttpExchange httpExchange, long id) throws IOException {
        try {
            sendText(httpExchange, gson.toJson(taskManager.getTask(id)), 200);
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
        } catch (Exception e) {
            sendInternalServerError(httpExchange);
        }
    }

    private void getTasks(HttpExchange httpExchange) throws IOException {
        try {
            sendText(httpExchange, gson.toJson(taskManager.getAllTasks()), 200);
        } catch (Exception e) {
            sendInternalServerError(httpExchange);
        }
    }

    private void removeTask(HttpExchange httpExchange, long id) throws IOException {
        try {
            if (taskManager.containsTaskId(id)) {
                taskManager.removeTaskById(id);
                sendText(httpExchange, "Task was deleted", 200);
            } else
                sendNotFound(httpExchange);
        } catch (Exception e) {
            sendInternalServerError(httpExchange);
        }
    }
}
