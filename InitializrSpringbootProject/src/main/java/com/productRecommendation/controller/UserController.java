package com.productRecommendation.controller;

import com.productRecommendation.entity.User;
import com.productRecommendation.service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        Long userId = null;
        model.addAttribute("user", new User("password", "username"));
        return "register"; // Replace with your registration view name
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.save(user);
            return "redirect:/login"; // Redirect to login page after successful registration
        } catch (Exception e) {
            // Handle registration errors (e.g., duplicate username)
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("credentials", new LoginCredentials("username", "password")); // Replace with login credential object
        return "login"; // Replace with your login view name
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute LoginCredentials credentials, Model model) {
        try {
            // Implement authentication logic (e.g., using Spring Security)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getUsername(),
                            credentials.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/products"; // Redirect to product listing after successful login
        } catch (Exception e) {
            // Handle login errors (e.g., invalid username/password)
            model.addAttribute("error", "Invalid username or password");
            model.addAttribute("credentials", credentials);
            return "login";
        }
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model, Principal principal) {
        if (principal instanceof UserDetails) {
            User user = userService.findByUsername(((UserDetails) principal).getUsername());
            model.addAttribute("user", user);
        }
        return "profile"; // Replace with your profile view name
    }
}

// Login credential class 
class LoginCredentials {

    LoginCredentials(String username1, String password1) {
    }

    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
