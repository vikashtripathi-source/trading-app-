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
@Document(collection = "market_data")
public class MarketDataEntity {
  @Id private String id;

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
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
