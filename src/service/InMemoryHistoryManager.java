package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private final int HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() >= HISTORY_SIZE)
            history.removeFirst();
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
