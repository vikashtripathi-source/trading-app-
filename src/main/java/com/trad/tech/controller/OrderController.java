package com.trad.tech.controller;

import com.trad.tech.dto.ApiResponse;
import com.trad.tech.model.Order;
import com.trad.tech.service.AuthService;
import com.trad.tech.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Order API", description = "Order management endpoints")
public class OrderController {

  private final OrderService orderService;
  private final AuthService authService;

  @Operation(
      summary = "Create new order",
      description = "Creates a new trading order for the authenticated user")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Order created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid order data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @PostMapping
  public ResponseEntity<ApiResponse<Order>> createOrder(
      @Valid @RequestBody Order order, @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    String userId = authService.getCurrentUser(token).getId();

    order.setUserId(userId);
    Order createdOrder = orderService.createOrder(order);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse<>(201, "Order created successfully", createdOrder, "/api/orders"));
  }

  @Operation(
      summary = "Get user orders",
      description = "Retrieves all orders for the authenticated user")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping("/user/current-user")
  public ResponseEntity<ApiResponse<List<Order>>> getCurrentUserOrders(
      @Parameter(description = "Order status filter") @RequestParam(required = false) String status,
      @RequestHeader("Authorization") String authorization) {

    System.out.println("DEBUG: getCurrentUserOrders endpoint called");
    System.out.println("DEBUG: Authorization header: " + authorization);
    
    String token = authorization.replace("Bearer ", "");
    String userId = authService.getCurrentUser(token).getId();
    
    System.out.println("DEBUG: Extracted userId: " + userId);
    System.out.println("DEBUG: Status filter: " + status);

    List<Order> orders = orderService.getUserOrders(userId, status);
    System.out.println("DEBUG: Retrieved " + orders.size() + " orders");
    
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Orders retrieved successfully", orders, "/api/orders/user/current-user"));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<ApiResponse<List<Order>>> getUserOrders(
      @Parameter(description = "User ID") @PathVariable String userId,
      @Parameter(description = "Order status filter") @RequestParam(required = false) String status,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    String currentUserId = authService.getCurrentUser(token).getId();

    // Ensure user can only access their own orders
    if (!currentUserId.equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ApiResponse<>(403, "Access denied", null, "/api/orders/user/" + userId));
    }

    List<Order> orders = orderService.getUserOrders(userId, status);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Orders retrieved successfully", orders, "/api/orders/user/" + userId));
  }

  @Operation(summary = "Cancel order", description = "Cancels a pending order")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Order cancelled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Order not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Order cannot be cancelled"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @PutMapping("/{id}/cancel")
  public ResponseEntity<ApiResponse<Order>> cancelOrder(
      @Parameter(description = "Order ID") @PathVariable String id,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    Order cancelledOrder = orderService.cancelOrder(id);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Order cancelled successfully", cancelledOrder, "/api/orders/" + id + "/cancel"));
  }
}
