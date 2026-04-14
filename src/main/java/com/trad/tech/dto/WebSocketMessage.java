package com.trad.tech.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class WebSocketMessage {

  private String action; // subscribe, unsubscribe
  private List<String> topics; // market-data, portfolio-updates, order-status
  private String userId;
  private String type; // market-data, portfolio-update, order-status
  private Object data;
  private LocalDateTime timestamp;
}
