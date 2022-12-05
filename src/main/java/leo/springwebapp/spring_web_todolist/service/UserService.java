package leo.springwebapp.spring_web_todolist.service;

import leo.springwebapp.spring_web_todolist.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User readById(long id);

    User update(User user);

    void delete(long id);

    List<User> getAll();

    User readByEmail(String email);

}