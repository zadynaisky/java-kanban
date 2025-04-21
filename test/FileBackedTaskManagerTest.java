import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.service.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private File testFile;

    @BeforeEach
    void setUp() {
        try {
            testFile = File.createTempFile("FileBackedTaskManagerTest", "csv");
            fileBackedTaskManager = new FileBackedTaskManager(testFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        testFile.deleteOnExit();
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks(){
        fileBackedTaskManager.save();
        fileBackedTaskManager.loadFromFile(testFile);
        assertEquals(Collections.EMPTY_LIST, fileBackedTaskManager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, fileBackedTaskManager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, fileBackedTaskManager.getAllSubtasks());
    }

    @Test
    public void shouldSaveAndLoad(){
        try {
            testFile = File.createTempFile("FileBackedTaskManagerTest", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileBackedTaskManager = new FileBackedTaskManager(testFile);

        long firstTaskId = fileBackedTaskManager.addTask(new Task("First task title", "First task description"));
        long secondTaskId = fileBackedTaskManager.addTask(new Task("Second task title", "Second task description"));
        long firstEpicId = fileBackedTaskManager.addEpic(new Epic("First epic title", "First epic description"));
        long firstEpicFirstSubtaskId = fileBackedTaskManager.addSubtask(new Subtask("Epic 1: First Subtask title",
                "Epic 1: First subtask description", firstEpicId));
        long firstEpicSecondSubtaskId = fileBackedTaskManager.addSubtask(new Subtask("Epic 1: Second Subtask title",
                "Epic 1: Second subtask description", firstEpicId));
        long firstEpicThirdSubtaskId = fileBackedTaskManager.addSubtask(new Subtask("Epic 1: Third Subtask title",
                "Epic 1: Third subtask description", firstEpicId));
        long secondEpicId = fileBackedTaskManager.addEpic(new Epic("Second epic title", "Second epic description"));

        System.out.println(fileBackedTaskManager.getNextId());

        FileBackedTaskManager secondFileBackedTaskManager = FileBackedTaskManager.loadFromFile(testFile);

        System.out.println(secondFileBackedTaskManager.getNextId());

        assertEquals(fileBackedTaskManager.getAllTasks(), secondFileBackedTaskManager.getAllTasks());
        assertEquals(fileBackedTaskManager.getAllEpics(), secondFileBackedTaskManager.getAllEpics());
        assertEquals(fileBackedTaskManager.getAllSubtasks(), secondFileBackedTaskManager.getAllSubtasks());
        assertEquals(fileBackedTaskManager.getNextId(), secondFileBackedTaskManager.getNextId());
        assertEquals(fileBackedTaskManager.getEpic(firstEpicId).getSubtasks(), secondFileBackedTaskManager.getEpic(firstEpicId).getSubtasks());
    }
}