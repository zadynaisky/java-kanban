package main.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static main.model.TaskType.EPIC;

public class Epic extends Task {
    private Set<Long> subtasks;
    private LocalDateTime endDateTime;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new HashSet<>();
    }

    public Epic(long id, String title, String description, Status status, Set<Long> subtasks) {
        super(id, title, description, status);
        this.subtasks = subtasks;
    }

    public Set<Long> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(long id) {
        subtasks.add(id);
    }

    public void removeSubtask(long id) {
        subtasks.remove(id);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + this.getId() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status='" + this.getStatus() + '\'' +
                ", subtasks.size=" + subtasks.size() +
                '}';
    }

    @Override
    public TaskType getTaskType() {
        return EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endDateTime;
    }
}
