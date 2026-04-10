package com.trad.tech.service;

import com.trad.tech.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService {
    
    String loginUser(String email, String password);
    
    void logoutUser(String token);
    
    User getCurrentUser(String token);
    
    boolean validateToken(String token);
    
    String generateToken(User user);
    
    String refreshToken(String token);
    
    String getEmailFromToken(String token);
    
    UserDetailsService getUserDetailsService();
}
