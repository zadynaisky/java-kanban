import main.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends AbstractTaskManagerTest<InMemoryTaskManager>{
    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }
}
