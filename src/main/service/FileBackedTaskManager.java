package main.service;

import main.exception.ManagerSaveException;
import main.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static main.model.Status.IN_PROGRESS;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File datasource;
    private static final String csvHeader = "id,taskType,title,description,status,epicId";

    public FileBackedTaskManager(File datasource) {
        this.datasource = datasource;
    }

    public static void main(String[] args) {
        FileBackedTaskManager firstManager = new FileBackedTaskManager(new File("/home/vidyakina/test.csv"));
        long firstTaskId = firstManager.addTask(new Task("First task title", "First task description", LocalDateTime.now(), 1440));
        long secondTaskId = firstManager.addTask(new Task("Second task title", "Second task description", LocalDateTime.now(), 1440));
        long firstEpicId = firstManager.addEpic(new Epic("First epic title", "First epic description"));
        long firstEpicFirstSubtaskId = firstManager.addSubtask(new Subtask("Epic 1: First Subtask title",
                "Epic 1: First subtask description", firstEpicId, LocalDateTime.now(), 1440));
        firstManager.updateSubtask(new Subtask(firstEpicFirstSubtaskId, "Epic 1: Second Subtask title",
                "Epic 1: Second subtask description", IN_PROGRESS, firstEpicId, LocalDateTime.now(), 1440));
        long firstEpicSecondSubtaskId = firstManager.addSubtask(new Subtask("Epic 1: Second Subtask title",
                "Epic 1: Second subtask description", firstEpicId, LocalDateTime.now(), 1440));
        long firstEpicThirdSubtaskId = firstManager.addSubtask(new Subtask("Epic 1: Third Subtask title",
                "Epic 1: Third subtask description", firstEpicId, LocalDateTime.now(), 1440));
        long secondEpicId = firstManager.addEpic(new Epic("Second epic title", "Second epic description"));

        FileBackedTaskManager secondManager = FileBackedTaskManager.loadFromFile(new File("/home/vidyakina/test.csv"));

        System.out.println("Tasks are equal: " + firstManager.getAllTasks().equals(secondManager.getAllTasks()));
        System.out.println("Epics are equal: " + firstManager.getAllEpics().equals(secondManager.getAllEpics()));
        System.out.println("Tasks are equal: " + firstManager.getAllSubtasks().equals(secondManager.getAllSubtasks()));
        System.out.println("nextId are equal: " + (firstManager.getNextId() == secondManager.getNextId()));
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(datasource))) {
            writer.write(csvHeader + System.lineSeparator());
            for (Task task : getAllTasks()) {
                writer.write(task.toCsvString() + System.lineSeparator());
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toCsvString() + System.lineSeparator());
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(subtask.toCsvString() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Couldn't save the data to the file");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                var task = fileBackedTaskManager.fromString(line);
                if (task.getId() >= fileBackedTaskManager.getNextId())
                    fileBackedTaskManager.setNextId(task.getId() + 1);
                switch (task.getTaskType()) {
                    case TASK -> fileBackedTaskManager.tasks.put(task.getId(), task);
                    case EPIC -> fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                    case SUBTASK -> fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                }
            }
            for (Epic epic : fileBackedTaskManager.epics.values()) {
                fileBackedTaskManager.calculateAndSetEpicStartEndTimeAndDuration(epic);
            }
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Couldn't find file");
        } catch (IOException e) {
            throw new ManagerSaveException("Couldn't read file");
        }
        fileBackedTaskManager.linkEpicsToSubtask();
        return fileBackedTaskManager;
    }

    public Task fromString(String line) {
        String[] parts = line.split(",");
        TaskType taskType = TaskType.valueOf(parts[1]);

        switch (taskType) {
            case EPIC -> {
                return new Epic(Long.parseLong(parts[0]), parts[2], parts[3],
                        Status.valueOf(parts[4]), new HashSet<>());
            }
            case SUBTASK -> {
                return new Subtask(Long.parseLong(parts[0]), parts[2], parts[3],
                        Status.valueOf(parts[4]), Long.parseLong(parts[5]), LocalDateTime.parse(parts[6], DateTimeFormatter.ISO_DATE_TIME), Long.parseLong(parts[7]));
            }
            default -> {
                return new Task(Long.parseLong(parts[0]), parts[2], parts[3],
                        Status.valueOf(parts[4]), LocalDateTime.parse(parts[6], DateTimeFormatter.ISO_DATE_TIME), Long.parseLong(parts[7]));
            }
        }
    }

    public void linkEpicsToSubtask() {
        for (Subtask s : getAllSubtasks()) {
            if (getEpics().containsKey(s.getEpicId()))
                getEpics().get(s.getEpicId()).addSubtask(s.getId());
            else
                System.out.println("Unknown epicId: " + s.getEpicId());
        }
    }

    @Override
    public long addEpic(Epic epic) {
        var x = super.addEpic(epic);
        save();
        return x;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(long id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public long addTask(Task task) {
        var x = super.addTask(task);
        save();
        return x;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(long id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public long addSubtask(Subtask subtask) {
        var x = super.addSubtask(subtask);
        save();
        return x;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskById(long id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }
}
