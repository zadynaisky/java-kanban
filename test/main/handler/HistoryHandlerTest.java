package main.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.HttpTaskServer;
import main.model.Task;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = GsonFactory.getGson();
    HttpClient client = HttpClient.newHttpClient();

    HistoryHandlerTest() throws IOException {
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
    public void getHistoryReturns200AndHistoryList() throws IOException, InterruptedException {
        final int tasksNum = 1_000;
        for (int i = 1; i <= tasksNum; i++) {
            Task task = new Task("Title" + i, "Description" + i, LocalDateTime.now().plusDays(i + 2), 1440);
            var taskId = taskManager.addTask(task);
            taskManager.getTask(taskId);
        }

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(tasksNum, tasksFromResponse.size());
    }
}