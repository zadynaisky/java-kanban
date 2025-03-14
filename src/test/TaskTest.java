package test;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import static model.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void twoDifferentTasksWithSameIdAreEqual() {
        Task task1 = new Task(1, "title 1", "description 1", NEW);
        Task task2 = new Task(1, "title 2", "description 2", IN_PROGRESS);
        assertEquals(task1, task2, "Tasks with same id are not equal");
    }
}