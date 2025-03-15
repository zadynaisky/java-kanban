package test;

import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import static model.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void twoDifferentSubtasksWithSameIdAreEqual() {
        Subtask subtask1 = new Subtask(1, "title 1", "description 1", NEW, 1);
        Subtask subtask2 = new Subtask(1, "title 2", "description 2", IN_PROGRESS, 2);
        assertEquals(subtask1, subtask2, "Subtasks with same id are not equal");
    }
}
