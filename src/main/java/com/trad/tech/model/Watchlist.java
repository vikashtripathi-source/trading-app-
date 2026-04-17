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
@Document(collection = "watchlists")
public class Watchlist {

  @Id private String id;

  private String userId;

  private String name;

  private List<String> symbols;

  private LocalDateTime createdDate;

  private LocalDateTime lastUpdated;

  private boolean isDefault;

  private boolean deleted;
}
