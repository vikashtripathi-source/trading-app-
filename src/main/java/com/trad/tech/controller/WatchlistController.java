package com.trad.tech.controller;

import com.trad.tech.dto.ApiResponse;
import com.trad.tech.model.Watchlist;
import com.trad.tech.service.AuthService;
import com.trad.tech.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlists")
@RequiredArgsConstructor
@Validated
@Tag(name = "Watchlist API", description = "Watchlist management endpoints")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final AuthService authService;

    @Operation(summary = "Get user watchlists", description = "Retrieves all watchlists for the authenticated user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Watchlists retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Watchlist>>> getUserWatchlists(
            @Parameter(description = "User ID") @PathVariable String userId,
            @RequestHeader("Authorization") String authorization) {
        
        String token = authorization.replace("Bearer ", "");
        String currentUserId = authService.getCurrentUser(token).getId();
        
        // Ensure user can only access their own watchlists
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "Access denied", null, "/api/watchlists/user/" + userId));
        }
        
        List<Watchlist> watchlists = watchlistService.getUserWatchlists(userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Watchlists retrieved successfully", watchlists, "/api/watchlists/user/" + userId));
    }

    @Operation(summary = "Create watchlist", description = "Creates a new watchlist for the authenticated user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Watchlist created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid watchlist data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Watchlist>> createWatchlist(
            @Valid @RequestBody Watchlist watchlist,
            @RequestHeader("Authorization") String authorization) {
        
        String token = authorization.replace("Bearer ", "");
        String userId = authService.getCurrentUser(token).getId();
        
        watchlist.setUserId(userId);
        Watchlist createdWatchlist = watchlistService.createWatchlist(watchlist);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Watchlist created successfully", createdWatchlist, "/api/watchlists"));
    }

    @Operation(summary = "Get watchlist by ID", description = "Retrieves a specific watchlist by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Watchlist retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Watchlist not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Watchlist>> getWatchlistById(
            @Parameter(description = "Watchlist ID") @PathVariable String id,
            @RequestHeader("Authorization") String authorization) {
        
        String token = authorization.replace("Bearer ", "");
        authService.validateToken(token); // Validate token
        
        Watchlist watchlist = watchlistService.getWatchlistById(id);
        
        // Ensure user can only access their own watchlist
        String currentUserId = authService.getCurrentUser(token).getId();
        if (!currentUserId.equals(watchlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "Access denied", null, "/api/watchlists/" + id));
        }
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Watchlist retrieved successfully", watchlist, "/api/watchlists/" + id));
    }

    @Operation(summary = "Add symbol to watchlist", description = "Adds a stock symbol to the watchlist")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Symbol added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Watchlist not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid symbol"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/{id}/symbols")
    public ResponseEntity<ApiResponse<Watchlist>> addSymbolToWatchlist(
            @Parameter(description = "Watchlist ID") @PathVariable String id,
            @Parameter(description = "Stock symbol") @RequestParam String symbol,
            @RequestHeader("Authorization") String authorization) {
        
        String token = authorization.replace("Bearer ", "");
        authService.validateToken(token); // Validate token
        
        // Verify ownership
        Watchlist watchlist = watchlistService.getWatchlistById(id);
        String currentUserId = authService.getCurrentUser(token).getId();
        if (!currentUserId.equals(watchlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "Access denied", null, "/api/watchlists/" + id + "/symbols"));
        }
        
        Watchlist updatedWatchlist = watchlistService.addSymbolToWatchlist(id, symbol);
        return ResponseEntity.ok(new ApiResponse<>(200, "Symbol added successfully", updatedWatchlist, "/api/watchlists/" + id + "/symbols"));
    }

    @Operation(summary = "Remove symbol from watchlist", description = "Removes a stock symbol from the watchlist")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Symbol removed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Watchlist not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid symbol"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}/symbols/{symbol}")
    public ResponseEntity<ApiResponse<Watchlist>> removeSymbolFromWatchlist(
            @Parameter(description = "Watchlist ID") @PathVariable String id,
            @Parameter(description = "Stock symbol") @PathVariable String symbol,
            @RequestHeader("Authorization") String authorization) {
        
        String token = authorization.replace("Bearer ", "");
        authService.validateToken(token); // Validate token
        
        // Verify ownership
        Watchlist watchlist = watchlistService.getWatchlistById(id);
        String currentUserId = authService.getCurrentUser(token).getId();
        if (!currentUserId.equals(watchlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "Access denied", null, "/api/watchlists/" + id + "/symbols/" + symbol));
        }
        
        Watchlist updatedWatchlist = watchlistService.removeSymbolFromWatchlist(id, symbol);
        return ResponseEntity.ok(new ApiResponse<>(200, "Symbol removed successfully", updatedWatchlist, "/api/watchlists/" + id + "/symbols/" + symbol));
    }

    @Operation(summary = "Delete watchlist", description = "Deletes a watchlist by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Watchlist deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Watchlist not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchlist(
            @Parameter(description = "Watchlist ID") @PathVariable String id,
            @RequestHeader("Authorization") String authorization) {
        
        String token = authorization.replace("Bearer ", "");
        authService.validateToken(token); // Validate token
        
        // Verify ownership
        Watchlist watchlist = watchlistService.getWatchlistById(id);
        String currentUserId = authService.getCurrentUser(token).getId();
        if (!currentUserId.equals(watchlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        watchlistService.deleteWatchlist(id);
        return ResponseEntity.noContent().build();
    }
}
