package main.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.HttpTaskServer;
import main.model.Task;
import main.service.InMemoryTaskManager;
import main.service.TaskManager;
import main.utils.GsonFactory;
import main.utils.TaskListTypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

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
    public void addTaskReturns200() throws IOException, InterruptedException {
        Task task = new Task("First task title", "First task description", LocalDateTime.now(), 1440);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Empty list of tasks");
        assertEquals(1, tasksFromManager.size(), "Incorrect number of tasks");
        assertEquals("First task title", tasksFromManager.get(0).getTitle(), "Incorrect task title");
        assertEquals("First task description", tasksFromManager.get(0).getDescription(), "Incorrect task description");
    }

    @Test
    public void getTaskReturns200() throws IOException, InterruptedException {
        taskManager.addTask(new Task("First task title", "First task description", LocalDateTime.now(), 1440));
        Task task2 =new Task("Second task title", "Second task description", LocalDateTime.now().plusDays(1), 1440);
        long secondTaskId = taskManager.addTask(task2);
        taskManager.addTask(new Task("Third task title", "Third task description", LocalDateTime.now().plusDays(2), 1440));

        List<Task> tasksFromManager = taskManager.getAllTasks();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + secondTaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task2, taskFromResponse, "Tasks are not equal");
    }

    @Test
    public void getTaskReturns404() throws IOException, InterruptedException {
        taskManager.addTask(new Task("First task title", "First task description", LocalDateTime.now(), 1440));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + 1_000_000))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void getTasksReturns200() throws IOException, InterruptedException {
        taskManager.addTask(new Task("First task title", "First task description", LocalDateTime.now(), 1440));
        taskManager.addTask(new Task("Second task title", "Second task description", LocalDateTime.now().plusDays(1), 1440));
        taskManager.addTask(new Task("Third task title", "Third task description", LocalDateTime.now().plusDays(2), 1440));

        List<Task> tasksFromManager = taskManager.getAllTasks();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(3, tasksFromManager.size(), "Incorrect number of tasks");
    }
}