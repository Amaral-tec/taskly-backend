package com.amaral.taskly.auth.controller;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amaral.taskly.security.JwtUtils;
import com.amaral.taskly.user.model.User;
import com.amaral.taskly.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(body.get("login"), body.get("password")));
        String token = jwtUtils.generateJwt(body.get("login"));
        return Map.of("token", token);
    }

    @PostMapping("/register")
    public User register(@RequestBody Map<String, String> body) {
        return userService.register(body.get("login"), body.get("password"));
    }

    @PostMapping("/change-password")
    public Map<String, String> changePassword(@RequestBody Map<String, String> body) {
        User user = userService.findByLogin(body.get("login"));
        userService.changePassword(user, body.get("password")); 

        return Map.of("message", "Password changed successfully");
    }

}
