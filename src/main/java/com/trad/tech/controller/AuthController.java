package com.trad.tech.controller;

import com.trad.tech.dto.ApiResponse;
import com.trad.tech.dto.LoginRequest;
import com.trad.tech.dto.LoginResponse;
import com.trad.tech.dto.RegisterRequest;
import com.trad.tech.model.User;
import com.trad.tech.service.AuthService;
import com.trad.tech.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "User authentication endpoints")
public class AuthController {

  private final UserService userService;
  private final AuthService authService;

  @Operation(summary = "User registration", description = "Register a new user")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "User registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid user data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Email already exists")
      })
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
    if (userService.existsByEmail(request.getEmail())) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new ApiResponse<>(409, "Email already exists", null, "/api/auth/register"));
    }

    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPassword(request.getPassword());
    user.setPhone(request.getPhone());
    user.setDateOfBirth(request.getDateOfBirth());
    user.setAddress(request.getAddress());

    User registeredUser = userService.registerUser(user);

    // Remove password from response
    registeredUser.setPassword(null);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new ApiResponse<>(
                201, "User registered successfully", registeredUser, "/api/auth/register"));
  }

  @Operation(summary = "User login", description = "Authenticate user and return token")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials")
      })
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    String token = authService.loginUser(request.getEmail(), request.getPassword());
    User user = userService.findByEmail(request.getEmail());

    // Remove password from response
    user.setPassword(null);

    LoginResponse loginResponse = new LoginResponse(user, token);

    return ResponseEntity.ok()
        .body(new ApiResponse<>(200, "Login successful", loginResponse, "/api/auth/login"));
  }

  @Operation(summary = "User logout", description = "Logout user and invalidate token")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Logout successful")
      })
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestHeader("Authorization") String authorization) {
    String token = authorization.replace("Bearer ", "");
    authService.logoutUser(token);

    return ResponseEntity.ok()
        .body(new ApiResponse<>(200, "Logout successful", null, "/api/auth/logout"));
  }

  @Operation(summary = "Get user profile", description = "Get current user profile")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<User>> getProfile(
      @RequestHeader("Authorization") String authorization) {
    String token = authorization.replace("Bearer ", "");
    User user = authService.getCurrentUser(token);

    // Remove password from response
    user.setPassword(null);

    return ResponseEntity.ok()
        .body(new ApiResponse<>(200, "Profile retrieved successfully", user, "/api/auth/profile"));
  }

  @Operation(summary = "Activate user", description = "Activate a user account")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User activated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found")
      })
  @GetMapping("/activate/{email}")
  public ResponseEntity<ApiResponse<String>> activateUser(
      @Parameter(description = "User email") @PathVariable String email) {

    try {
      userService.activateUser(email);
      return ResponseEntity.ok(
          new ApiResponse<>(
              200, "User activated successfully", email, "/api/auth/activate/" + email));
    } catch (Exception e) {
      return ResponseEntity.status(404)
          .body(
              new ApiResponse<>(
                  404, "User not found: " + e.getMessage(), null, "/api/auth/activate/" + email));
    }
  }
}
