package com.Rentals.app.controller;
import com.Rentals.app.dto.RegisterRequest;
import com.Rentals.app.repository.UserRepository;
import jakarta.validation.Valid;

import com.Rentals.app.service.AuthService;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
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
    public String showLoginPage(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "msg", required = false) String msg) {
        return "forward:/auth_ui/login.html";
    }

    // Show register page by redirecting to the static HTML asset
    @GetMapping("/register")
    public String showRegisterPage(){
        return "forward:/auth_ui/register.html";
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "redirect:/dashboard.html";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegisterRequest request, RedirectAttributes redirectAttributes){
        authService.register(
            request.getUsername(),
            request.getEmail(),
            request.getPassword());

        return "redirect:/login?registered=true";
    }

    // Debug endpoint to view all users
    @GetMapping("/debug/users")
    @ResponseBody
    public Object getAllUsers() {
        return userRepository.findAll();
    }    
    @Configuration
    class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/auth_ui/**")
            .addResourceLocations("classpath:/static/auth_ui/");
    }}
}
