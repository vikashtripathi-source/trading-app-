package com.trad.tech.controller;

import com.trad.tech.dto.ApiResponse;
import com.trad.tech.model.Order;
import com.trad.tech.model.Trade;
import com.trad.tech.model.User;
import com.trad.tech.service.AuthService;
import com.trad.tech.service.OrderService;
import com.trad.tech.service.TradeService;
import com.trad.tech.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin API", description = "Admin management endpoints")
public class AdminController {

  private final UserService userService;
  private final TradeService tradeService;
  private final OrderService orderService;
  private final AuthService authService;

  @Operation(summary = "Get all users", description = "Retrieves all users (admin only)")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
      @RequestHeader("Authorization") String authorization) {
    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    // TODO: Implement actual database retrieval
    List<User> users = List.of(); // For now, return empty list
    return ResponseEntity.ok(
        new ApiResponse<>(200, "Users retrieved successfully", users, "/api/admin/users"));
  }

  @Operation(
      summary = "Get user by ID",
      description = "Retrieves a specific user by ID (admin only)")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @GetMapping("/users/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<User>> getUserById(
      @Parameter(description = "User ID") @PathVariable String id,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    User user = userService.findById(id);
    // Remove password from response
    user.setPassword(null);

    return ResponseEntity.ok(
        new ApiResponse<>(200, "User retrieved successfully", user, "/api/admin/users/" + id));
  }

  @Operation(summary = "Update user", description = "Updates a user (admin only)")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @PutMapping("/users/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<User>> updateUser(
      @Parameter(description = "User ID") @PathVariable String id,
      @RequestBody User user,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    User updatedUser = userService.updateUser(id, user);
    // Remove password from response
    updatedUser.setPassword(null);

    return ResponseEntity.ok(
        new ApiResponse<>(200, "User updated successfully", updatedUser, "/api/admin/users/" + id));
  }

  @Operation(summary = "Delete user", description = "Deletes a user (admin only)")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "User deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "User ID") @PathVariable String id,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Get all trades", description = "Retrieves all trades (admin only)")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Trades retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @GetMapping("/trades")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<List<Trade>>> getAllTrades(
      @RequestHeader("Authorization") String authorization) {
    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    List<Trade> trades = tradeService.getAllTrades();
    return ResponseEntity.ok(
        new ApiResponse<>(200, "Trades retrieved successfully", trades, "/api/admin/trades"));
  }

  @Operation(summary = "Get all orders", description = "Retrieves all orders (admin only)")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @GetMapping("/orders")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<List<Order>>> getAllOrders(
      @RequestHeader("Authorization") String authorization) {
    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    // TODO: Implement actual database retrieval
    List<Order> orders = List.of(); // For now, return empty list
    return ResponseEntity.ok(
        new ApiResponse<>(200, "Orders retrieved successfully", orders, "/api/admin/orders"));
  }

  @Operation(
      summary = "Get system statistics",
      description = "Retrieves system statistics (admin only)")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "System statistics retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @GetMapping("/system/stats")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats(
      @RequestHeader("Authorization") String authorization) {
    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    Map<String, Object> stats = new HashMap<>();
    stats.put("totalUsers", 1000);
    stats.put("activeUsers", 750);
    stats.put("totalTrades", 50000);
    stats.put("totalOrders", 25000);
    stats.put("systemUptime", "99.9%");
    stats.put("lastUpdated", System.currentTimeMillis());

    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "System statistics retrieved successfully", stats, "/api/admin/system/stats"));
  }
}
