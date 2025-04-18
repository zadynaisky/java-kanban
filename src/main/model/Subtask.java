package main.model;

import java.util.StringJoiner;

import static main.model.TaskType.SUBTASK;

public class Subtask extends Task {
    private final long epicId;

    public Subtask(String title, String description, long epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(long id, String title, String description, Status status, long epicId) {
        super(id, title, description, status);
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
                .toString();
    }

    @Override
    public TaskType getTaskType() {
        return SUBTASK;
    }
}
