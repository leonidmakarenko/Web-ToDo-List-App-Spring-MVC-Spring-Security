package leo.springwebapp.spring_web_todolist.controller;

import leo.springwebapp.spring_web_todolist.dto.TaskDto;
import leo.springwebapp.spring_web_todolist.dto.TaskTransformer;
import leo.springwebapp.spring_web_todolist.model.Priority;
import leo.springwebapp.spring_web_todolist.model.Task;
import leo.springwebapp.spring_web_todolist.service.StateService;
import leo.springwebapp.spring_web_todolist.service.TaskService;
import leo.springwebapp.spring_web_todolist.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {
	private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;

    @Autowired
    public TaskController(TaskService taskService, ToDoService todoService, StateService stateService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;
    }

    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.canReadTodo(#todoId)")
    @GetMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model) {
        model.addAttribute("task", new TaskDto());
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("priorities", Priority.values());
        return "create-task";
    }

    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.canReadTodo(#todoId)")
    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
    	
        if (result.hasErrors()) {
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", Priority.values());
            return "create-task";
        }
        Task task = TaskTransformer.convertToEntity(taskDto, todoService.readById(todoId),
                stateService.getByName("New")
        );
        taskService.create(task);
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.canReadTodo(#todoId)")
    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model) {
        TaskDto taskDto = TaskTransformer.convertToDto(taskService.readById(taskId));
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("states", stateService.getAll());
        return "update-task";
    }

    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.canReadTodo(#todoId)")
    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task")TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("states", stateService.getAll());
            return "update-task";
        }
        Task task = TaskTransformer.convertToEntity(taskDto, todoService.readById(todoId), stateService.readById(taskDto.getStateId()));
        taskService.update(task);
        return "redirect:/todos/" + todoId + "/tasks";
    }
    

    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.canReadTodo(#todoId)")
    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId) {
        taskService.delete(taskId);
        return "redirect:/todos/" + todoId + "/tasks";
    }
    
}