
import model.*;
import service.TaskManager;

import static model.Status.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();


        long epicId = taskManager.addEpic(new Epic("Epic1", "Description"));
        long firstSubTaskId = taskManager.addSubtask(new Subtask("Subtask1", "Description", epicId));
        long secondSubTaskId = taskManager.addSubtask(new Subtask("Subtask2", "Description", epicId));
        long task = taskManager.addTask(new Task("Task", "Description"));

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.getSubtask(firstSubTaskId).setStatus(IN_PROGRESS);
        taskManager.updateSubtask(taskManager.getSubtask(firstSubTaskId));

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.getSubtask(firstSubTaskId).setStatus(DONE);
        taskManager.updateSubtask(taskManager.getSubtask(firstSubTaskId));
        taskManager.getSubtask(secondSubTaskId).setStatus(DONE);
        taskManager.updateSubtask(taskManager.getSubtask(secondSubTaskId));

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

    }
}