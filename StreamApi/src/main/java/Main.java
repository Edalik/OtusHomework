import domain.enums.Status;
import service.TaskService;

public class Main {

    public static void main(String[] args) {
        TaskService taskService = new TaskService();
        System.out.println(taskService.getTasks());
        System.out.println(taskService.getTasksByStatus(Status.CLOSED));
        System.out.println(taskService.isTaskPresentById(1));
        System.out.println(taskService.getTasksSortedByStatus());
        System.out.println(taskService.getTaskCountByStatus(Status.OPEN));
    }

}