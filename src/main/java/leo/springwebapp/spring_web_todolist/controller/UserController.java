package leo.springwebapp.spring_web_todolist.controller;

import leo.springwebapp.spring_web_todolist.model.User;
import leo.springwebapp.spring_web_todolist.security.CustomAuthenticationManager;
import leo.springwebapp.spring_web_todolist.service.RoleService;
import leo.springwebapp.spring_web_todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final CustomAuthenticationManager manager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, RoleService roleService, CustomAuthenticationManager manager,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasAuthority('ADMIN') or !isAuthenticated()")
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        return "create-user";
    }

    @PreAuthorize("hasAuthority('ADMIN') or !isAuthenticated()")
    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") User user, BindingResult result) {

        isEmailUnique(user, result);

        if (result.hasErrors()) {
            return "create-user";
        }
        String password = user.getPassword();
        user.setRole(roleService.readById(2));
        User newUser = userService.create(user);
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return "redirect:/home";
        }
        Authentication auth = manager.authenticate(new UsernamePasswordAuthenticationToken(newUser.getEmail(), password));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/login";
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.details.userId == #id")
    @GetMapping("/{id}/read")
    public String read(@PathVariable long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.details.userId == #id")
    @GetMapping("/{id}/update")
    public String update(@PathVariable long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAll());
		//			if controller authority used (not thymeleaf)
//        model.addAttribute("authority", SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
//                .findFirst().get().getAuthority().equals("ADMIN"));
        return "update-user";
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.details.userId == #id")
    @PostMapping("/{id}/update")
    public String update(@PathVariable long id, Model model, @Validated @ModelAttribute("user") User user, BindingResult result,
                         @RequestParam("roleId") long roleId, @RequestParam("old-password") String oldPassword) {
		User oldUser = userService.readById(id);
		if (!passwordEncoder.matches(oldPassword, oldUser.getPassword())) {
            result.addError(new ObjectError("oldPasswordError", "Invalid existing password entered"));
        }

		if (!Objects.equals(oldUser.getEmail(), user.getEmail())) {
			isEmailUnique(user, result);
		}

		if (result.hasErrors()) {
            user.setRole(roleService.readById(roleId));
            model.addAttribute("roles", roleService.getAll());
//			if controller authority used (not thymeleaf)
//            model.addAttribute("authority", SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
//                    .findFirst().get().getAuthority().equals("ADMIN"));
            return "update-user";
        }
        oldUser.setFirstName(user.getFirstName());
        oldUser.setLastName(user.getLastName());
        oldUser.setPassword(user.getPassword());
        oldUser.setEmail(user.getEmail());

        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get()
                .getAuthority().equals("ADMIN")) {
            oldUser.setRole(roleService.readById(roleId));
        }

        userService.update(oldUser);
        return "redirect:/users/" + id + "/read";
    }

    @PreAuthorize("hasAuthority('ADMIN') and authentication.details.userId != #id or" +
            " hasAuthority('USER') and authentication.details.userId == #id")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id) {

        if (userService.readByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getId() == id) {
            userService.delete(id);
            SecurityContextHolder.clearContext();
            return "redirect:/login";
        }
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get()
                .getAuthority().equals("ADMIN")) {
            userService.delete(id);
            return "redirect:/users/all";
        }
        return "not-found";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users-list";
    }

	private void isEmailUnique(User user, BindingResult result) {
		if (userService.readByEmail(user.getEmail()) != null) {
			result.addError(new FieldError("email", "email",
					"Email " + user.getEmail() + " not unique"));
		}
	}
}