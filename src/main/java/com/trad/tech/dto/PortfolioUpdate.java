package com.trad.tech.dto;

import lombok.Data;

@Data
public class PortfolioUpdate {

  private String userId;
  private double totalValue;
  private double dailyPnL;
  private long timestamp;
}
