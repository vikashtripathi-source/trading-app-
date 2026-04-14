package com.trad.tech.controller;

import com.trad.tech.dto.ApiResponse;
import com.trad.tech.service.AnalyticsService;
import com.trad.tech.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Validated
@Tag(name = "Analytics API", description = "Trading analytics endpoints")
public class AnalyticsController {

  private final AnalyticsService analyticsService;
  private final AuthService authService;

  @Operation(
      summary = "Get trading statistics",
      description = "Retrieves comprehensive trading statistics for a user")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully"),
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
  @GetMapping("/user/{userId}/statistics")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getTradingStatistics(
      @Parameter(description = "User ID") @PathVariable String userId,
      @Parameter(description = "Time period") @RequestParam(defaultValue = "1M") String period,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    String currentUserId = authService.getCurrentUser(token).getId();

    // Ensure user can only access their own analytics
    if (!currentUserId.equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(
              new ApiResponse<>(
                  403, "Access denied", null, "/api/analytics/user/" + userId + "/statistics"));
    }

    Map<String, Object> statistics = analyticsService.getTradingStatistics(userId, period);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200,
            "Statistics retrieved successfully",
            statistics,
            "/api/analytics/user/" + userId + "/statistics"));
  }

  @Operation(
      summary = "Get performance metrics",
      description = "Retrieves performance metrics for a user")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Performance metrics retrieved successfully"),
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
  @GetMapping("/user/{userId}/performance")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics(
      @Parameter(description = "User ID") @PathVariable String userId,
      @Parameter(description = "Time period") @RequestParam(defaultValue = "1M") String period,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    String currentUserId = authService.getCurrentUser(token).getId();

    // Ensure user can only access their own analytics
    if (!currentUserId.equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(
              new ApiResponse<>(
                  403, "Access denied", null, "/api/analytics/user/" + userId + "/performance"));
    }

    Map<String, Object> performance = analyticsService.getPerformanceMetrics(userId, period);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200,
            "Performance metrics retrieved successfully",
            performance,
            "/api/analytics/user/" + userId + "/performance"));
  }

  @Operation(
      summary = "Get trade analysis",
      description = "Retrieves detailed trade analysis for a user")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Trade analysis retrieved successfully"),
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
  @GetMapping("/user/{userId}/trade-analysis")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getTradeAnalysis(
      @Parameter(description = "User ID") @PathVariable String userId,
      @Parameter(description = "Time period") @RequestParam(defaultValue = "1M") String period,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    String currentUserId = authService.getCurrentUser(token).getId();

    // Ensure user can only access their own analytics
    if (!currentUserId.equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(
              new ApiResponse<>(
                  403, "Access denied", null, "/api/analytics/user/" + userId + "/trade-analysis"));
    }

    Map<String, Object> analysis = analyticsService.getTradeAnalysis(userId, period);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200,
            "Trade analysis retrieved successfully",
            analysis,
            "/api/analytics/user/" + userId + "/trade-analysis"));
  }
}
