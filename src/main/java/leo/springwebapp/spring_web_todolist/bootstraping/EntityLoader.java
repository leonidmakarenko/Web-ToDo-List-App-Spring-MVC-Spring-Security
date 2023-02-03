package leo.springwebapp.spring_web_todolist.bootstraping;

import leo.springwebapp.spring_web_todolist.model.User;
import leo.springwebapp.spring_web_todolist.service.RoleService;
import leo.springwebapp.spring_web_todolist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;
    private final RoleService roleService;

    private final Logger logger = LoggerFactory.getLogger(EntityLoader.class);


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        User user = new User();
        user.setEmail("leo@mail.com");
        user.setRole(roleService.readById(2));
        user.setPassword("3333");
        user.setFirstName("Leo");
        user.setLastName("Silver");
        user = userService.create(user);

        logger.info("Saved user " + user.getFirstName() + " " +
                user.getLastName() + " with id = " + user.getId());
    }
}
