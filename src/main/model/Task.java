package main.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

import static main.model.Status.NEW;
import static main.model.TaskType.TASK;

public class Task implements Comparable<Task> {
    private long id;
    private String title;
    private String description;
    private Status status;

    private Duration duration;
    private LocalDateTime startTime;

    public Task(String title, String description, LocalDateTime startTime, long duration) {
        this.title = title;
        this.description = description;
        this.status = NEW;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(long id, String title, String description, Status status, LocalDateTime startTime, long duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Task))
            return false;
        if (obj == this)
            return true;
        Task other = (Task) obj;
        if (other.id == this.id)
            return true;
        else
            return false;
    }

    @Override
    public int compareTo(Task o) {
        return Long.compare(id, o.id);
    }

    public TaskType getTaskType() {
        return TASK;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", duration=" + getDuration() +
                '}';
    }

    public String toCsvString() {
        return new StringJoiner(",")
                .add(String.valueOf(getId()))
                .add(getTaskType().name())
                .add(getTitle())
                .add(getDescription())
                .add(getStatus().name())
                .add("")
                .add(String.valueOf(getStartTime()))
                .add(String.valueOf(getDuration().toMinutes()))
                .toString();
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public boolean isIntersectWith(Task other) {
        if (getStartTime() == null || getEndTime() == null || other.getStartTime() == null || other.getEndTime() == null)
            return false;
        if ((other.getStartTime().isAfter(getStartTime()) && other.getStartTime().isBefore(getEndTime()))
                || (other.getEndTime().isAfter(getStartTime()) && other.getEndTime().isBefore(getEndTime())))
            return true;
        return false;
    }
}
