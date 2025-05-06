package com.pringdemo.springboot21.service;

import com.pringdemo.springboot21.dto.UserRequest;
import com.pringdemo.springboot21.dto.UserResponse;
import com.pringdemo.springboot21.entity.User;
import com.pringdemo.springboot21.exception.ResourceNotFoundException;
import com.pringdemo.springboot21.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // Create a new user
    public UserResponse createUser(UserRequest userRequest) {
        log.debug("Creating new user with email: {}", userRequest.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            log.error("Email {} already exists", userRequest.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user entity
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setDob(userRequest.getDob());
        user.setEmail(userRequest.getEmail());

        // Save to database
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return mapToUserResponse(savedUser);
    }

    // Get all users
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    

    // Get user by ID
    public UserResponse getUserById(UUID id) {
        log.debug("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        return mapToUserResponse(user);
    }

    // Update user
    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        log.debug("Updating user with ID: {}", id);
        
        // Find existing user
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        // Check if email is being changed to an existing email
        if (!user.getEmail().equals(userRequest.getEmail())){
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                log.error("Email {} already exists", userRequest.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }
        }

        // Update user details
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setDob(userRequest.getDob());
        user.setEmail(userRequest.getEmail());

        User updatedUser = userRepository.save(user);
        log.info("User with ID {} updated successfully", id);

        return mapToUserResponse(updatedUser);
    }

    // Delete user
    public void deleteUser(UUID id) {
        log.debug("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User not found with ID: {}", id);
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("User with ID {} deleted successfully", id);
    }

    // Helper method to convert User entity to UserResponse DTO
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setDob(user.getDob());
        response.setEmail(user.getEmail());
        return response;
    }
}