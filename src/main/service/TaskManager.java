package main.service;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;

import java.util.List;

public interface TaskManager {
    long addEpic(Epic epic);

    Epic getEpic(long id);

    List<Epic> getAllEpics();

    List<Subtask> getEpicSubtasks(long id);

    void updateEpic(Epic epic);

    void removeEpicById(long id);

    void removeAllEpics();

    long addTask(Task task);

    Task getTask(long id);

    List<Task> getAllTasks();

    void updateTask(Task task);

    void removeTaskById(long id);

    void removeAllTasks();

    long addSubtask(Subtask subtask);

    Subtask getSubtask(long id);

    List<Subtask> getAllSubtasks();

    void updateSubtask(Subtask subtask);

    void removeSubtaskById(long id);

    void removeAllSubtasks();

    List<Task> getHistory();

    void calculateAndSetEpicStartEndTimeAndDuration(Epic epic);
}