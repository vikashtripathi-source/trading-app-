package com.trad.tech.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketData {

  private String symbol;

  private double currentPrice;

  private double openPrice;

  private double highPrice;

  private double lowPrice;

  private double previousClose;

  private double change;

  private double changePercentage;

  private long volume;

  private long averageVolume;

  private double marketCap;

  private double peRatio;

  private long timestamp;
}
