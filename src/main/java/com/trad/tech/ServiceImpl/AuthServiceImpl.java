package com.trad.tech.ServiceImpl;

import com.trad.tech.model.User;
import com.trad.tech.service.AuthService;
import com.trad.tech.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;

  @Value("${jwt.secret:default-secret-key-for-trading-app}")
  private String jwtSecret;

  @Value("${jwt.expiration:86400000}") // 24 hours
  private long jwtExpiration;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  @Override
  public String loginUser(String email, String password) {
    log.info("Attempting to login user: {}", email);

    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(email, password));

      // Get the email from authentication and find the user from database
      String userEmail = authentication.getName();
      User user = userService.findByEmail(userEmail);
      return generateToken(user);

    } catch (AuthenticationException e) {
      log.error("Authentication failed for user: {}", email, e);
      throw new RuntimeException("Invalid credentials");
    }
  }

  @Override
  public void logoutUser(String token) {
    log.info("Logging out user with token");

    // TODO: Implement token invalidation (e.g., using Redis blacklist)
    // For now, just log the logout
  }

  @Override
  public User getCurrentUser(String token) {
    log.info("Getting current user from token");

    if (!validateToken(token)) {
      throw new RuntimeException("Invalid token");
    }

    String email = getEmailFromToken(token);
    User user = userService.findByEmail(email);

    // Debug logging
    String userIdFromToken = getUserIdFromToken(token);
    log.info(
        "Token userId: {}, Database userId: {}, Email: {}", userIdFromToken, user.getId(), email);

    return user;
  }

  @Override
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      log.error("Token validation failed", e);
      return false;
    }
  }

  @Override
  public String generateToken(User user) {
    log.info("Generating token for user: {}", user.getEmail());

    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());
    claims.put("email", user.getEmail());

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getEmail())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String refreshToken(String token) {
    log.info("Refreshing token");

    if (!validateToken(token)) {
      throw new RuntimeException("Invalid token");
    }

    String email = getEmailFromToken(token);
    User user = userService.findByEmail(email);

    return generateToken(user);
  }

  @Override
  public String getEmailFromToken(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();

      return claims.getSubject();
    } catch (Exception e) {
      log.error("Error extracting email from token", e);
      return null;
    }
  }

  public String getUserIdFromToken(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();

      return claims.get("userId", String.class);
    } catch (Exception e) {
      log.error("Error extracting userId from token", e);
      return null;
    }
  }

  @Override
  public UserDetailsService getUserDetailsService() {
    return userService;
  }
}
