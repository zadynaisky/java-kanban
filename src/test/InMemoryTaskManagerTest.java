package test;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void addTask() {
        assertTrue(taskManager.getAllTasks().isEmpty());
        Task task = new Task("Title", "Description");
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
        Subtask subtask = new Subtask("Subtask title", "Subtask description", epicId);
        var subtaskId = taskManager.addSubtask(subtask);
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals(subtask, taskManager.getSubtask(subtaskId));
        List<Long> epicSubtasksIds = taskManager.getEpicSubtasks(epicId).stream().map(Subtask::getId).collect(toList());
        assertTrue(epicSubtasksIds.contains(subtaskId));
    }

    @Test
    public void removeTaskById(){
        Task task = new Task("Title", "Description");
        long taskId = taskManager.addTask(task);
        assertEquals(1, taskManager.getAllTasks().size());
        assertTrue(taskManager.getAllTasks().contains(task));
        taskManager.removeTaskById(taskId);
        assertEquals(0, taskManager.getAllTasks().size());
        assertFalse(taskManager.getAllTasks().contains(task));
    }

    @Test
    public void removeEpicById(){
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask title", "Subtask description", epicId);
        var subtaskId = taskManager.addSubtask(subtask);
        taskManager.removeEpicById(epicId);
        assertNull(taskManager.getEpic(epicId));
        assertNull(taskManager.getSubtask(subtaskId));
    }

    @Test void removeSubtaskById(){
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask title", "Subtask description", epicId);
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
        Subtask subtask1 = new Subtask("Subtask 1 title", "Subtask 1 description", epicId);
        var subtask1Id = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 title", "Subtask 2 description", epicId);
        var subtask2Id = taskManager.addSubtask(subtask2);
        assertEquals(NEW, taskManager.getEpic(epicId).getStatus());
        taskManager.updateSubtask(new Subtask(subtask1Id, "Subtask 1", "Subtask 1 description", IN_PROGRESS, epicId));
        assertNotEquals(NEW, taskManager.getEpic(epicId).getStatus());
        taskManager.removeSubtaskById(subtask1Id);
        assertEquals(NEW, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void epicBecomeInProgressIfAnySubtaskIsInProgress() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1 title", "Subtask 1 description", epicId);
        var subtask1Id = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 title", "Subtask 2 description", epicId);
        var subtask2Id = taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask 3 title", "Subtask 3 description", epicId);
        var subtask3Id = taskManager.addSubtask(subtask3);
        assertEquals(NEW, taskManager.getEpic(epicId).getStatus());
        taskManager.updateSubtask(new Subtask(subtask1Id, "Subtask 1", "Subtask 1 description", IN_PROGRESS, epicId));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Subtask 2 description", DONE, epicId));
        assertEquals(IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void epicBecomeDoneIfAllSubtaskIsDone() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        var epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1 title", "Subtask 1 description", epicId);
        var subtask1Id = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 title", "Subtask 2 description", epicId);
        var subtask2Id = taskManager.addSubtask(subtask2);
        assertEquals(NEW, taskManager.getEpic(epicId).getStatus());
        taskManager.updateSubtask(new Subtask(subtask1Id, "Subtask 1", "Subtask 1 description", DONE, epicId));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Subtask 2 description", DONE, epicId));
        assertEquals(DONE, taskManager.getEpic(epicId).getStatus());

    }
}
