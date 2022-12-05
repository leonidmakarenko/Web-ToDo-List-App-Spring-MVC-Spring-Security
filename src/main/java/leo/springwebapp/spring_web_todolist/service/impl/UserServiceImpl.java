package leo.springwebapp.spring_web_todolist.service.impl;

import jakarta.persistence.EntityNotFoundException;
import leo.springwebapp.spring_web_todolist.exception.NullEntityReferenceException;
import leo.springwebapp.spring_web_todolist.model.User;
import leo.springwebapp.spring_web_todolist.repository.UserRepository;
import leo.springwebapp.spring_web_todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User create(User user) {
        if (user != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        throw new NullEntityReferenceException("User cannot be 'null'");
    }

    @Override
    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public User readByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User update(User role) {
        if (role != null) {
            readById(role.getId());
            role.setPassword(passwordEncoder.encode(role.getPassword()));
            return userRepository.save(role);
        }
        throw new NullEntityReferenceException("User cannot be 'null'");
    }

    @Override
    public void delete(long id) {
        userRepository.delete(readById(id));
    }

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? new ArrayList<>() : users;
    }
}
