import main.model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.service.Managers;
import main.service.TaskManager;

import java.time.LocalDateTime;

import static main.model.Status.IN_PROGRESS;
import static main.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void twoDifferentSubtasksWithSameIdAreEqual() {
        Subtask subtask1 = new Subtask(1, "title 1", "description 1", NEW, 1, LocalDateTime.now(), 1440);
        Subtask subtask2 = new Subtask(1, "title 2", "description 2", IN_PROGRESS, 2, LocalDateTime.now(), 1440);
        assertEquals(subtask1, subtask2, "Subtasks with same id are not equal");
    }
}
