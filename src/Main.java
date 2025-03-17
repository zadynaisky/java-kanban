import model.Epic;
import model.Subtask;
import model.Task;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import static model.Status.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        //Здесь вообще все можно удалить, т.к. все это теперь есть в тестах
        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();
        long firstTaskId = taskManager.addTask(new Task("First task title", "First task description"));
        long secondTaskId = taskManager.addTask(new Task("Second task title", "Second task description"));

        long firstEpicId = taskManager.addEpic(new Epic("First epic title", "First epic description"));
        long firstEpicFirstSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: First Subtask title",
                "Epic 1: First subtask description", firstEpicId));
        long firstEpicSecondSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: Second Subtask title",
                "Epic 1: Second subtask description", firstEpicId));

        long secondEpicId = taskManager.addEpic(new Epic("Second epic title", "Second epic description"));
        long secondEpicFirstSubtaskId = taskManager.addSubtask(new Subtask("Epic 2: First Subtask title",
                "Epic 2: First subtask description", secondEpicId));

        taskManager.printAllTaskAndEpics();

        taskManager.updateSubtask(new Subtask(firstEpicFirstSubtaskId, "Epic 1: First Subtask title",
                "Epic 1: First subtask description", IN_PROGRESS, firstEpicId));
        taskManager.updateSubtask(new Subtask(firstEpicSecondSubtaskId, "Epic 1: Second Subtask title",
                "Epic 1: First subtask description", DONE, firstEpicId));
        taskManager.updateSubtask(new Subtask(secondEpicFirstSubtaskId, "Epic 2: First Subtask title",
                "Epic 2: First subtask description", DONE, secondEpicId));
        taskManager.printAllTaskAndEpics();

        taskManager.removeSubtaskById(secondEpicFirstSubtaskId);
        taskManager.printAllTaskAndEpics();

        taskManager.removeEpicById(firstEpicId);
        taskManager.printAllTaskAndEpics();
    }
}