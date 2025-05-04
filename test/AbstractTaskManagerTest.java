import main.exception.ManagerSaveException;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.service.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static main.model.Status.*;
import static main.model.Status.DONE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractTaskManagerTest <T extends TaskManager>{
    T taskManager;

    @Test
    public void addTask() {
        assertTrue(taskManager.getAllTasks().isEmpty());
        Task task = new Task("Title", "Description", LocalDateTime.now(), 1440);
        var taskId = taskManager.addTask(task);
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(task, taskManager.getTask(taskId));
    }

    @Test
    public void addEpic() {
        assertTrue(taskManager.getAllEpics().isEmpty());
        Epic epic = new Epic("Title", "Description");
        long epicId = taskManager.addEpic(epic);
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(epic, taskManager.getEpic(epicId));
        assertNotNull(taskManager.getEpic(epicId).getSubtasks());
    }

    @Test
    public void addSubtask() {
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask title", "Subtask description", epicId, LocalDateTime.now(), 1440);
        var subtaskId = taskManager.addSubtask(subtask);
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals(subtask, taskManager.getSubtask(subtaskId));
        List<Long> epicSubtasksIds = taskManager.getEpicSubtasks(epicId).stream().map(Subtask::getId).collect(toList());
        assertTrue(epicSubtasksIds.contains(subtaskId));
    }

    @Test
    public void removeTaskById() {
        Task task = new Task("Title", "Description", LocalDateTime.now(), 1440);
        long taskId = taskManager.addTask(task);
        assertEquals(1, taskManager.getAllTasks().size());
        assertTrue(taskManager.getAllTasks().contains(task));
        taskManager.removeTaskById(taskId);
        assertEquals(0, taskManager.getAllTasks().size());
        assertFalse(taskManager.getAllTasks().contains(task));
    }

    @Test
    public void removeEpicById() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask title", "Subtask description", epicId, LocalDateTime.now(), 1440);
        var subtaskId = taskManager.addSubtask(subtask);
        taskManager.removeEpicById(epicId);
        assertNull(taskManager.getEpic(epicId));
        assertNull(taskManager.getSubtask(subtaskId));
    }

    @Test
    void removeSubtaskById() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask title", "Subtask description", epicId, LocalDateTime.now(), 1440);
        var subtaskId = taskManager.addSubtask(subtask);
        taskManager.removeSubtaskById(subtaskId);
        assertNull(taskManager.getSubtask(subtaskId));
        assertEquals(false, taskManager.getEpic(epicId).getSubtasks().contains(subtask.getId()));
    }

    @Test
    public void epicBecomeNewIfSubtaskListIsEmptyOrAllSubtasksIsNew() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        assertEquals(0, taskManager.getEpic(epicId).getSubtasks().size());
        assertEquals(NEW, taskManager.getEpic(epicId).getStatus());
        Subtask subtask1 = new Subtask("Subtask 1 title", "Subtask 1 description", epicId, LocalDateTime.now(), 1440);
        var subtask1Id = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 title", "Subtask 2 description", epicId, LocalDateTime.now().plusDays(2), 1440);
        var subtask2Id = taskManager.addSubtask(subtask2);
        assertEquals(NEW, taskManager.getEpic(epicId).getStatus());
        taskManager.updateSubtask(new Subtask(subtask1Id, "Subtask 1", "Subtask 1 description", IN_PROGRESS, epicId, LocalDateTime.now(), 1440));
        assertNotEquals(NEW, taskManager.getEpic(epicId).getStatus());
        taskManager.removeSubtaskById(subtask1Id);
        assertEquals(NEW, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void epicBecomeInProgressIfAnySubtaskIsInProgress() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1 title", "Subtask 1 description", epicId, LocalDateTime.now(), 1440);
        var subtask1Id = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 title", "Subtask 2 description", epicId, LocalDateTime.now().plusDays(2), 1440);
        var subtask2Id = taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask 3 title", "Subtask 3 description", epicId, LocalDateTime.now().plusDays(4), 1440);
        var subtask3Id = taskManager.addSubtask(subtask3);
        assertEquals(NEW, taskManager.getEpic(epicId).getStatus());
        taskManager.updateSubtask(new Subtask(subtask1Id, "Subtask 1", "Subtask 1 description", IN_PROGRESS, epicId, LocalDateTime.now(), 1440));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Subtask 2 description", DONE, epicId, LocalDateTime.now().plusDays(2), 1440));
        assertEquals(IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void epicBecomeDoneIfAllSubtaskIsDone() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1 title", "Subtask 1 description", epicId, LocalDateTime.now(), 1440);
        var subtask1Id = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 title", "Subtask 2 description", epicId, LocalDateTime.now().plusDays(2), 1440);
        var subtask2Id = taskManager.addSubtask(subtask2);
        assertEquals(NEW, taskManager.getEpic(epicId).getStatus());
        taskManager.updateSubtask(new Subtask(subtask1Id, "Subtask 1", "Subtask 1 description", DONE, epicId, LocalDateTime.now(), 1440));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Subtask 2 description", DONE, epicId, LocalDateTime.now().plusDays(2), 1440));
        assertEquals(DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void epicStartAndEndDatetimeCalculatesCorrectly() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1 title", "Subtask 1 description", epicId, LocalDateTime.now(), 1440);
        Subtask subtask2 = new Subtask("Subtask 2 title", "Subtask 2 description", epicId, LocalDateTime.now().plusDays(4), 10000);
        Subtask subtask3 = new Subtask("Subtask 3 title", "Subtask 3 description", epicId, LocalDateTime.now().plusDays(2), 100);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask2.getEndTime(), epic.getEndTime());
        taskManager.removeSubtaskById(subtask1.getId());
        taskManager.removeSubtaskById(subtask2.getId());
        assertEquals(subtask3.getStartTime(), epic.getStartTime());
        assertEquals(subtask3.getEndTime(), epic.getEndTime());
    }

    @Test
    public void attemptToAddTwoTasksWithIntersectingIntervalReturnsError() {
        assertThrows(IllegalArgumentException.class, () -> {
            Task task = new Task("Task 1 title", "Task 1 description", LocalDateTime.now(), 1440);
            var task1Id = taskManager.addTask(task);
            Task task2 = new Task("Task 2 title", "Task 2 description", LocalDateTime.now(), 1000);
            var task2Id = taskManager.addTask(task2);
        }, "Попытка добавления двух задач с пересекающимися интервалами выполнения");
    }
}
