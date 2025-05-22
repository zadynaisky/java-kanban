package main.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.exception.IntersectionException;
import main.exception.NotFoundException;
import main.model.Endpoint;
import main.model.Epic;
import main.service.TaskManager;

import java.io.IOException;

import static main.model.Endpoint.*;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, httpExchange.getRequestMethod());
        switch (endpoint) {
            case GET_LIST -> getEpics(httpExchange);
            case GET -> getEpic(httpExchange, getId(path));
            case GET_EPIC_SUBTASKS -> getEpicSubtasks(httpExchange, getId(path));
            case POST -> postEpic(httpExchange);
            case DELETE -> removeEpic(httpExchange, getId(path));
            default -> sendMethodNotAllowed(httpExchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 4 && paths[1].equals("epics")) {
            switch (requestMethod) {
                case "GET" -> {
                    if (paths.length == 2)
                        return GET_LIST;
                    else if (paths.length == 3)
                        return GET;
                    else
                        return GET_EPIC_SUBTASKS;
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

    private void postEpic(HttpExchange httpExchange) throws IOException {
        var requestBody = new String(httpExchange.getRequestBody().readAllBytes());
        try {
            var epic = gson.fromJson(requestBody, Epic.class);
            if (taskManager.containsEpicId(epic.getId())) {
                taskManager.updateEpic(epic);
                sendText(httpExchange, "Epic was updated", 201);
            } else {
                taskManager.addEpic(epic);
                sendText(httpExchange, "Epic was created", 201);
            }
        } catch (IntersectionException e) {
            sendHasIntersections(httpExchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(httpExchange);
        } catch (Exception e) {
            sendInternalServerError(httpExchange);
        }
    }

    private void getEpic(HttpExchange httpExchange, long id) throws IOException {
        try {
            sendText(httpExchange, gson.toJson(taskManager.getEpic(id)), 200);
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
        } catch (Exception e) {
            sendInternalServerError(httpExchange);
        }
    }

    private void getEpics(HttpExchange httpExchange) throws IOException {
        try {
            String jsonEpics = gson.toJson(taskManager.getAllEpics());
            sendText(httpExchange, jsonEpics, 200);
        } catch (Exception e) {
            sendInternalServerError(httpExchange);
        }
    }

    private void getEpicSubtasks(HttpExchange httpExchange, long id) throws IOException {
        try {
            if (taskManager.containsEpicId(id))
                sendText(httpExchange, gson.toJson(taskManager.getEpic(id).getSubtasks()), 200);
            else
                sendNotFound(httpExchange);
        } catch (Exception e) {
            sendInternalServerError(httpExchange);
        }
    }

    private void removeEpic(HttpExchange httpExchange, long id) throws IOException {
        try {
            if (taskManager.containsEpicId(id)) {
                taskManager.removeEpicById(id);
                sendText(httpExchange, "Epic was removed", 200);
            } else
                sendNotFound(httpExchange);
        } catch (Exception e) {
            sendInternalServerError(httpExchange);
        }
    }
}
