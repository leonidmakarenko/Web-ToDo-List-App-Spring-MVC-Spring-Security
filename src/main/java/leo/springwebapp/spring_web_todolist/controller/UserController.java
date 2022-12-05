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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	CustomAuthenticationManager manager;

//    @Autowired
//    private WebSecurityConfigurer webSecurityConfigurer;
	private final UserService userService;
	private final RoleService roleService;

//    private User getAuthenticatedUser(){
//        return userService.readByEmail(webSecurityConfigurer.getCurrentUsername());
//    }
	@Autowired
	public UserController(UserService userService, RoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}

	@GetMapping("/create")
	public String create(Model model) {
		model.addAttribute("user", new User());
		return "create-user";
	}

	@PostMapping("/create")
	public String create(@Validated @ModelAttribute("user") User user, BindingResult result) {
		if (result.hasErrors()) {
			return "create-user";
		}
		String password = user.getPassword();
		user.setRole(roleService.readById(2));
		User newUser = userService.create(user);
		Authentication auth = manager.authenticate(new UsernamePasswordAuthenticationToken(newUser.getEmail(), password));
		SecurityContextHolder.getContext().setAuthentication(auth);
		return "redirect:/login";
//		return "redirect:/todos/all/users/" + newUser.getId();
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping("/{id}/read")
	public String read(@PathVariable long id, Model model) {
//       User currentUser = getAuthenticatedUser();
//        if (currentUser.getRole().getName().equals("ADMIN") | userService.readById(id).getEmail().equals(currentUser.getEmail())){
		User user = userService.readById(id);
		model.addAttribute("user", user);
		return "user-info";
//        }
//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping("/{id}/update")
	public String update(@PathVariable long id, Model model) {
//        User currentUser = getAuthenticatedUser();
//        if (currentUser.getRole().getName().equals("ADMIN") | userService.readById(id).getEmail().equals(currentUser.getEmail())){
		User user = userService.readById(id);
		model.addAttribute("user", user);
		model.addAttribute("roles", roleService.getAll());
		model.addAttribute("authority", SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.findFirst().get().getAuthority().equals("ADMIN"));
		return "update-user";
//        }

//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PostMapping("/{id}/update")
	public String update(@PathVariable long id, Model model, @Validated @ModelAttribute("user") User user,
			@RequestParam("roleId") long roleId, BindingResult result) {
//        User currentUser = getAuthenticatedUser();
		
		if (result.hasErrors()) {
//			user.setRole(user.getRole());
//			model.addAttribute("roles", roleService.getAll());
			return "update-user";
		}
		User newUser = userService.readById(id);
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
		newUser.setPassword(user.getPassword());

		if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get()
				.getAuthority().equals("ADMIN")) {
			newUser.setEmail(user.getEmail());
			newUser.setRole(roleService.readById(roleId));
		}

		userService.update(newUser);
		return "redirect:/users/" + id + "/read";
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping("/{id}/delete")
	public String delete(@PathVariable("id") long id) {
//        User currentUser = getAuthenticatedUser();
		
		if(userService.readByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getId() == id) {
			userService.delete(id);
			return "redirect:/login";
		}
		if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get()
				.getAuthority().equals("ADMIN")) {
			userService.delete(id);
			return "redirect:/users/all";
		}
		return "not-found";
//		return "bad-request";
//        if (userService.readById(id).getEmail().equals(currentUser.getEmail())){
//		userService.delete(id);
//		return "redirect:/login";
//        }

//        throw new ForbiddenException("User does not have sufficient access rights for this action!");
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/all")
	public String getAll(Model model) {
//		String authority = getAuthenticatedUser().getRole().getName();
		List<User> users = new ArrayList<User>();
//		if (authority.equals("ADMIN")) {
			users = userService.getAll();
//		} else {
//			users.add(userService.readByEmail(webSecurityConfigurer.getCurrentUsername()));
//		}
		model.addAttribute("users", users);
		return "users-list";
	}
}