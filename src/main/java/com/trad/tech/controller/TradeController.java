package com.trad.tech.controller;

import com.trad.tech.dto.ApiResponse;
import com.trad.tech.model.Trade;
import com.trad.tech.service.AuthService;
import com.trad.tech.service.TradeService;
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
@RequestMapping("/api/trades")
@RequiredArgsConstructor
@Validated
@Tag(name = "Trade API", description = "Trade management endpoints")
public class TradeController {

  private final TradeService tradeService;
  private final AuthService authService;

  @Operation(
      summary = "Get all trades for authenticated user",
      description = "Retrieves all trades for the authenticated user")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Trades retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping
  public ResponseEntity<ApiResponse<List<Trade>>> getUserTrades(
      @RequestHeader("Authorization") String authorization) {
    String token = authorization.replace("Bearer ", "");
    String userId = authService.getCurrentUser(token).getId();

    List<Trade> trades = tradeService.getUserTrades(userId);
    return ResponseEntity.ok(
        new ApiResponse<>(200, "Trades retrieved successfully", trades, "/api/trades"));
  }

  @Operation(
      summary = "Get specific trade by ID",
      description = "Retrieves a specific trade by its ID")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Trade retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Trade not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Trade>> getTradeById(
      @Parameter(description = "Trade ID") @PathVariable String id,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    Trade trade = tradeService.getTrade(id);
    return ResponseEntity.ok(
        new ApiResponse<>(200, "Trade retrieved successfully", trade, "/api/trades/" + id));
  }

  @Operation(
      summary = "Create new trade",
      description = "Creates a new trade for the authenticated user")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Trade created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid trade data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @PostMapping
  public ResponseEntity<ApiResponse<Trade>> createTrade(
      @Valid @RequestBody Trade trade, @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    String userId = authService.getCurrentUser(token).getId();

    Trade createdTrade = tradeService.createTradeForUser(userId, trade);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse<>(201, "Trade created successfully", createdTrade, "/api/trades"));
  }

  @Operation(summary = "Update existing trade", description = "Updates an existing trade")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Trade updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Trade not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid trade data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Trade>> updateTrade(
      @Parameter(description = "Trade ID") @PathVariable String id,
      @Valid @RequestBody Trade trade,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    Trade updatedTrade = tradeService.updateTrade(id, trade);
    return ResponseEntity.ok(
        new ApiResponse<>(200, "Trade updated successfully", updatedTrade, "/api/trades/" + id));
  }

  @Operation(summary = "Delete trade", description = "Deletes a trade by its ID")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Trade deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Trade not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTrade(
      @Parameter(description = "Trade ID") @PathVariable String id,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    tradeService.deleteTrade(id);
    return ResponseEntity.noContent().build();
  }
}
