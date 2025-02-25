package model;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static model.Status.*;

public class Epic extends Task{
    /*
    Кирилл!
    Не знаю зачем хранить в эпике его сабтаски или айдишники его сабтасок (т.к. их всегда можно получить
    с помощью TaskManager'а, и его мапы subtasks фильтрованной по эпикАйди, но в задании сказано что
    "Каждый эпик знает, какие подзадачи в него входят.", а для этого эпик либо должен "знать" про таскменеджер,
    либо хранить в себе информацию про свои сабтаски). Подскажите в чем великий смысл этого дублирования в задании
    которое потенциально может привести систему в неконсистентно состояние (в мапе менеджера сабтаска есть, а в мапе
    эпика нет, или наоборот - мне не понятно.
     */
    Set<Subtask> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new HashSet<>();
    }

    public Epic(long id, String title, String description) {
        super(title, description);
        this.setId(id);
        this.subtasks = new HashSet<>();
    }

    public void setSubtasks(Set<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        if (subtasks == null || subtask.getEpicId() != this.getId()) {
            System.out.println("Incorrect subtask");
        }
        subtasks.add(subtask);
        this.calculateAndSetEpicStatus();
    }

    public void removeSubtask(long id) {
        subtasks.remove(id);
        this.calculateAndSetEpicStatus();
    }
    public void  removeAllSubtasks() { subtasks.clear();}

    /*
    Кирилл, этот метод кмк должен быть в менеджере, но тогда совсем непонятно зачем "Каждый эпик знает,
    какие подзадачи в него входят.". Чтобы хоть как-то оправдать это сет с сабтасками в эпике
    сделал тут. Почему не стоило так делать?
     */
    public void calculateAndSetEpicStatus() {
        Set<Status> epicSubtasksStatuses = subtasks
                .stream()
                .map(x -> x.getStatus())
                .collect(toSet());

        if (epicSubtasksStatuses.size() == 0 || (epicSubtasksStatuses.size() == 1 && epicSubtasksStatuses.contains(NEW)))
            this.setStatus(NEW);
        else if (epicSubtasksStatuses.size() == 1 && epicSubtasksStatuses.contains(DONE)) {
            this.setStatus(DONE);
        }
        else
            this.setStatus(IN_PROGRESS);
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
}
