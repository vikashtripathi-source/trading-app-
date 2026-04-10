package com.trad.tech.service;

import com.trad.tech.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;

public interface UserService extends UserDetailsService {
    
    User registerUser(User user);
    
    User findByEmail(String email);
    
    User findById(String id);
    
    User updateUser(String id, User user);
    
    void deleteUser(String id);
    
    User getUserProfile(String userId);
    
    boolean existsByEmail(String email);
    
    void incrementLoginAttempts(String email);
    
    void resetLoginAttempts(String email);
    
    void lockUser(String email, LocalDateTime lockUntil);
    
    void unlockUser(String email);
}
