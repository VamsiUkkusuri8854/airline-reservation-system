/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.service;

import com.airline.exception.InvalidInputException;
import com.airline.exception.UserAlreadyExistsException;
import com.airline.exception.UserNotFoundException;
import com.airline.model.User;
import com.airline.repository.UserRepository;
import com.airline.util.InputValidator;
import com.airline.util.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Service class managing User registrations, authentications, and credentials validation using Spring Data.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user.
     */
    public User registerUser(String username, String password, String name, String email, String phone, String role) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            name == null || name.trim().isEmpty() ||
            email == null || phone == null) {
            throw new InvalidInputException("All fields are mandatory.");
        }

        if (username.length() < 3) {
            throw new InvalidInputException("Username must be at least 3 characters.");
        }

        if (password.length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters.");
        }

        if (!InputValidator.isValidEmail(email)) {
            throw new InvalidInputException("Invalid email formatting.");
        }

        if (!InputValidator.isValidPhone(phone)) {
            throw new InvalidInputException("Phone number must contain between 10 to 15 digits.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username '" + username + "' is already registered.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email '" + email + "' is already registered.");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        String finalRole = "ADMIN".equalsIgnoreCase(role) ? "ADMIN" : "CUSTOMER";

        User newUser = new User(username, hashedPassword, name, email, phone, finalRole);
        User savedUser = userRepository.save(newUser);

        Logger.info("User registered successfully: " + username);
        return savedUser;
    }

    /**
     * Authenticates credentials.
     */
    public User loginUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            throw new InvalidInputException("Username and password cannot be empty.");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent() || !BCrypt.checkpw(password, userOpt.get().getPassword())) {
            Logger.warn("Failed login attempt for username: " + username);
            throw new UserNotFoundException("Invalid username or password.");
        }

        Logger.info("User logged in successfully: " + username);
        return userOpt.get();
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));
    }

    /**
     * Updates an existing user's profile information.
     */
    public User updateUserProfile(int userId, String name, String email, String phone, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
        User user = userOpt.get();
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            throw new InvalidInputException("Name, Email, and Phone number are mandatory fields.");
        }
        if (!InputValidator.isValidEmail(email)) {
            throw new InvalidInputException("Invalid email formatting.");
        }
        if (!InputValidator.isValidPhone(phone)) {
            throw new InvalidInputException("Phone number must contain between 10 to 15 digits.");
        }

        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPhone(phone.trim());

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 6) {
                throw new InvalidInputException("New password must be at least 6 characters.");
            }
            user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
        }

        User updatedUser = userRepository.save(user);
        Logger.info("Profile successfully updated for user: " + user.getUsername());
        return updatedUser;
    }

    public long getTotalUsersCount() {
        return userRepository.count();
    }

    /**
     * Setup default administrator account automatically on boot-up.
     */
    @PostConstruct
    public void setupDefaultAdmin() {
        try {
            Optional<User> admin = userRepository.findByUsername("admin");
            if (!admin.isPresent()) {
                registerUser("admin", "admin123", "System Administrator", "admin@airline.com", "+919999999999", "ADMIN");
                Logger.info("Default system admin created successfully.");
            }
        } catch (UserAlreadyExistsException e) {
            // Already exists, safe to ignore
        } catch (Exception e) {
            Logger.error("Failed to setup default admin profile", e);
        }
    }
}
