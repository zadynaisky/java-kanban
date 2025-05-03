import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.service.Managers;
import main.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static main.model.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void getHistoryReturnsOldVersionOfEpicAfterUpdate() {
        Epic oldEpic = new Epic("Old epic title", "Old epic description");
        var epicId = taskManager.addEpic(oldEpic);
        Subtask subtask = new Subtask("Subtask title", "Subtask description", epicId, LocalDateTime.now(), 1440);
        taskManager.addSubtask(subtask);
        taskManager.getEpic(epicId);
        Epic newEpic = new Epic(epicId, "New task title", "New task description", IN_PROGRESS, new HashSet<>());
        taskManager.addEpic(newEpic);
        assertEquals(1, taskManager.getHistory().size());
        Epic epicFromHistory = (Epic) taskManager.getHistory().getFirst();
        assertEquals(oldEpic.getTitle(), epicFromHistory.getTitle());
        assertEquals(oldEpic.getDescription(), epicFromHistory.getDescription());
        assertEquals(oldEpic.getStatus(), epicFromHistory.getStatus());
    }

    @Test
    public void getHistoryReturnsNewVersionOfSubtaskAfterUpdate() {
        Epic epic = new Epic("Epic title", "Epic description");
        var epicId = taskManager.addEpic(epic);
        Subtask oldSubtask = new Subtask("Old subtask title", "Old subtask description", epicId, LocalDateTime.now(), 1440);
        var oldSubtaskId = taskManager.addSubtask(oldSubtask);
        taskManager.getSubtask(oldSubtaskId);
        Subtask newSubtask = new Subtask(oldSubtaskId, "New subtask title", "New subtask description", IN_PROGRESS, epicId, LocalDateTime.now(), 1440);
        taskManager.updateSubtask(newSubtask);
        assertEquals(1, taskManager.getHistory().size());
        Subtask subtaskFromHistory = (Subtask) taskManager.getHistory().getFirst();
        assertEquals(oldSubtask.getTitle(), subtaskFromHistory.getTitle());
        assertEquals(oldSubtask.getDescription(), subtaskFromHistory.getDescription());
        assertEquals(oldSubtask.getStatus(), subtaskFromHistory.getStatus());
    }

    @Test
    public void getHistoryReturnsLastSeenIssues() {
        final int tasksNum = 1_000_000;
        for (int i = 1; i <= tasksNum; i++) {
            Task task = new Task("Title" + i, "Description" + i, LocalDateTime.now(), 1440);
            var taskId = taskManager.addTask(task);
            taskManager.getTask(taskId);
        }
        assertEquals(tasksNum, taskManager.getHistory().size());
        List<String> tasksNames = taskManager.getHistory().stream().map(Task::getTitle).collect(toList());
        assertTrue(tasksNames.contains("Title1"));
        assertTrue(tasksNames.contains("Title2"));
        assertTrue(tasksNames.contains("Title" + tasksNum));
        assertFalse(tasksNames.contains("Title" + tasksNum + 1));
    }

    @Test
    public void tasksRemovesFromHistoryAfterDeletion() {
        Epic epic = new Epic("Epic title", "Epic description");
        taskManager.addEpic(epic);
        taskManager.getEpic(epic.getId());
        Subtask subtask = new Subtask("Subtask title", "Subtask description", epic.getId(), LocalDateTime.now(), 1440);
        taskManager.addSubtask(subtask);
        taskManager.getSubtask(subtask.getId());
        Task task = new Task("Title", "Description", LocalDateTime.now(), 1440);
        taskManager.addTask(task);
        taskManager.getTask(task.getId());
        assertTrue(taskManager.getHistory().contains(epic));
        assertTrue(taskManager.getHistory().contains(subtask));
        assertTrue(taskManager.getHistory().contains(task));
        taskManager.removeSubtaskById(subtask.getId());
        assertTrue(taskManager.getHistory().contains(epic));
        assertFalse(taskManager.getHistory().contains(subtask));
        assertTrue(taskManager.getHistory().contains(task));
        taskManager.removeEpicById(epic.getId());
        assertFalse(taskManager.getHistory().contains(epic));
        assertFalse(taskManager.getHistory().contains(subtask));
        assertTrue(taskManager.getHistory().contains(task));
        taskManager.removeTaskById(task.getId());
        assertFalse(taskManager.getHistory().contains(epic));
        assertFalse(taskManager.getHistory().contains(subtask));
        assertFalse(taskManager.getHistory().contains(task));
    }
}
