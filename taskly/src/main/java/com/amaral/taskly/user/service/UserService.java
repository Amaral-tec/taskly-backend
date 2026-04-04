package com.amaral.taskly.user.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.amaral.taskly.BusinessException;
import com.amaral.taskly.access.dto.AccessRequestDTO;
import com.amaral.taskly.access.dto.AccessResponseDTO;
import com.amaral.taskly.access.model.Access;
import com.amaral.taskly.access.service.AccessService;
import com.amaral.taskly.user.model.User;
import com.amaral.taskly.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccessService accessService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, AccessService accessService, PasswordEncoder encoder) {
        this.userRepository = repo;
        this.accessService = accessService;
        this.passwordEncoder = encoder;
    }

    public void assignAccess(UUID userPublicId, UUID accessPublicId) {
        Access access = accessService.findByIdOrThrow(accessPublicId);
        User user = findByIdOrThrow(userPublicId);
        userRepository.insertUserAccess(user.getId(), access.getId());
    }
    
    public User register(String login, String rawPassword) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new RuntimeException("Login already exists");
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPasswordCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        assignDefaultRole(user.getPublicId());
        return user;
    }

    public void changePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new BusinessException("User not found: " + login));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    private User findByIdOrThrow(UUID userPublicId) {
        return userRepository.findByPublicId(userPublicId)
                .filter(a -> !a.getDeleted())
                .orElseThrow(() -> new BusinessException("ID not found or deleted: " + userPublicId));
    }

    private void assignDefaultRole(UUID userPublicId) {
        AccessResponseDTO dto = accessService
            .findAccessByName("ROLE_USER")
            .stream()
            .findFirst()
            .orElseGet(() -> accessService.createAccess(new AccessRequestDTO("ROLE_USER")));

        assignAccess(userPublicId, dto.publicId());
    }
}
