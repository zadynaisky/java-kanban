package main;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.service.InMemoryTaskManager;
import main.service.Managers;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();
        long firstTaskId = taskManager.addTask(new Task("First task title", "First task description", LocalDateTime.now(), 1440));
        long secondTaskId = taskManager.addTask(new Task("Second task title", "Second task description", LocalDateTime.now(), 1440));
        long firstEpicId = taskManager.addEpic(new Epic("First epic title", "First epic description"));
        long firstEpicFirstSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: First Subtask title",
                "Epic 1: First subtask description", firstEpicId, LocalDateTime.now(), 1440));
        long firstEpicSecondSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: Second Subtask title",
                "Epic 1: Second subtask description", firstEpicId, LocalDateTime.now(), 1440));
        long firstEpicThirdSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: Third Subtask title",
                "Epic 1: Third subtask description", firstEpicId, LocalDateTime.now(), 1440));
        long secondEpicId = taskManager.addEpic(new Epic("Second epic title", "Second epic description"));

        taskManager.getTask(firstTaskId);
        taskManager.getTask(secondTaskId);
        taskManager.getEpic(firstEpicId);
        taskManager.getSubtask(firstEpicFirstSubtaskId);
        taskManager.getSubtask(firstEpicSecondSubtaskId);
        taskManager.getSubtask(firstEpicThirdSubtaskId);
        taskManager.getEpic(secondEpicId);

        taskManager.getHistory().stream().forEach(System.out::println);
        System.out.println("Shuffling tasks in history...");
        taskManager.getSubtask(firstEpicFirstSubtaskId);
        taskManager.getSubtask(firstEpicSecondSubtaskId);
        taskManager.getSubtask(firstEpicThirdSubtaskId);
        taskManager.getHistory().stream().forEach(System.out::println);
        System.out.println("Shuffling tasks in history...");
        taskManager.getEpic(firstEpicId);
        taskManager.getEpic(secondEpicId);
        taskManager.getHistory().stream().forEach(System.out::println);
        System.out.println("Shuffling tasks in history...");
        taskManager.getTask(firstTaskId);
        taskManager.getTask(secondTaskId);
        taskManager.getHistory().stream().forEach(System.out::println);
        System.out.println("Shuffling tasks in history...");
        taskManager.removeEpicById(firstEpicId);
        taskManager.getHistory().stream().forEach(System.out::println);
    }
}