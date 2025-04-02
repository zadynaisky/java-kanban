package main.service;

import main.model.Task;
import main.utils.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> history = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.remove(task.getId());
            history.linkLast(task);
        }
    }

    public void remove(long id) {
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    public class CustomLinkedList<T extends Task> {
        private Node<T> first;
        private Node<T> last;
        private final Map<Long, Node<T>> registry = new HashMap<>();

        public void linkLast(T t) {
            final Node<T> l = this.last;
            final Node<T> newNode = new Node<>(l, t, null);
            this.last = newNode;

            if (l == null)
                first = newNode;
            else
                l.setNext(newNode);

            registry.put(t.getId(), newNode);
        }

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node<T> node = first;
            while (node != null) {
                tasks.add(node.getItem());
                node = node.getNext();
            }
            return tasks;
        }

        public void remove(long id) {
            if (registry.containsKey(id))
                removeNode(registry.get(id));
        }

        public void removeNode(Node<T> x) {
            T t = x.getItem();
            final Node<T> prev = x.getPrev();
            final Node<T> next = x.getNext();

            if (prev == null) {
                first = next;
            } else {
                prev.setNext(next);
                x.setPrev(null);
            }

            if (next == null) {
                last = prev;
            } else {
                next.setPrev(prev);
                x.setNext(null);
            }

            registry.remove(x.getItem().getId());
            x.setItem(null);
        }
    }
}
