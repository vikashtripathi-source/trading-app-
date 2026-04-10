package com.trad.tech.service.impl;

import com.trad.tech.model.User;
import com.trad.tech.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User registerUser(User user) {
        log.info("Registering new user with email: {}", user.getEmail());
        
        // TODO: Implement actual database creation
        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setLastUpdated(LocalDateTime.now());
        user.setVerified(false);
        user.setActive(true);
        
        // Initialize default preferences if not set
        if (user.getPreferences() == null) {
            user.setPreferences(new User.UserPreferences());
        }
        
        // Initialize security if not set
        if (user.getSecurity() == null) {
            user.setSecurity(new User.UserSecurity());
        }
        
        return user;
    }
    
    @Override
    public User findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        
        // TODO: Implement actual database retrieval
        // For now, return mock user
        User user = new User();
        user.setId("user123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail(email);
        user.setPhone("+1234567890");
        user.setDateOfBirth("1990-01-01");
        user.setAddress(new User.Address("123 Main St", "New York", "NY", "10001", "USA", "12345"));
        user.setPreferences(new User.UserPreferences());
        user.setSecurity(new User.UserSecurity());
        user.setCreatedAt(LocalDateTime.now().minusDays(30));
        user.setLastUpdated(LocalDateTime.now());
        user.setVerified(true);
        user.setActive(true);
        
        // Set password for authentication
        if ("admin@gmail.com".equals(email)) {
            user.setPassword(passwordEncoder.encode("Pass@123"));
            user.setFirstName("Admin");
            user.setLastName("User");
            user.setId("admin123");
        } else if ("john@example.com".equals(email)) {
            user.setPassword(passwordEncoder.encode("password123"));
        } else {
            user.setPassword(passwordEncoder.encode("password123"));
        }
        
        return user;
    }
    
    @Override
    public User findById(String id) {
        log.info("Finding user by id: {}", id);
        
        // TODO: Implement actual database retrieval
        return findByEmail("john@example.com");
    }
    
    @Override
    public User updateUser(String id, User user) {
        log.info("Updating user with id: {}", id);
        
        // TODO: Implement actual database update
        user.setId(id);
        user.setLastUpdated(LocalDateTime.now());
        
        return user;
    }
    
    @Override
    public void deleteUser(String id) {
        log.info("Deleting user with id: {}", id);
        
        // TODO: Implement actual database deletion
    }
    
    @Override
    public User getUserProfile(String userId) {
        log.info("Getting user profile for: {}", userId);
        
        // TODO: Implement actual database retrieval
        return findById(userId);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        log.info("Checking if user exists by email: {}", email);
        
        // TODO: Implement actual database check
        // For now, return true for admin user to prevent duplicate registration
        return "admin@gmail.com".equals(email);
    }
    
    @Override
    public void incrementLoginAttempts(String email) {
        log.info("Incrementing login attempts for: {}", email);
        
        // TODO: Implement actual database update
    }
    
    @Override
    public void resetLoginAttempts(String email) {
        log.info("Resetting login attempts for: {}", email);
        
        // TODO: Implement actual database update
    }
    
    @Override
    public void lockUser(String email, LocalDateTime lockUntil) {
        log.info("Locking user {} until: {}", email, lockUntil);
        
        // TODO: Implement actual database update
    }
    
    @Override
    public void unlockUser(String email) {
        log.info("Unlocking user: {}", email);
        
        // TODO: Implement actual database update
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        
        User user = findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
}
