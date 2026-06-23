package com.Rentals.app.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/register",
                    "/auth_ui/**", "/debug/**",
                    "/css/**", "/js/**", "/images/**" ,"/login","/register","/auth_ui/**",        // This covers ALL files under auth_ui including js/ and css/
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/*.css",
                    "/*.js"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=L002")
                .permitAll()
            )
            .logout(logout -> logout
                // After logout, redirect to /login?logout — your JS already handles this param
                .logoutSuccessUrl("/login?msg=L003")
                .permitAll()
            );

        return http.build();
    }
}