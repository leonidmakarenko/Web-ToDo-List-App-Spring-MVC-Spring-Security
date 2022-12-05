package leo.springwebapp.spring_web_todolist.controller;

import leo.springwebapp.spring_web_todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Controller
public class HomeController {
    private final UserService userService;

    @Autowired
//    private WebSecurityConfigurer webSecurityConfigurer;
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping({"/", "home"})
    public String home(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get().getAuthority().equals("ADMIN")) {
            model.addAttribute("users", userService.getAll());
        } else {
            model.addAttribute("users", Arrays.asList(userService.readByEmail(SecurityContextHolder.getContext().getAuthentication().getName())));
        }
        return "home";
    }
}