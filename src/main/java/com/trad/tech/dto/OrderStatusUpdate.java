package com.trad.tech.dto;

import lombok.Data;

@Data
public class OrderStatusUpdate {

  private String orderId;
  private String status;
  private double executedPrice;
  private long timestamp;
}
