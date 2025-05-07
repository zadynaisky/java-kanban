import main.exception.ManagerSaveException;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.service.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {
    private File testFile;

    @BeforeEach
    void setUp() {
        try {
            testFile = File.createTempFile("FileBackedTaskManagerTest", "csv");
            taskManager = new FileBackedTaskManager(testFile);
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
        taskManager.save();
        taskManager.loadFromFile(testFile);
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllSubtasks());
    }

    @Test
    public void shouldSaveAndLoad(){
        try {
            testFile = File.createTempFile("FileBackedTaskManagerTest", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = new FileBackedTaskManager(testFile);

        long firstTaskId = taskManager.addTask(new Task("First task title", "First task description", LocalDateTime.now(), 1440));
        long secondTaskId = taskManager.addTask(new Task("Second task title", "Second task description", LocalDateTime.now().plusDays(2), 1440));
        long firstEpicId = taskManager.addEpic(new Epic("First epic title", "First epic description"));
        long firstEpicFirstSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: First Subtask title",
                "Epic 1: First subtask description", firstEpicId, LocalDateTime.now().plusDays(4), 1440));
        long firstEpicSecondSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: Second Subtask title",
                "Epic 1: Second subtask description", firstEpicId, LocalDateTime.now().plusDays(6), 1440));
        long firstEpicThirdSubtaskId = taskManager.addSubtask(new Subtask("Epic 1: Third Subtask title",
                "Epic 1: Third subtask description", firstEpicId, LocalDateTime.now().plusDays(8), 1440));
        long secondEpicId = taskManager.addEpic(new Epic("Second epic title", "Second epic description"));

        FileBackedTaskManager secondFileBackedTaskManager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(taskManager.getAllTasks(), secondFileBackedTaskManager.getAllTasks());
        assertEquals(taskManager.getAllEpics(), secondFileBackedTaskManager.getAllEpics());
        assertEquals(taskManager.getAllSubtasks(), secondFileBackedTaskManager.getAllSubtasks());
        assertEquals(taskManager.getNextId(), secondFileBackedTaskManager.getNextId());
        assertEquals(taskManager.getEpic(firstEpicId).getSubtasks(), secondFileBackedTaskManager.getEpic(firstEpicId).getSubtasks());
    }

    @Test
    public void attemptToLoadFromNonexistentFileReturnsError() {
        assertThrows(ManagerSaveException.class, () -> {
            testFile.delete();
            taskManager.loadFromFile(testFile);
        }, "Попытка загрузки из несуществующего файла возвращает ошибку");
    }
}