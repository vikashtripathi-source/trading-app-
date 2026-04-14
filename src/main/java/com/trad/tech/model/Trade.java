package com.trad.tech.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trades")
public class Trade {
  @Id private String id;

  @NotBlank(message = "User ID is required")
  private String userId;

  @NotBlank(message = "Symbol is required")
  private String symbol;

  @NotBlank(message = "Type is required")
  @Pattern(regexp = "(?i)BUY|SELL", message = "Type must be either BUY or SELL")
  private String type; // BUY/SELL

  @NotNull(message = "Quantity is required")
  @Positive(message = "Quantity must be greater than 0")
  private int quantity;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.01", message = "Price must be greater than 0")
  private double price;

  private String status; // PENDING, EXECUTED, CANCELLED, REJECTED

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Additional getters/setters to support service implementations
  public String getId() {
    return id;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getType() {
    return type;
  }

  public int getQuantity() {
    return quantity;
  }

  public double getPrice() {
    return price;
  }

  public String getStatus() {
    return status;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setPrice(double price) {
    this.price = price;
  }
}
