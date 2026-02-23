package com.bms.bms_app.service;

import org.springframework.stereotype.Service;

import com.bms.bms_app.dto.UserRequest;
import com.bms.bms_app.dto.UserResponse;
import com.bms.bms_app.exception.ResourceNotFoundException;
import com.bms.bms_app.model.User;
import com.bms.bms_app.repository.UserRepository;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public UserResponse creatUser(UserRequest userRequest) {
        User user = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .role(userRequest.getRole())
                .phone(userRequest.getPhone())
                .status("ACTIVE")
                .build();


        User saved =  userRepository.save(user);

        return mapToResponse(saved);
    }



    // GET ALL
    public Iterable<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // GET BY ID
    public UserResponse getUserById(Long id) {  
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }       

    // UPDATE
    public UserResponse updateUser(Long id, UserRequest userRequest) { 

        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        existingUser.setName(userRequest.getName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setRole(userRequest.getRole());
        existingUser.setPhone(userRequest.getPhone());

        return mapToResponse(userRepository.save(existingUser));
    }

    // DELETE
    public void deleteUser(Long id) {

        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(existingUser);
        
    }

}
