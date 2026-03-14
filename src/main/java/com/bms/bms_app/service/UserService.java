package com.bms.bms_app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bms.bms_app.dto.LoginRequest;
import com.bms.bms_app.dto.LoginResponse;
import com.bms.bms_app.dto.RegisterRequest;
import com.bms.bms_app.dto.UserRequest;
import com.bms.bms_app.dto.UserResponse;
import com.bms.bms_app.exception.InvalidCredentialsException;
import com.bms.bms_app.exception.ResourceNotFoundException;
import com.bms.bms_app.model.User;
import com.bms.bms_app.repository.UserRepository;
import com.bms.bms_app.security.JwtUtil;

@Service
public class UserService {

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
                .role(user.getRole())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    // CREATE
    public UserResponse createUser(UserRequest userRequest) {
        User user = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .role(userRequest.getRole())
                .phone(userRequest.getPhone())
                .status("ACTIVE")
                .build();

        User saved = userRepository.save(user);

        return mapToResponse(saved);
    }

    // GET ALL
    public Iterable<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // GET BY ID
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    // UPDATE
    public UserResponse updateUser(Long id, UserRequest userRequest) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        existingUser.setName(userRequest.getName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setRole(userRequest.getRole());
        existingUser.setPhone(userRequest.getPhone());

        return mapToResponse(userRepository.save(existingUser));
    }

    // DELETE
    public void deleteUser(Long id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(existingUser);

    }

    public UserResponse register(RegisterRequest registerRequest) {

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
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

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        return LoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .build();
    }
}
