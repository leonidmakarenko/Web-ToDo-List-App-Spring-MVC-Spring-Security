package leo.springwebapp.spring_web_todolist.service;

import leo.springwebapp.spring_web_todolist.model.Role;

import java.util.List;

public interface RoleService {
    Role create(Role role);
    Role readById(long id);
    Role update(Role role);
    void delete(long id);
    List<Role> getAll();
}