package leo.springwebapp.spring_web_todolist.controller;

import leo.springwebapp.spring_web_todolist.model.Task;
import leo.springwebapp.spring_web_todolist.model.ToDo;
import leo.springwebapp.spring_web_todolist.model.User;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller("toDoController")
@RequestMapping("/todos")
public class ToDoController {

//    @Autowired
//    private WebSecurityConfigurer webSecurityConfigurer;
    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;

//    private User getAuthenticatedUser(){
//        return userService.readByEmail(webSecurityConfigurer.getCurrentUsername());
//    }

    @Autowired
    public ToDoController(ToDoService todoService, TaskService taskService, UserService userService) {
        this.todoService = todoService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") long ownerId, Model model) {
//        User currentUser = getAuthenticatedUser();
//        if (currentUser.getRole().getName().equals("ADMIN") | userService.readById(ownerId).getEmail().equals(currentUser.getEmail())){
            model.addAttribute("todo", new ToDo());
            model.addAttribute("ownerId", ownerId);
            return "create-todo";
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") long ownerId, @Validated @ModelAttribute("todo") ToDo todo, BindingResult result) {
        if (result.hasErrors()) {
            return "create-todo";
        }
//        User currentUser = getAuthenticatedUser();
//        if (currentUser.getRole().getName().equals("ADMIN") | userService.readById(ownerId).getEmail().equals(currentUser.getEmail())){
            todo.setCreatedAt(LocalDateTime.now());
            todo.setOwner(userService.readById(ownerId));
            todoService.create(todo);
            return "redirect:/todos/all/users/" + ownerId;
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }
    
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/{id}/tasks")
    public String read(@PathVariable long id, Model model) {
//    	User currentUser = getAuthenticatedUser();
//        if (this.todoAcces(id, currentUser)){
        ToDo todo = todoService.readById(id);
        List<Task> tasks = taskService.getByTodoId(id);
        List<User> users = userService.getAll().stream()
                .filter(user -> user.getId() != todo.getOwner().getId()).collect(Collectors.toList());
        model.addAttribute("todo", todo);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", users);
//        model.addAttribute("authority", currentUser.getRole().getName().equals("ADMIN") || todo.getOwner().getEmail() == currentUser.getEmail());
       model.addAttribute("authority", true); ///////////////////////STUB!!!!!!!!!!!!
        return "todo-tasks";
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId, Model model) {
//        User currentUser = getAuthenticatedUser();
        ToDo todo = todoService.readById(todoId);
//        if ((currentUser.getRole().getName().equals("ADMIN") | (userService.readById(ownerId).getEmail().equals(currentUser.getEmail())) & todo.getOwner().getEmail().equals(currentUser.getEmail()))){
            model.addAttribute("todo", todo);
            return "update-todo";
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }

//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
//    @GetMapping("/{todo_id}/update/users/{owner_id}")
//    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId, Model model) {
//    	User currentUser = getAuthenticatedUser();
//    	ToDo todo = todoService.readById(todoId);
//    	if ((currentUser.getRole().getName().equals("ADMIN") | (userService.readById(ownerId).getEmail().equals(currentUser.getEmail())) & todo.getOwner().getEmail().equals(currentUser.getEmail()))){
//    		model.addAttribute("todo", todo);
//    		return "update-todo";
//    	}
//    	throw new ForbiddenException("User does not have sufficient access rights for this action!");
//    }
//    
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId,
                         @Validated @ModelAttribute("todo") ToDo todo, BindingResult result) {
        if (result.hasErrors()) {
            todo.setOwner(userService.readById(ownerId));
            return "update-todo";
        }

//        User currentUser = getAuthenticatedUser();
        ToDo oldTodo = todoService.readById(todoId);

//        if (currentUser.getRole().getName().equals("ADMIN") | (userService.readById(ownerId).getEmail().equals(currentUser.getEmail()) & oldTodo.getOwner().getEmail().equals(currentUser.getEmail()))){
        todo.setOwner(oldTodo.getOwner());
        todo.setCollaborators(oldTodo.getCollaborators());
        todoService.update(todo);
        return "redirect:/todos/all/users/" + ownerId;
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }

//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') and @toDoController.isOwner(#ownerId)")
    @PreAuthorize("hasAuthority('ADMIN') or authentication.details.userId == #ownerId")
    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    public String delete(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId) {
//        User currentUser = getAuthenticatedUser();
//        if (currentUser.getRole().getName().equals("ADMIN") | userService.readById(ownerId).getEmail().equals(currentUser.getEmail())) {
            todoService.delete(todoId);
            return "redirect:/todos/all/users/" + ownerId;
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }
    
//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
//    @GetMapping("/{todo_id}/delete/users/{owner_id}")
//    public String delete(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId) {
//    	User currentUser = getAuthenticatedUser();
//    	if (currentUser.getRole().getName().equals("ADMIN") | userService.readById(ownerId).getEmail().equals(currentUser.getEmail())) {
//    		todoService.delete(todoId);
//    		return "redirect:/todos/all/users/" + ownerId;
//    	}
//    	throw new ForbiddenException("User does not have sufficient access rights for this action!");
//    }
//    
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable("user_id") long userId, Model model) {
//    	User currentUser = getAuthenticatedUser();
//        if (currentUser.getId() == userId || currentUser.getRole().getName().equals("ADMIN")){
        List<ToDo> todos = todoService.getByUserId(userId);
        model.addAttribute("todos", todos);
        model.addAttribute("user", userService.readById(userId));
        return "todos-user";
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")  // here
    @GetMapping("/{id}/add")
    public String addCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        ToDo todo = todoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.add(userService.readById(userId));
        todo.setCollaborators(collaborators);
        todoService.update(todo);
        return "redirect:/todos/" + id + "/tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
//        User currentUser = getAuthenticatedUser();
        ToDo todo = todoService.readById(id);
//        if (currentUser.getRole().getName().equals("ADMIN") | todo.getOwner().getEmail().equals(currentUser.getEmail())) {
            List<User> collaborators = todo.getCollaborators();
            collaborators.remove(userService.readById(userId));
            todo.setCollaborators(collaborators);
            todoService.update(todo);
            return "redirect:/todos/" + id + "/tasks";
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
    }
    
    private boolean todoAcces(long todoId, User user) {
    	ToDo todo = todoService.readById(todoId);
    	return user.getRole().getName().equals("ADMIN") || todo.getCollaborators().contains(user) || todo.getOwner() == user;	
    }
    
    public boolean isOwner(long id) {
    	return id == 5;
    }
}