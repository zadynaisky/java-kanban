package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

import static model.Status.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class TaskManager {
    private final Map<Long, Epic> epics = new HashMap<>();
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Subtask> subtasks = new HashMap<>();
    private static long nextId = 1;

    public long getNextId() {return nextId++;}

    public long addEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public Epic getEpic(long id) { return epics.get(id); }
    public List<Epic> getAllEpics() { return epics.values().stream().collect(toList()); }

    public List<Subtask> getEpicSubtasks(long id) {
        return subtasks
                .values()
                .stream()
                .filter(x -> x.getEpicId() == id)
                .collect(toList());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        calculateAndSetEpicStatus(epic.getId());
    }

    public void removeEpicById(long id) {
        epics.remove(id);
        subtasks.entrySet().removeIf(entry -> entry.getValue().getEpicId() == id);
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public long addTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public Task getTask(long id) { return tasks.get(id); }
    public List<Task> getAllTasks() { return tasks.values().stream().collect(toList()); }
    public void updateTask(Task task) {tasks.put(task.getId(), task);}
    public void removeTaskById(long id) { tasks.remove(id); }
    public void removeAllTasks() { tasks.clear(); }

    public long addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())){
            subtask.setId(getNextId());
            subtasks.put(subtask.getId(), subtask);
            calculateAndSetEpicStatus(subtask.getEpicId());
            return subtask.getId();
        }
        else{
            System.out.println("Subtask wasn't added. Couldn't find epic with id " + subtask.getEpicId());
            return -1;
        }
    }

    public Subtask getSubtask(long id) { return subtasks.get(id); }
    public List<Subtask> getAllSubtasks() { return subtasks.values().stream().collect(toList()); }

    public void updateSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())){
            System.out.println("Subtask wasn't updated. Couldn't find epic with id " + subtask.getEpicId());
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        calculateAndSetEpicStatus(subtask.getEpicId());
    }

    public void calculateAndSetEpicStatus(long id) {
        Set<Status> epicSubtasksStatuses = getEpicSubtasks(id)
                .stream()
                .map(x -> x.getStatus())
                .collect(toSet());

        if (epicSubtasksStatuses.size() == 0 || (epicSubtasksStatuses.size() == 1 && epicSubtasksStatuses.contains(NEW)))
            epics.get(id).setStatus(NEW);
        else if (epicSubtasksStatuses.size() == 1 && epicSubtasksStatuses.contains(DONE)) {
            epics.get(id).setStatus(DONE);
        }
        else
            epics.get(id).setStatus(IN_PROGRESS);

    }

    public void removeSubtaskById(long id) {
        var epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        calculateAndSetEpicStatus(epicId);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        epics.entrySet().stream().forEach(entry -> entry.getValue().setStatus(NEW));
    }

}
