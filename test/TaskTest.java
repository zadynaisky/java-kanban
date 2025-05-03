import main.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.service.Managers;
import main.service.TaskManager;

import java.time.LocalDateTime;

import static main.model.Status.IN_PROGRESS;
import static main.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void twoDifferentTasksWithSameIdAreEqual() {
        Task task1 = new Task(1, "title 1", "description 1", NEW, LocalDateTime.now(), 1440);
        Task task2 = new Task(1, "title 2", "description 2", IN_PROGRESS, LocalDateTime.now(), 1440);
        assertEquals(task1, task2, "Tasks with same id are not equal");
    }

    @Test
    public void isIntersectWithReturnsFalseIfTasksDoNotIntersect() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(1, "title 1", "description 1", NEW, now.minusDays(1), 1440);
        Task task2 = new Task(2, "title 2", "description 2", NEW, now, 1440);
        Task task3 = new Task(3, "title 3", "description 3", NEW, now.plusDays(1), 1440);
        assertFalse(task2.isIntersectWith(task1));
        assertFalse(task2.isIntersectWith(task3));
        Task task4 = new Task(4, "title 4", "description 4", NEW, now.minusDays(1), 1441);
        assertTrue(task2.isIntersectWith(task4));
        assertTrue(task2.isIntersectWith(new Task(5, "title 5", "description 5", NEW, LocalDateTime.now(), 1440)));
    }
}