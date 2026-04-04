package com.amaral.taskly.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amaral.taskly.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @PostMapping("/{userPublicId}/access/{accessPublicId}")
    public ResponseEntity<Void> assignAccess(@PathVariable UUID userPublicId, @PathVariable UUID accessPublicId) {
        userService.assignAccess(userPublicId, accessPublicId);
        return ResponseEntity.ok().build();
    }
}
