package test;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static model.Status.IN_PROGRESS;
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
        Subtask subtask = new Subtask("Subtask title", "Subtask description", epicId);
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
        Subtask oldSubtask = new Subtask("Old subtask title", "Old subtask description", epicId);
        var oldSubtaskId = taskManager.addSubtask(oldSubtask);
        taskManager.getSubtask(oldSubtaskId);
        Subtask newSubtask = new Subtask(oldSubtaskId, "New subtask title", "New subtask description", IN_PROGRESS, epicId);
        taskManager.updateSubtask(newSubtask);
        assertEquals(1, taskManager.getHistory().size());
        Subtask subtaskFromHistory = (Subtask) taskManager.getHistory().getFirst();
        assertEquals(oldSubtask.getTitle(), subtaskFromHistory.getTitle());
        assertEquals(oldSubtask.getDescription(), subtaskFromHistory.getDescription());
        assertEquals(oldSubtask.getStatus(), subtaskFromHistory.getStatus());
    }

    @Test
    public void getHistoryReturnsOnly10LastSeenIssues(){
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Title" + i, "Description" + i);
            var taskId = taskManager.addTask(task);
            taskManager.getTask(taskId);
        }
        assertEquals(10, taskManager.getHistory().size());
        List<String> tasksNames = taskManager.getHistory().stream().map(Task::getTitle).collect(toList());
        assertFalse(tasksNames.contains("Title1"));
        assertTrue(tasksNames.contains("Title2"));
        assertTrue(tasksNames.contains("Title11"));
    }
}
