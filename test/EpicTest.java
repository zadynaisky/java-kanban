import main.model.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.service.Managers;
import main.service.TaskManager;

import java.util.HashSet;
import java.util.List;

import static main.model.Status.IN_PROGRESS;
import static main.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void twoDifferentEpicsWithSameIdAreEqual() {
        Epic epic1 = new Epic(1, "title 1", "description 1", NEW, new HashSet<>());
        Epic epic2 = new Epic(1, "title 2", "description 2", IN_PROGRESS, new HashSet<>());
        assertEquals(epic1, epic2, "Epics with same id are not equal");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("First epic title", "First epic description");
        final long epicId = taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Epic not found");
        assertEquals(epic, savedEpic, "Epics not equal");

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "EpicsList not found");
        assertEquals(1, epics.size(), "Incorrect epics list size");
        assertEquals(epic, epics.get(0), "Epics not equal");
    }

    @Test
    void removeSubtask() {
    }

    @Test
    void removeAllSubtasks() {
    }
}