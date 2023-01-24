package leo.springwebapp.spring_web_todolist.controller;

import leo.springwebapp.spring_web_todolist.model.Task;
import leo.springwebapp.spring_web_todolist.model.ToDo;
import leo.springwebapp.spring_web_todolist.model.User;
import leo.springwebapp.spring_web_todolist.security.CustomUserDetails;
import leo.springwebapp.spring_web_todolist.service.TaskService;
import leo.springwebapp.spring_web_todolist.service.ToDoService;
import leo.springwebapp.spring_web_todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller("toDoController")
@RequestMapping("/todos")
public class ToDoController {

    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public ToDoController(ToDoService todoService, TaskService taskService, UserService userService) {
        this.todoService = todoService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.details.userId == #ownerId")
    @GetMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") long ownerId, Model model) {
        model.addAttribute("todo", new ToDo());
        model.addAttribute("ownerId", ownerId);
        return "create-todo";
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.details.userId == #ownerId")
    @PostMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") long ownerId, @Validated @ModelAttribute("todo") ToDo todo,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "create-todo";
        }
        todo.setCreatedAt(LocalDateTime.now());
        todo.setOwner(userService.readById(ownerId));
        todoService.create(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.canReadTodo(#id)")
    @GetMapping("/{id}/tasks")
    public String read(@PathVariable long id, Model model) {
        ToDo todo = todoService.readById(id);
        List<Task> tasks = taskService.getByTodoId(id);
        List<User> collaborators = userService.getAll().stream()
                .filter(user -> (user.getId() != todo.getOwner().getId() && !todo.getCollaborators()
                        .contains(user))).collect(Collectors.toList());
        model.addAttribute("todo", todo);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", collaborators);
        model.addAttribute("authority", ((CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getDetails()).getUserId() == todo.getOwner().getId() ||
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .anyMatch(c -> c.getAuthority().equals("ADMIN")));
        return "todo-tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.details.userId == #ownerId")
    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId, Model model) {
        ToDo todo = todoService.readById(todoId);
        model.addAttribute("todo", todo);
        return "update-todo";
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.details.userId == #ownerId")
    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId,
                         @Validated @ModelAttribute("todo") ToDo todo, BindingResult result) {
        if (result.hasErrors()) {
            todo.setOwner(userService.readById(ownerId));
            return "update-todo";
        }
        ToDo oldTodo = todoService.readById(todoId);
        todo.setOwner(oldTodo.getOwner());
        todo.setCollaborators(oldTodo.getCollaborators());
        todoService.update(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.details.userId == #ownerId")
    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    public String delete(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId) {
        todoService.delete(todoId);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') and authentication.details.userId == #userId")
    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable("user_id") long userId, Model model) {
        model.addAttribute("todos", todoService.getByUserId(userId));
        model.addAttribute("user", userService.readById(userId));
        return "todos-user";
    }

    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#id).owner.id == authentication.details.userId ")
    @GetMapping("/{id}/add")
    public String addCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        if (userId == -1) {
            return "redirect:/todos/" + id + "/tasks";
        }
        ToDo todo = todoService.readById(id);
        todo.getCollaborators().add(userService.readById(userId));
        todoService.update(todo);
        return "redirect:/todos/" + id + "/tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#id).owner.id == authentication.details.userId ")
    @GetMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        ToDo todo = todoService.readById(id);
        todo.getCollaborators().remove(userService.readById(userId));
        todoService.update(todo);
        return "redirect:/todos/" + id + "/tasks";
    }

}