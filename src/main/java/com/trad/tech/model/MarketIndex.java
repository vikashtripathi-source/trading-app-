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
@Document(collection = "market_indices")
public class MarketIndex {
  @Id private String id;

  private String name;
  private double value;
  private double change;
  private double changePercent;
  private LocalDateTime lastUpdated;
}
