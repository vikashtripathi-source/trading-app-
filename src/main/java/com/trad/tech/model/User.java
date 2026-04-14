package com.trad.tech.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

  @Id private String id;

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String phone;
  private String dateOfBirth;

  private Address address;
  private UserPreferences preferences;
  private UserSecurity security;

  private LocalDateTime createdAt;
  private LocalDateTime lastUpdated;

  @Field("verified")
  private boolean isVerified;

  @Field("active")
  private boolean isActive;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String postalCode;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserPreferences {
    private boolean darkMode = false;
    private String language = "en";
    private String timezone = "UTC";
    private boolean notifications = true;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserSecurity {
    private boolean twoFactorEnabled = false;
    private int loginAttempts = 0;
    private LocalDateTime lockUntil;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return security == null
        || security.getLockUntil() == null
        || security.getLockUntil().isBefore(LocalDateTime.now());
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isActive;
  }
}
