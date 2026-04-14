package com.trad.tech.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stock_symbols")
public class StockSymbol {
  @Id private String id;

  private String symbol;
  private String name;
  private String type; // STOCK, ETF, etc.
  private String exchange;
  private String sector;
  private String industry;
  private boolean active;
}
