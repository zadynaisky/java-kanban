package main.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.HttpTaskServer;
import main.model.Epic;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = GsonFactory.getGson();
    HttpClient client = HttpClient.newHttpClient();

    EpicHandlerTest() throws IOException {
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
    public void addEpicReturns200() throws IOException, InterruptedException {
        Epic task = new Epic("First epic title", "First epic description");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = taskManager.getAllEpics();

        assertNotNull(epicsFromManager, "Empty list of epics");
        assertEquals(1, epicsFromManager.size(), "Incorrect number of epics");
        assertEquals("First epic title", epicsFromManager.get(0).getTitle(), "Incorrect epic title");
        assertEquals("First epic description", epicsFromManager.get(0).getDescription(), "Incorrect epic description");
    }

    @Test
    public void getEpicReturns200() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("First epic title", "First epic description"));
        Epic secondEpic = new Epic("Second epic title", "Second epic description");
        long secondEpicId = taskManager.addEpic(secondEpic);
        taskManager.addEpic(new Epic("Third epic title", "Third epic description"));

        List<Epic> epicsFromManager = taskManager.getAllEpics();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + secondEpicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(secondEpic, epicFromResponse, "Tasks are not equal");
    }

    @Test
    public void getEpicReturns404() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("First task title", "First task description"));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + 1_000_000))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void getEpicsReturns200() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("First task title", "First task description"));
        taskManager.addEpic(new Epic("Second task title", "Second task description"));
        taskManager.addEpic(new Epic("Third task title", "Third task description"));

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {}.getType());

        Set<Epic> fromManager = new HashSet<>(taskManager.getAllEpics());
        Set<Epic> fromResponse = new HashSet<>(tasksFromResponse);

        assertEquals(200, response.statusCode());
        assertEquals(fromManager.size(), fromResponse.size(), "Incorrect number of tasks");
        assertTrue(fromManager.containsAll(fromResponse), "Tasks in the manager and in the API do not match");
        assertTrue(fromResponse.containsAll(fromManager), "Tasks in the manager and in the API do not match ");
    }

    @Test
    public void deleteEpicDeletesTheEpicAndReturnsCode200() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("First task title", "First task description"));
        long id = taskManager.addEpic(new Epic("Second task title", "Second task description"));
        taskManager.addEpic(new Epic("Third task title", "Third task description"));
        assertEquals(3, taskManager.getAllEpics().size(), "Incorrect number of tasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(2, taskManager.getAllEpics().size(), "Incorrect number of tasks");
    }
}