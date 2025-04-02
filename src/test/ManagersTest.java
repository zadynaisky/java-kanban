package test;

import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    @Test
    public void getDefaultReturnsInMemoryTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
        assertInstanceOf(InMemoryTaskManager.class, manager);
    }

    @Test
    public void getDefaultHistoryReturnsInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertInstanceOf(InMemoryHistoryManager.class, historyManager);
    }
}
