package com.bms.bms_app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bms.bms_app.dto.UserRequest;
import com.bms.bms_app.dto.UserResponse;
import com.bms.bms_app.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }   

    // CREATE
    @PostMapping()
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        return userService.creatUser(userRequest);
    }

    // GET ALL
    @GetMapping()
    public Iterable<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // UPDATE
    @PutMapping("/{id}")       
    public UserResponse updateUser(@PathVariable Long id,@Valid @RequestBody UserRequest userRequest) {
        return userService.updateUser(id, userRequest);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
