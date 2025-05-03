package main.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import static main.model.TaskType.SUBTASK;

public class Subtask extends Task {
    private final long epicId;

    public Subtask(String title, String description, long epicId, LocalDateTime startTime, long duration) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(long id, String title, String description, Status status, long epicId, LocalDateTime startTime, long duration) {
        super(id, title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + this.getId() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status='" + this.getStatus() + '\'' +
                ", epicId=" + epicId +
                ", startTime=" + super.getStartTime() +
                ", duration=" + super.getDuration() +
                '}';
    }

    public String toCsvString() {
        return new StringJoiner(",")
                .add(String.valueOf(getId()))
                .add(getTaskType().name())
                .add(getTitle())
                .add(getDescription())
                .add(getStatus().name())
                .add(String.valueOf(epicId))
                .add(String.valueOf(getStartTime()))
                .add(String.valueOf(getDuration().toMinutes()))
                .toString();
    }

    @Override
    public TaskType getTaskType() {
        return SUBTASK;
    }
}
