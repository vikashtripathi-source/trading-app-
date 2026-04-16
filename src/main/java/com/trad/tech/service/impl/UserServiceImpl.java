package com.trad.tech.service.impl;

import com.trad.tech.model.User;
import com.trad.tech.repository.UserRepository;
import com.trad.tech.service.UserService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User registerUser(User user) {
    log.info("Registering new user with email: {}", user.getEmail());

    // Set user properties
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

    // Save to MongoDB
    User savedUser = userRepository.save(user);
    log.info("User successfully registered with ID: {}", savedUser.getId());

    return savedUser;
  }

  @Override
  public User findByEmail(String email) {
    log.info("Finding user by email: {}", email);

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
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

    return userRepository.existsByEmail(email);
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
  public void activateUser(String email) {
    log.info("Activating user: {}", email);

    User user = findByEmail(email);
    user.setActive(true);
    user.setLastUpdated(LocalDateTime.now());
    userRepository.save(user);

    log.info("User activated successfully: {}", email);
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
