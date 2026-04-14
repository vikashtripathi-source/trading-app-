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

    // Additional getters/setters to support service implementations
    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    public void setQuantity(int quantity) {
      this.quantity = quantity;
    }

    public void setAvgCost(double avgCost) {
      this.averagePrice = avgCost;
    }

    public void setCurrentPrice(double currentPrice) {
      this.currentPrice = currentPrice;
    }

    public void setMarketValue(double marketValue) {
      this.totalValue = marketValue;
    }

    public void setGain(double gain) {
      this.pnl = gain;
    }

    public void setGainPercent(double gainPercent) {
      this.pnlPercentage = gainPercent;
    }
  }

  // Additional getters/setters to support service implementations
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setTotalValue(double totalValue) {
    this.totalValue = totalValue;
  }

  public void setTotalGain(double totalGain) {
    this.totalPnL = totalGain;
  }

  public void setTotalGainPercent(double totalGainPercent) {
    this.dailyPnL = totalGainPercent;
  }

  public List<Holding> getHoldings() {
    return holdings;
  }

  public void setHoldings(List<Holding> holdings) {
    this.holdings = holdings;
  }

  public void setLastUpdated(long lastUpdated) {
    this.lastUpdated =
        java.time.LocalDateTime.ofEpochSecond(lastUpdated / 1000, 0, java.time.ZoneOffset.UTC);
  }
}
