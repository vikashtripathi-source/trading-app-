package com.trad.tech.controller;

import com.trad.tech.dto.ApiResponse;
import com.trad.tech.model.Portfolio;
import com.trad.tech.service.AuthService;
import com.trad.tech.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Portfolio API", description = "Portfolio management endpoints")
public class PortfolioController {

  private final PortfolioService portfolioService;
  private final AuthService authService;

  @Operation(
      summary = "Get user portfolio",
      description = "Retrieves the complete portfolio for a user")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Portfolio retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Portfolio not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @GetMapping("/user/{userId}")
  public ResponseEntity<ApiResponse<Portfolio>> getUserPortfolio(
      @Parameter(description = "User ID") @PathVariable String userId,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    String currentUserId = authService.getCurrentUser(token).getId();

    // Ensure user can only access their own portfolio
    if (!currentUserId.equals(userId)) {
      return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
          .body(new ApiResponse<>(403, "Access denied", null, "/api/portfolios/user/" + userId));
    }

    Portfolio portfolio = portfolioService.getUserPortfolio(userId);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Portfolio retrieved successfully", portfolio, "/api/portfolios/user/" + userId));
  }

  @Operation(
      summary = "Get portfolio performance",
      description = "Gets portfolio performance metrics")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Performance data retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Portfolio not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @GetMapping("/user/{userId}/performance")
  public ResponseEntity<ApiResponse<Object>> getPortfolioPerformance(
      @Parameter(description = "User ID") @PathVariable String userId,
      @Parameter(description = "Time period") @RequestParam(defaultValue = "1M") String period,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    String currentUserId = authService.getCurrentUser(token).getId();

    // Ensure user can only access their own portfolio
    if (!currentUserId.equals(userId)) {
      return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
          .body(
              new ApiResponse<>(
                  403, "Access denied", null, "/api/portfolios/user/" + userId + "/performance"));
    }

    Object performance = portfolioService.getPortfolioPerformance(userId, period);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200,
            "Performance data retrieved successfully",
            performance,
            "/api/portfolios/user/" + userId + "/performance"));
  }

  @Operation(
      summary = "Get portfolio holdings",
      description = "Retrieves all holdings in the portfolio")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Holdings retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Portfolio not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied")
      })
  @GetMapping("/user/{userId}/holdings")
  public ResponseEntity<ApiResponse<List<Portfolio.Holding>>> getPortfolioHoldings(
      @Parameter(description = "User ID") @PathVariable String userId,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    String currentUserId = authService.getCurrentUser(token).getId();

    // Ensure user can only access their own portfolio
    if (!currentUserId.equals(userId)) {
      return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
          .body(
              new ApiResponse<>(
                  403, "Access denied", null, "/api/portfolios/user/" + userId + "/holdings"));
    }

    List<Portfolio.Holding> holdings = portfolioService.getPortfolioHoldings(userId);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200,
            "Holdings retrieved successfully",
            holdings,
            "/api/portfolios/user/" + userId + "/holdings"));
  }
}
