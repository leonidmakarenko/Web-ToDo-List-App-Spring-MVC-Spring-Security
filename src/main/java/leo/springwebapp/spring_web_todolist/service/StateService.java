package leo.springwebapp.spring_web_todolist.service;

import leo.springwebapp.spring_web_todolist.model.State;

import java.util.List;

public interface StateService {
    State create(State state);
    State readById(long id);
    State update(State state);
    void delete(long id);

    State getByName(String name);
    List<State> getAll();
}