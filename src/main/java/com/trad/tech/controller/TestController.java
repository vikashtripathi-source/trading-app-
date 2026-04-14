package com.trad.tech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test API", description = "Health check and testing endpoints")
public class TestController {

  @Operation(
      summary = "Health check endpoint",
      description = "Returns the health status of the application")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy"),
        @ApiResponse(responseCode = "503", description = "Service unavailable")
      })
  @GetMapping("/health")
  public String health() {
    return "Application is healthy!";
  }

  @Operation(summary = "Ping endpoint", description = "Simple ping-pong test for connectivity")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully pinged the server"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping("/ping")
  public String ping() {
    return "pong";
  }
}
