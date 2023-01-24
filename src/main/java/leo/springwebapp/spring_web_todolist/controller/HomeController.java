package leo.springwebapp.spring_web_todolist.controller;

import leo.springwebapp.spring_web_todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;

@Controller
public class HomeController {
    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping({"/", "home"})
    public String home(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            model.addAttribute("users", userService.getAll());
        } else {
            model.addAttribute("users", Collections.singletonList(userService.readByEmail(SecurityContextHolder.getContext().getAuthentication().getName())));
        }
        return "home";
    }
}