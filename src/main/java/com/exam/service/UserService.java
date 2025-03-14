package com.exam.service;

import com.exam.dal.dto.RegisterRequest;
import com.exam.dal.dto.UserUpdateRequest;
import com.exam.dal.model.User;
import com.exam.dal.repository.UserRepository;
import com.exam.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User createUser(RegisterRequest registerRequest) {
        // Check if email is already taken
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already taken");
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build();

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserUpdateRequest updateRequest) {
        User user = getUserById(id);

        // Update fields if provided
        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            // Check if new email is already taken
            if (userRepository.findByEmail(updateRequest.getEmail()).isPresent()) {
                throw new RuntimeException("Email is already taken");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        if (updateRequest.getRole() != null) {
            user.setRole(updateRequest.getRole());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
