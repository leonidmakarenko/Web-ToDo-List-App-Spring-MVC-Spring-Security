package leo.springwebapp.spring_web_todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class WebToDoListApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WebToDoListApplication.class, args);
    }

}
