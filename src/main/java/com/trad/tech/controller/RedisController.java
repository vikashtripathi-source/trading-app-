package com.trad.tech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@Tag(name = "Redis Controller", description = "API endpoints for Redis cache operations")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/set")
    @Operation(summary = "Set stock price in Redis", description = "Sets a sample stock price for AAPL in Redis cache")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully set value in Redis"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public String set() {
        redisTemplate.opsForValue().set("stock:AAPL", 180);
        return "Saved";
    }

    @GetMapping("/get")
    @Operation(summary = "Get stock price from Redis", description = "Retrieves the stock price for AAPL from Redis cache")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved value from Redis"),
        @ApiResponse(responseCode = "404", description = "Value not found in Redis"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Object get() {
        return redisTemplate.opsForValue().get("stock:AAPL");
    }

    @GetMapping("/price/{symbol}")
    @Operation(summary = "Get price by symbol", description = "Retrieves the price for a specific stock symbol from Redis cache")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved price for symbol"),
        @ApiResponse(responseCode = "404", description = "Price not found for symbol"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Object getPrice(
            @Parameter(description = "Stock symbol", example = "AAPL", required = true)
            @PathVariable String symbol) {
        return redisTemplate.opsForValue().get("price:" + symbol);
    }
}
