package main.service;

import main.exception.ManagerSaveException;
import main.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.HashSet;

import static main.model.Status.IN_PROGRESS;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File datasource;

    public FileBackedTaskManager(File datasource) {
        this.datasource = datasource;
    }

    public static void main(String[] args) {
        FileBackedTaskManager firstManager = new FileBackedTaskManager(new File("/home/zed/test.csv"));
        long firstTaskId = firstManager.addTask(new Task("First task title", "First task description"));
        long secondTaskId = firstManager.addTask(new Task("Second task title", "Second task description"));
        long firstEpicId = firstManager.addEpic(new Epic("First epic title", "First epic description"));
        long firstEpicFirstSubtaskId = firstManager.addSubtask(new Subtask("Epic 1: First Subtask title",
                "Epic 1: First subtask description", firstEpicId));
        firstManager.updateSubtask(new Subtask(firstEpicFirstSubtaskId, "Epic 1: Second Subtask title",
                "Epic 1: Second subtask description", IN_PROGRESS, firstEpicId));
        long firstEpicSecondSubtaskId = firstManager.addSubtask(new Subtask("Epic 1: Second Subtask title",
                "Epic 1: Second subtask description", firstEpicId));
        long firstEpicThirdSubtaskId = firstManager.addSubtask(new Subtask("Epic 1: Third Subtask title",
                "Epic 1: Third subtask description", firstEpicId));
        long secondEpicId = firstManager.addEpic(new Epic("Second epic title", "Second epic description"));

        FileBackedTaskManager secondManager = new FileBackedTaskManager(new File("/home/zed/test.csv"));

        secondManager.load();

        System.out.println("Tasks are equal: " + firstManager.getAllTasks().equals(secondManager.getAllTasks()));
        System.out.println("Epics are equal: " + firstManager.getAllEpics().equals(secondManager.getAllEpics()));
        System.out.println("Tasks are equal: " + firstManager.getAllSubtasks().equals(secondManager.getAllSubtasks()));
        System.out.println("nextId are equal: " + (firstManager.getNextId() == secondManager.getNextId()));

        System.out.println();

    }

    public void save() {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(datasource), CSVFormat.Builder
                .create(CSVFormat.DEFAULT)
                .setHeader("id", "taskType", "title", "description", "status", "epicId")
                .build())) {

            for (Task task : getAllTasks()) {
                printer.printRecord(task.toCSVPrinterRecord());
            }
            for (Epic epic : getAllEpics()) {
                printer.printRecord(epic.toCSVPrinterRecord());
            }
            for (Subtask subtask : getAllSubtasks()) {
                printer.printRecord(subtask.toCSVPrinterRecord());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Couldn't save the data to the file");
        }
    }

    public void load(){
        try (Reader in = new FileReader(datasource)){
            Iterable<CSVRecord> records = CSVFormat.Builder
                    .create(CSVFormat.DEFAULT)
                    .setHeader("id", "taskType", "title", "description", "status", "epicId")
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(in);
            for (CSVRecord record : records) {
                var task = fromCSVRecord(record);

                if (task.getId() >= getNextId())
                    setNextId(task.getId());

                switch (task.getTaskType()){
                    case TASK -> super.updateTask(task);
                    case EPIC -> super.updateEpic((Epic) task);
                    case SUBTASK -> super.updateSubtask((Subtask) task);
                }
            }
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Couldn't find file");
        } catch (IOException e) {
            throw new ManagerSaveException("Couldn't read file");
        }
        linkEpicsToSubtask();
    }

    public Task fromCSVRecord(CSVRecord record) {
        TaskType taskType = TaskType.valueOf(record.get("taskType"));
        switch (taskType) {
            case EPIC -> {
                return new Epic(Long.parseLong(record.get("id")), record.get("title"), record.get("description"),
                        Status.valueOf(record.get("status")), new HashSet<>());
            }
            case SUBTASK -> {
                return new Subtask(Long.parseLong(record.get("id")), record.get("title"), record.get("description"),
                        Status.valueOf(record.get("status")), Long.parseLong(record.get("epicId")));
            }
            default -> {
                return new Task(Long.parseLong(record.get("id")), record.get("title"), record.get("description"),
                        Status.valueOf(record.get("status")));
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
