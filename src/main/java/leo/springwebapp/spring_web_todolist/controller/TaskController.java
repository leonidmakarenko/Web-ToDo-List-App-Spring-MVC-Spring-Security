package leo.springwebapp.spring_web_todolist.controller;

import leo.springwebapp.spring_web_todolist.dto.TaskDto;
import leo.springwebapp.spring_web_todolist.dto.TaskTransformer;
import leo.springwebapp.spring_web_todolist.model.Priority;
import leo.springwebapp.spring_web_todolist.model.Task;
import leo.springwebapp.spring_web_todolist.model.ToDo;
import leo.springwebapp.spring_web_todolist.model.User;
import leo.springwebapp.spring_web_todolist.service.StateService;
import leo.springwebapp.spring_web_todolist.service.TaskService;
import leo.springwebapp.spring_web_todolist.service.ToDoService;
import leo.springwebapp.spring_web_todolist.service.UserService;
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
//    @Autowired
//    private WebSecurityConfigurer webSecurityConfigurer;
	private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, ToDoService todoService, StateService stateService, UserService userService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;
        this.userService = userService;
    }
    
//    private User getAuthenticatedUser(){
//        return userService.readByEmail(webSecurityConfigurer.getCurrentUsername());
//    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model) {
//    	User currentUser = getAuthenticatedUser();
//        if (this.taskAcces(todoId, currentUser)){
        model.addAttribute("task", new TaskDto());
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("priorities", Priority.values());
        return "create-task";
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
    	
        if (result.hasErrors()) {
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", Priority.values());
            return "create-task";
        }
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.getByName("New")
        );
        taskService.create(task);
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model) {
//    	User currentUser = getAuthenticatedUser();
//        if (this.taskAcces(todoId, currentUser)){
        TaskDto taskDto = TaskTransformer.convertToDto(taskService.readById(taskId));
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("states", stateService.getAll());
        return "update-task";
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task")TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("states", stateService.getAll());
            return "update-task";
        }
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.readById(taskDto.getStateId()));
        taskService.update(task);
        return "redirect:/todos/" + todoId + "/tasks";
    }
    

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId) {
//    	User currentUser = getAuthenticatedUser();
//        if (this.taskAcces(todoId, currentUser)){
        taskService.delete(taskId);
        return "redirect:/todos/" + todoId + "/tasks";
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }
    
    public boolean taskAcces(long todoId, User user) {
    	ToDo todo = todoService.readById(todoId);
    	return user.getRole().getName().equals("ADMIN") || todo.getCollaborators().contains(user) || todo.getOwner() == user;	
    }
}