package leo.springwebapp.spring_web_todolist.service;

import leo.springwebapp.spring_web_todolist.model.ToDo;

import java.util.List;

public interface ToDoService {
    ToDo create(ToDo todo);
    ToDo readById(long id);
    ToDo update(ToDo todo);
    void delete(long id);

    List<ToDo> getAll();
    List<ToDo> getByUserId(long userId);
}