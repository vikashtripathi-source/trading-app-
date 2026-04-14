package com.trad.tech.controller;

import com.trad.tech.dto.ApiResponse;
import com.trad.tech.service.AuthService;
import com.trad.tech.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Activation API", description = "User activation endpoints")
public class UserActivationController {

  private final UserService userService;
  private final AuthService authService;

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
  @PostMapping("/{email}/activate")
  public ResponseEntity<ApiResponse<String>> activateUser(
      @Parameter(description = "User email") @PathVariable String email) {

    try {
      userService.activateUser(email);
      return ResponseEntity.ok(
          new ApiResponse<>(
              200, "User activated successfully", email, "/api/users/" + email + "/activate"));
    } catch (Exception e) {
      return ResponseEntity.status(404)
          .body(
              new ApiResponse<>(404, "User not found", null, "/api/users/" + email + "/activate"));
    }
  }
}
