package leo.springwebapp.spring_web_todolist.service.impl;

import leo.springwebapp.spring_web_todolist.exception.NullEntityReferenceException;
import leo.springwebapp.spring_web_todolist.model.ToDo;
import leo.springwebapp.spring_web_todolist.repository.ToDoRepository;
import leo.springwebapp.spring_web_todolist.security.CustomUserDetails;
import leo.springwebapp.spring_web_todolist.service.ToDoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ToDoServiceImpl implements ToDoService {

    private final ToDoRepository todoRepository;

    @Override
    public ToDo create(ToDo todo) {
        if (todo != null) {
            return todoRepository.save(todo);
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    @Override
    public ToDo readById(long id) {
        return todoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("ToDo with id " + id + " not found"));
    }

    @Override
    public ToDo update(ToDo todo) {
        if (todo != null) {
            readById(todo.getId());
            return todoRepository.save(todo);
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    @Override
    public void delete(long id) {
        todoRepository.delete(readById(id));
    }

    @Override
    public List<ToDo> getAll() {
        List<ToDo> todos = todoRepository.findAll();
        return todos.isEmpty() ? new ArrayList<>() : todos;
    }

    @Override
    public List<ToDo> getByUserId(long userId) {
        List<ToDo> todos = todoRepository.getByUserId(userId);
        return todos.isEmpty() ? new ArrayList<>() : todos;
    }

    public boolean canReadTodo(long todoID){
        long userID = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUserId();
        ToDo toDo = readById(todoID);
        return toDo.getCollaborators().stream().anyMatch(c -> c.getId() == userID) || toDo.getOwner().getId() == userID;
    }
}