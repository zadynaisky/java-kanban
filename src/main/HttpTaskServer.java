package main;

import com.sun.net.httpserver.HttpServer;
import main.handler.*;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.service.InMemoryTaskManager;
import main.service.Managers;
import main.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();
        long firstTaskId = taskManager.addTask(new Task("First task title", "First task description", LocalDateTime.now().plusDays(1), 1440));
        long secondTaskId = taskManager.addTask(new Task("Second task title", "Second task description", LocalDateTime.now().plusDays(2), 1440));
        long firstEpicId = taskManager.addEpic(new Epic("First epic title", "First epic description"));
        long firstEpicFirstSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: First Subtask title",
                "Epic 1: First subtask description", firstEpicId, LocalDateTime.now().plusDays(3), 1440));
        long firstEpicSecondSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: Second Subtask title",
                "Epic 1: Second subtask description", firstEpicId, LocalDateTime.now().plusDays(4), 1440));
        long firstEpicThirdSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: Third Subtask title",
                "Epic 1: Third subtask description", firstEpicId, LocalDateTime.now().plusDays(5), 1440));
        long secondEpicId = taskManager.addEpic(new Epic("Second epic title", "Second epic description"));

        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }
}
