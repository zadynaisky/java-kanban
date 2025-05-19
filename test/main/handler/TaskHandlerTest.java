package main.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.HttpTaskServer;
import main.model.Epic;
import main.model.Subtask;
import main.service.InMemoryTaskManager;
import main.service.TaskManager;
import main.utils.GsonFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest {

    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = GsonFactory.getGson();
    HttpClient client = HttpClient.newHttpClient();

    TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void addSubtaskReturns200() throws IOException, InterruptedException {
        var epicId = taskManager.addEpic(new Epic("First epic title", "First Epic Description"));
        Subtask task = new Subtask("First task title", "First task description", epicId, LocalDateTime.now(), 1440);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Empty list of tasks");
        assertEquals(1, subtasksFromManager.size(), "Incorrect number of tasks");
        assertEquals("First task title", subtasksFromManager.get(0).getTitle(), "Incorrect task title");
        assertEquals("First task description", subtasksFromManager.get(0).getDescription(), "Incorrect task description");
    }

    @Test
    public void getSubtaskReturns200() throws IOException, InterruptedException {
        var epicId = taskManager.addEpic(new Epic("First epic title", "First Epic Description"));
        taskManager.addSubtask(new Subtask("First task title", "First task description", epicId, LocalDateTime.now(), 1440));
        Subtask subtask2 = new Subtask("Second task title", "Second task description", epicId, LocalDateTime.now().plusDays(1), 1440);
        long secondSubtaskId = taskManager.addSubtask(subtask2);
        taskManager.addSubtask(new Subtask("Third task title", "Third task description", epicId, LocalDateTime.now().plusDays(2), 1440));

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + secondSubtaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask taskFromResponse = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(subtask2, taskFromResponse, "Tasks are not equal");
    }

    @Test
    public void getSubtaskReturns404() throws IOException, InterruptedException {
        var epicId = taskManager.addEpic(new Epic("First epic title", "First Epic Description"));
        taskManager.addSubtask(new Subtask("First task title", "First task description", epicId, LocalDateTime.now(), 1440));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + 1_000_000))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void getSubtasksReturns200() throws IOException, InterruptedException {
        var epicId = taskManager.addEpic(new Epic("First epic title", "First Epic Description"));
        taskManager.addSubtask(new Subtask("First task title", "First task description", epicId, LocalDateTime.now(), 1440));
        taskManager.addSubtask(new Subtask("Second task title", "Second task description", epicId, LocalDateTime.now().plusDays(1), 1440));
        taskManager.addSubtask(new Subtask("Third task title", "Third task description", epicId, LocalDateTime.now().plusDays(2), 1440));

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {}.getType());

        Set<Subtask> fromManager = new HashSet<>(taskManager.getAllSubtasks());
        Set<Subtask> fromResponse = new HashSet<>(tasksFromResponse);

        assertEquals(200, response.statusCode());
        assertEquals(fromManager.size(), fromResponse.size(), "Incorrect number of tasks");
        assertTrue(fromManager.containsAll(fromResponse), "Tasks in the manager and in the API do not match");
        assertTrue(fromResponse.containsAll(fromManager), "Tasks in the manager and in the API do not match ");
    }

    @Test
    public void deleteSubtaskDeletesTheSubtaskAndReturnsCode200() throws IOException, InterruptedException {
        var epicId = taskManager.addEpic(new Epic("First epic title", "First Epic Description"));
        taskManager.addSubtask(new Subtask("First task title", "First task description", epicId, LocalDateTime.now(), 1440));
        Subtask subtask2 = new Subtask("Second task title", "Second task description", epicId, LocalDateTime.now().plusDays(1), 1440);
        long secondSubtaskId = taskManager.addSubtask(subtask2);
        taskManager.addSubtask(new Subtask("Third task title", "Third task description", epicId, LocalDateTime.now().plusDays(2), 1440));

        assertEquals(3, taskManager.getAllSubtasks().size(), "Incorrect number of tasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + secondSubtaskId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(2, taskManager.getAllSubtasks().size(), "Incorrect number of tasks");
    }
}