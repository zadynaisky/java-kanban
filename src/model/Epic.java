package model;

public class Epic extends Task{

    public Epic(String title, String description) {
        super(title, description);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + this.getId() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status='" + this.getStatus() + '\'' +
                '}';
    }
}
