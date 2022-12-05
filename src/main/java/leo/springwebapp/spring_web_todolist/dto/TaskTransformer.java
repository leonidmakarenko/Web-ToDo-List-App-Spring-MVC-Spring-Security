package leo.springwebapp.spring_web_todolist.dto;

import leo.springwebapp.spring_web_todolist.model.Priority;
import leo.springwebapp.spring_web_todolist.model.State;
import leo.springwebapp.spring_web_todolist.model.Task;
import leo.springwebapp.spring_web_todolist.model.ToDo;

public class TaskTransformer {
    public static TaskDto convertToDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getPriority().toString(),
                task.getTodo().getId(),
                task.getState().getId()
        );
    }

    public static Task convertToEntity(TaskDto taskDto, ToDo todo, State state) {
        Task task = new Task();
        task.setId(taskDto.getId());
        task.setName(taskDto.getName());
        task.setPriority(Priority.valueOf(taskDto.getPriority()));
        task.setTodo(todo);
        task.setState(state);
        return task;
    }
}