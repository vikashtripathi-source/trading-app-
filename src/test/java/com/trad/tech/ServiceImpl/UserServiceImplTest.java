package com.trad.tech.ServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.trad.tech.model.User;
import com.trad.tech.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserServiceImpl userService;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId("test123");
    testUser.setFirstName("Vikash");
    testUser.setLastName("Test");
    testUser.setEmail("vikash@example.com");
    testUser.setPassword("encodedPassword");
    testUser.setPhone("+1234567890");
    testUser.setDateOfBirth("1990-01-01");
  }

  @Test
  void testFindById_Success() {
    // Given
    when(userRepository.findById("test123")).thenReturn(Optional.of(testUser));

    // When
    User result = userService.findById("test123");

    // Then
    assertNotNull(result);
    assertEquals("test123", result.getId());
    assertEquals("Vikash", result.getFirstName());
    assertEquals("vikash@example.com", result.getEmail());
    verify(userRepository, times(1)).findById("test123");
  }

  @Test
  void testFindById_UserNotFound() {
    // Given
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        UsernameNotFoundException.class,
        () -> {
          userService.findById("nonexistent");
        });
    verify(userRepository, times(1)).findById("nonexistent");
  }

  @Test
  void testGetUserProfile_Success() {
    // Given
    when(userRepository.findById("test123")).thenReturn(Optional.of(testUser));

    // When
    User result = userService.getUserProfile("test123");

    // Then
    assertNotNull(result);
    assertEquals("test123", result.getId());
    assertEquals("Vikash", result.getFirstName());
    verify(userRepository, times(1)).findById("test123");
  }

  @Test
  void testFindByEmail_Success() {
    // Given
    when(userRepository.findByEmail("vikash@example.com")).thenReturn(Optional.of(testUser));

    // When
    User result = userService.findByEmail("vikash@example.com");

    // Then
    assertNotNull(result);
    assertEquals("vikash@example.com", result.getEmail());
    assertEquals("Vikash", result.getFirstName());
    verify(userRepository, times(1)).findByEmail("vikash@example.com");
  }

  @Test
  void testFindByEmail_UserNotFound() {
    // Given
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        UsernameNotFoundException.class,
        () -> {
          userService.findByEmail("nonexistent@example.com");
        });
    verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
  }
}
