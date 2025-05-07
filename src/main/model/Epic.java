package main.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import static main.model.TaskType.EPIC;

public class Epic extends Task {
    private Set<Long> subtasks;
    private LocalDateTime endDateTime;

    public Epic(String title, String description) {
        super(title, description, null, 0L);
        this.subtasks = new HashSet<>();
    }

    public Epic(long id, String title, String description, Status status, Set<Long> subtasks) {
        super(id, title, description, status, null, 0L);
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
                ", startTime=" + super.getStartTime() +
                ", endTime=" + getEndTime() +
                ", duration=" + super.getDuration() +
                '}';
    }

    @Override
    public String toCsvString() {
        return new StringJoiner(",")
                .add(String.valueOf(getId()))
                .add(getTaskType().name())
                .add(getTitle())
                .add(getDescription())
                .add(getStatus().name())
                .add("")
                .add("")
                .add("")
                .toString();
    }

    @Override
    public TaskType getTaskType() {
        return EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
