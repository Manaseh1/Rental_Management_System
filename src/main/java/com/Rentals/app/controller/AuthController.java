package com.Rentals.app.controller;
import com.Rentals.app.dto.RegisterRequest;
import com.Rentals.app.repository.UserRepository;
import jakarta.validation.Valid;

import com.Rentals.app.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    
    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    // Show login page by redirecting to the static HTML asset
    @GetMapping("/login")
    public String showLoginPage() {
        return "redirect:/auth_ui/login.html";
    }

    // Show register page by redirecting to the static HTML asset
    @GetMapping("/register")
    public String showRegisterPage(){
        return "redirect:/auth_ui/register.html";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegisterRequest request, RedirectAttributes redirectAttributes){
        authService.register(
            request.getUsername(),
            request.getEmail(),
            request.getPassword());

        redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
        return "redirect:/login";
    }

    // Debug endpoint to view all users
    @GetMapping("/debug/users")
    @ResponseBody
    public Object getAllUsers() {
        return userRepository.findAll();
    }
}
