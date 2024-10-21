package service;

import domain.entity.Task;
import domain.enums.Status;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class TaskService {

    private final List<Task> tasks = createTasks();

    private List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            Task task = new Task(i, "Задача" + i, Status.values()[i % 3]);
            tasks.add(task);
        }
        return tasks;
    }

    public List<Task> getTasksByStatus(Status status) {
        return tasks.stream().filter(task -> task.getStatus() == status).toList();
    }

    public boolean isTaskPresentById(Integer id) {
        return tasks.stream().anyMatch(task -> task.getId() == id);
    }

    public List<Task> getTasksSortedByStatus() {
        return tasks.stream().sorted(Comparator.comparing(Task::getStatus)).toList();
    }

    public Long getTaskCountByStatus(Status status) {
        return tasks.stream().filter(task -> task.getStatus() == status).count();
    }

}