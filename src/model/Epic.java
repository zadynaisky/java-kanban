package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static model.Status.*;

public class Epic extends Task{
    /*
    Ревьюер!
    Не знаю зачем хранить в эпике его сабтаски или айдишники его сабтасок (т.к. их всегда можно получить
    с помощью TaskManager'а, и его мапы subtasks фильтрованной по эпикАйди, но в задании сказано что
    "Каждый эпик знает, какие подзадачи в него входят.", а для этого эпик либо должен "знать" про таскменеджер,
    либо хранить в себе информацию про свои сабтаски). Если ты мне расскажешь в чем великий смысл этого дублирования
    которое потенциально может привести систему в неконсистентно состояние (в мапе менеджера сабтаска есть, а в мапе
    эпика нет, или наоборот - мне не понятно.
     */
    Map<Long, Subtask> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new HashMap<>();
    }

    public Epic(long id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subtasks = new HashMap<>();
    }

    public void addSubtask(Subtask subtask) {
        if (subtasks == null || subtask.getEpicId() != this.getId()) {
            System.out.println("Incorrect subtask");
        }
        subtasks.put(subtask.getId(), subtask);
    }

    public void removeSubtask(long id) { subtasks.remove(id); }
    public void  removeAllSubtasks() { subtasks.clear();}

    public void calculateAndSetEpicStatus() {
        Set<Status> epicSubtasksStatuses = subtasks
                .values()
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
                '}';
    }
}
