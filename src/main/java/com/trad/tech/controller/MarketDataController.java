package com.trad.tech.controller;

import com.trad.tech.dto.ApiResponse;
import com.trad.tech.model.MarketData;
import com.trad.tech.service.AuthService;
import com.trad.tech.service.MarketDataService;
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
@RequestMapping("/api/market")
@RequiredArgsConstructor
@Validated
@Tag(name = "Market Data API", description = "Market data endpoints")
public class MarketDataController {

  private final MarketDataService marketDataService;
  private final AuthService authService;

  @Operation(
      summary = "Get market data for symbol",
      description = "Retrieves current market data for a specific stock symbol")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Market data retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Symbol not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping("/data/{symbol}")
  public ResponseEntity<ApiResponse<MarketData>> getMarketData(
      @Parameter(description = "Stock symbol") @PathVariable String symbol,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    MarketData marketData = marketDataService.getMarketData(symbol);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Market data retrieved successfully", marketData, "/api/market/data/" + symbol));
  }

  @Operation(
      summary = "Get batch market data",
      description = "Retrieves market data for multiple stock symbols")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Market data retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid symbols"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping("/data/batch")
  public ResponseEntity<ApiResponse<List<MarketData>>> getBatchMarketData(
      @Parameter(description = "Comma-separated stock symbols") @RequestParam String symbols,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    List<String> symbolList = List.of(symbols.split(","));
    List<MarketData> marketDataList = marketDataService.getBatchMarketData(symbolList);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Market data retrieved successfully", marketDataList, "/api/market/data/batch"));
  }

  @Operation(summary = "Get market indices", description = "Retrieves major market indices data")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Indices data retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping("/indices")
  public ResponseEntity<ApiResponse<Object>> getMarketIndices(
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    Object indices = marketDataService.getMarketIndices();
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Indices data retrieved successfully", indices, "/api/market/indices"));
  }

  @Operation(summary = "Get top gainers", description = "Retrieves top gaining stocks")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Top gainers retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping("/top-gainers")
  public ResponseEntity<ApiResponse<List<MarketData>>> getTopGainers(
      @Parameter(description = "Number of results") @RequestParam(defaultValue = "10") int limit,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    List<MarketData> topGainers = marketDataService.getTopGainers(limit);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Top gainers retrieved successfully", topGainers, "/api/market/top-gainers"));
  }

  @Operation(summary = "Get top losers", description = "Retrieves top losing stocks")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Top losers retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping("/top-losers")
  public ResponseEntity<ApiResponse<List<MarketData>>> getTopLosers(
      @Parameter(description = "Number of results") @RequestParam(defaultValue = "10") int limit,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    List<MarketData> topLosers = marketDataService.getTopLosers(limit);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Top losers retrieved successfully", topLosers, "/api/market/top-losers"));
  }

  @Operation(summary = "Search symbols", description = "Search for stock symbols by name or symbol")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid search query"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized")
      })
  @GetMapping("/search")
  public ResponseEntity<ApiResponse<List<Object>>> searchSymbols(
      @Parameter(description = "Search query") @RequestParam String query,
      @Parameter(description = "Number of results") @RequestParam(defaultValue = "10") int limit,
      @RequestHeader("Authorization") String authorization) {

    String token = authorization.replace("Bearer ", "");
    authService.validateToken(token); // Validate token

    List<Object> results = marketDataService.searchSymbols(query, limit);
    return ResponseEntity.ok(
        new ApiResponse<>(
            200, "Search results retrieved successfully", results, "/api/market/search"));
  }
}
