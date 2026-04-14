package com.trad.tech.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "portfolios")
public class Portfolio {

  @Id private String id;

  private String userId;

  private String name;

  private double totalValue;

  private double availableBalance;

  private double totalInvested;

  private double totalPnL;

  private double dailyPnL;

  private LocalDateTime lastUpdated;

  private List<Holding> holdings;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Holding {
    private String symbol;
    private int quantity;
    private double averagePrice;
    private double currentPrice;
    private double totalValue;
    private double pnl;
    private double pnlPercentage;
  }
}
