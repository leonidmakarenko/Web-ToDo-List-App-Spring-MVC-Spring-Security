package leo.springwebapp.spring_web_todolist.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @RequestMapping("/login")
    public String loginPage (@RequestParam(value = "error", required = false) String error, Model model) {
//		public String loginPage (@RequestParam(value = "error", required = false) String error,
//				@RequestParam(value = "perform-logout", required = false) String logout,
//				Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return "redirect:home";
        }
        model.addAttribute("error", error != null);
//		model.addAttribute("logout", logout != null);
        return "login";
    }
}

