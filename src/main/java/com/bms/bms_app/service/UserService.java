package com.bms.bms_app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bms.bms_app.dto.LoginRequest;
import com.bms.bms_app.dto.LoginResponse;
import com.bms.bms_app.dto.RegisterRequest;
import com.bms.bms_app.dto.UserRequest;
import com.bms.bms_app.dto.UserResponse;
import com.bms.bms_app.exception.InvalidCredentialsException;
import com.bms.bms_app.exception.ResourceNotFoundException;
import com.bms.bms_app.model.Role;
import com.bms.bms_app.model.User;
import com.bms.bms_app.repository.UserRepository;
import com.bms.bms_app.security.JwtUtil;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    // CREATE
    public UserResponse createUser(UserRequest userRequest) {
        log.debug("Creating user with email={}", userRequest.getEmail());

        User user = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(Role.valueOf(userRequest.getRole().toUpperCase()))
                .phone(userRequest.getPhone())
                .status("ACTIVE")
                .build();
        User saved = userRepository.save(user);

        log.info("Created user id={} email={}", saved.getId(), saved.getEmail());
        return mapToResponse(saved);
    }

    // GET ALL
    public Iterable<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        Iterable<UserResponse> users = userRepository.findAll().stream().map(this::mapToResponse).toList();
        log.info("Fetched {} users", (users instanceof java.util.Collection ? ((java.util.Collection<?>) users).size() : "(unknown)"));
        return users;
    }

    // GET BY ID
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        log.info("Found user id={} email={}", user.getId(), user.getEmail());
        return mapToResponse(user);
    }

    // UPDATE
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        log.debug("Updating user id={}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        existingUser.setName(userRequest.getName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
        existingUser.setPhone(userRequest.getPhone());

        User updated = userRepository.save(existingUser);
        log.info("Updated user id={} email={}", updated.getId(), updated.getEmail());
        return mapToResponse(updated);
    }

    // DELETE
    public void deleteUser(Long id) {
        log.debug("Deleting user with id={}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(existingUser);

        log.info("Deleted user with id={}", id);
    }

    public UserResponse register(RegisterRequest registerRequest) {

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .phone(registerRequest.getPhone())
                .status("ACTIVE")
                .build();

        User saved = userRepository.save(user);

        return mapToResponse(saved);
    }

    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        return LoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .build();
    }
}
