package com.trad.tech.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

  private int status;
  private String message;
  private T data;
  private String path;
  private LocalDateTime timestamp;

  public ApiResponse(int status, String message, T data, String path) {
    this.status = status;
    this.message = message;
    this.data = data;
    this.path = path;
    this.timestamp = LocalDateTime.now();
  }
}
