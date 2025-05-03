package main.service;

import main.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static main.model.Status.*;

public class InMemoryTaskManager implements TaskManager {
    final Map<Long, Epic> epics = new HashMap<>();
    final Map<Long, Task> tasks = new HashMap<>();
    final Map<Long, Subtask> subtasks = new HashMap<>();
    private static long nextId = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(
            Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId));

    @Override
    public long addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Epic getEpic(long id) {
        var epic = epics.get(id);
        if (epic != null)
            historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(long id) {
        return subtasks
                .values()
                .stream()
                .filter(x -> x.getEpicId() == id)
                .sorted()
                .collect(toList());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeEpicById(long id) {
        epics.remove(id);
        historyManager.remove(id);
        var idsToDelete = subtasks
                .values()
                .stream()
                .filter(x -> x.getEpicId() == id)
                .map(x -> x.getId())
                .collect(toList());
        subtasks.entrySet().removeIf(entry -> idsToDelete.contains(entry.getKey()));
        idsToDelete.stream().forEach(x -> historyManager.remove(x));
    }

    @Override
    public void removeAllEpics() {
        subtasks.values().forEach(prioritizedTasks::remove);
        epics.clear();
        subtasks.clear();
        var idsToDelete = historyManager
                .getHistory()
                .stream()
                .filter(x -> x instanceof Subtask || x instanceof Epic)
                .map(x -> x.getId())
                .collect(toList());
        idsToDelete.forEach(id -> historyManager.remove(id));
    }

    @Override
    public long addTask(Task task) {
        if (taskIsIntersectWithOthersTasks(task))
            throw new IllegalArgumentException("Task interval intersect with existed task");
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public Task getTask(long id) {
        var task = tasks.get(id);
        if (task != null)
            historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        if (taskIsIntersectWithOthersTasks(task))
            throw new IllegalArgumentException("Task interval intersect with existed task");
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeTaskById(long id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        var idsToDelete = historyManager
                .getHistory()
                .stream()
                .filter(x -> x instanceof Task)
                .map(x -> x.getId())
                .collect(toList());
        idsToDelete.forEach(id -> historyManager.remove(id));
        tasks.clear();
    }

    @Override
    public long addSubtask(Subtask subtask) {
        if (!(subtask instanceof Subtask)) {
            System.out.println("Only subtasks can be added");
            return -1;
        }
        if (taskIsIntersectWithOthersTasks(subtask))
            throw new IllegalArgumentException("Task interval intersect with existed task");

        var epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            subtask.setId(nextId++);
            subtasks.put(subtask.getId(), subtask);
            epics.get(epicId).addSubtask(subtask.getId());
            calculateAndSetEpicStatus(epics.get(epicId));
            calculateAndSetEpicStartEndTimeAndDuration(epics.get(epicId));
            prioritizedTasks.add(subtask);
            return subtask.getId();
        } else {
            System.out.println("Subtask wasn't added. Couldn't find epic with id " + subtask.getEpicId());
            return -1;
        }
    }

    @Override
    public Subtask getSubtask(long id) {
        var subtask = subtasks.get(id);
        if (subtask != null)
            historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        var epicId = subtask.getEpicId();

        if (taskIsIntersectWithOthersTasks(subtask))
            throw new IllegalArgumentException("Task interval intersect with existed task");

        if (!epics.containsKey(epicId)) {
            System.out.println("Subtask wasn't updated. Couldn't find epic with id " + epicId);
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        var epic = epics.get(epicId);
        epic.addSubtask(subtask.getId());
        calculateAndSetEpicStatus(epic);
    }

    @Override
    public void removeSubtaskById(long id) {
        prioritizedTasks.remove(subtasks.get(id));
        var epic = epics.get(subtasks.get(id).getEpicId());
        subtasks.remove(id);
        historyManager.remove(id);
        epic.removeSubtask(id);
        calculateAndSetEpicStatus(epic);
        calculateAndSetEpicStartEndTimeAndDuration(epic);
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(prioritizedTasks::remove);
        var idsToDelete = historyManager
                .getHistory()
                .stream()
                .filter(x -> x instanceof Subtask)
                .map(x -> x.getId())
                .collect(toList());
        idsToDelete.forEach(id -> historyManager.remove(id));
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

    public List<Task> getHistory() {
        return historyManager.getHistory();
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
        } else
            epic.setStatus(IN_PROGRESS);
    }

    @Override
    public void calculateAndSetEpicStartEndTimeAndDuration(Epic epic) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration duration = null;

        for (long subtaskId : epic.getSubtasks()){
            if (subtasks.containsKey(subtaskId)){
                Subtask subtask = subtasks.get(subtaskId);
                if (startTime == null || subtask.getStartTime().isBefore(startTime))
                    startTime = subtask.getStartTime();
                if (endTime == null || subtask.getEndTime().isAfter(endTime))
                    endTime = subtask.getEndTime();
            }
        }
        epic.setStartTime(startTime);
        epic.setEndDateTime(endTime);
        if (startTime != null && endTime != null)
            duration = Duration.between(startTime, endTime);
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

    public static void setNextId(long nextId) {
        InMemoryTaskManager.nextId = nextId;
    }

    public static long getNextId() {
        return nextId;
    }

    public Map<Long, Epic> getEpics() {
        return epics;
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public boolean taskIsIntersectWithOthersTasks(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null)
            return false;
        return  getPrioritizedTasks()
                .stream()
                .filter(x -> x.getId() != task.getId())
                .filter(x -> x.isIntersectWith(task))
                .findAny()
                .isPresent();
    }
}
