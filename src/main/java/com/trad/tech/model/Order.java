package com.trad.tech.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

  @Id private String id;

  private String userId;

  private String symbol;

  private OrderType orderType;

  private OrderSide side;

  private int quantity;

  private double price;

  private double stopPrice;

  private OrderStatus status;

  private LocalDateTime createdDate;

  private LocalDateTime executedDate;

  private double executedPrice;

  private int executedQuantity;

  private String reason;

  public enum OrderType {
    MARKET,
    LIMIT,
    STOP_LOSS,
    STOP_LIMIT
  }

  public enum OrderSide {
    BUY,
    SELL
  }

  public enum OrderStatus {
    PENDING,
    EXECUTED,
    CANCELLED,
    REJECTED,
    PARTIALLY_FILLED
  }

  // Additional getters/setters to support service implementations
  public void setCreatedAt(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public void setExecutedDate(LocalDateTime executedDate) {
    this.executedDate = executedDate;
  }

  public void setExecutedPrice(double executedPrice) {
    this.executedPrice = executedPrice;
  }

  public void setExecutedQuantity(int executedQuantity) {
    this.executedQuantity = executedQuantity;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public void setType(OrderType orderType) {
    this.orderType = orderType;
  }

  public void setSide(OrderSide side) {
    this.side = side;
  }
}
