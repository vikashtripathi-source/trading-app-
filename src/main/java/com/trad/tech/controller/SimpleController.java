package com.trad.tech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Simple API", description = "Simple endpoints for testing")
public class SimpleController {

    @Operation(summary = "Simple test endpoint", description = "Returns a simple success message to verify the API is working")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved simple message"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/simple")
    public ResponseEntity<String> simple() {
        return ResponseEntity.ok("Simple endpoint works!");
    }
}
