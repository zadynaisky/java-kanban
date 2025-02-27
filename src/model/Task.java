package model;

import java.util.Objects;
import static model.Status.NEW;

public class Task implements Comparable<Task> {
    private long id;
    private String title;
    private String description;
    private Status status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = NEW;
    }

    public Task(long id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public void setId(long id) { this.id = id; }

    public void setStatus(Status status) { this.status = status; }

    public long getId() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public Status getStatus() { return status; }

    @Override
    public int hashCode() { return Objects.hash(id); }

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
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public int compareTo(Task o) {
        return Long.compare(id, o.id);
    }
}
