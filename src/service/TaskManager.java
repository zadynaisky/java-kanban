package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static model.Status.*;

public class TaskManager {
    private final Map<Long, Epic> epics = new HashMap<>();
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Subtask> subtasks = new HashMap<>();
    private static long nextId = 1;

    public long addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public Epic getEpic(long id) { return epics.get(id); }

    public List<Epic> getAllEpics() { return epics.values().stream().sorted().collect(toList()); }

    public List<Subtask> getEpicSubtasks(long id) {
        return subtasks
                .values()
                .stream()
                .filter(x -> x.getEpicId() == id)
                .collect(toList());
    }

    public void updateEpic(Epic epic) { epics.put(epic.getId(), epic); }

    public void removeEpicById(long id) {
        epics.remove(id);
        subtasks.entrySet().removeIf(entry -> entry.getValue().getEpicId() == id);
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public long addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public Task getTask(long id) { return tasks.get(id); }
    public List<Task> getAllTasks() { return tasks.values().stream().sorted().collect(toList()); }
    public void updateTask(Task task) {tasks.put(task.getId(), task);}
    public void removeTaskById(long id) { tasks.remove(id); }
    public void removeAllTasks() { tasks.clear(); }

    public long addSubtask(Subtask subtask) {
        var epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)){
            subtask.setId(nextId++);
            subtasks.put(subtask.getId(), subtask);
            epics.get(epicId).addSubtask(subtask.getId());
            calculateAndSetEpicStatus(epics.get(epicId));
            return subtask.getId();
        }
        else{
            System.out.println("Subtask wasn't added. Couldn't find epic with id " + subtask.getEpicId());
            return -1;
        }
    }

    public Subtask getSubtask(long id) { return subtasks.get(id); }

    public List<Subtask> getAllSubtasks() { return subtasks.values().stream().sorted().collect(toList()); }

    public void updateSubtask(Subtask subtask) {
        var epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)){
            System.out.println("Subtask wasn't updated. Couldn't find epic with id " + epicId);
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        var epic = epics.get(epicId);
        epic.addSubtask(subtask.getId());
        calculateAndSetEpicStatus(epic);
    }

    public void removeSubtaskById(long id) {
        var epic = epics.get(subtasks.get(id).getEpicId());
        subtasks.remove(id);
        epic.removeSubtask(id);
        calculateAndSetEpicStatus(epic);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        epics
                .entrySet()
                .stream()
                .map(x -> x.getValue())
                .forEach(x -> {
                            x.removeAllSubtasks();
                            calculateAndSetEpicStatus(x);
                        });
    }

    public void calculateAndSetEpicStatus(Epic epic) {
        Set<Status> epicSubtasksStatuses = subtasks
                .values()
                .parallelStream()
                .filter(x -> x.getEpicId() == epic.getId())
                .map(x -> x.getStatus())
                .collect(toSet());

        if (epic.getSubtasks().isEmpty() || (epicSubtasksStatuses.size() == 1 && epicSubtasksStatuses.contains(NEW)))
            epic.setStatus(NEW);
        else if (epicSubtasksStatuses.size() == 1 && epicSubtasksStatuses.contains(DONE)) {
            epic.setStatus(DONE);
        }
        else
            epic.setStatus(IN_PROGRESS);
    }

    public void printAllTaskAndEpics() {
        System.out.println("====================");
        System.out.println("Tasks: ");
        getAllTasks().stream().forEach(System.out::println);
        System.out.println();
        System.out.println("Epics: ");
        getAllEpics()
                .stream()
                .forEach(epic -> {
                    System.out.println(epic);
                    getEpicSubtasks(epic.getId()).forEach(task -> System.out.println("\t" + task));
                });
    }
}
