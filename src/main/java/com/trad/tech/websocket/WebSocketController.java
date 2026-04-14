package com.trad.tech.websocket;

import com.trad.tech.dto.MarketDataUpdate;
import com.trad.tech.dto.OrderStatusUpdate;
import com.trad.tech.dto.PortfolioUpdate;
import com.trad.tech.dto.WebSocketMessage;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

  private final SimpMessagingTemplate messagingTemplate;

  // Store user subscriptions
  private final Map<String, String> userSubscriptions = new ConcurrentHashMap<>();

  @MessageMapping("/subscribe")
  public void handleSubscription(@Payload WebSocketMessage message) {
    log.info("Received subscription message: {}", message);

    if ("subscribe".equals(message.getAction())) {
      String userId = message.getUserId();
      if (message.getTopics() != null) {
        for (String topic : message.getTopics()) {
          userSubscriptions.put(userId + ":" + topic, topic);
          log.info("User {} subscribed to topic: {}", userId, topic);
        }
      }
    }
  }

  // Methods to send real-time updates
  public void sendMarketDataUpdate(String symbol, double price, double change) {
    MarketDataUpdate update = new MarketDataUpdate();
    update.setSymbol(symbol);
    update.setPrice(price);
    update.setChange(change);
    update.setTimestamp(System.currentTimeMillis());

    WebSocketMessage message = new WebSocketMessage();
    message.setType("market-data");
    message.setData(update);
    message.setTimestamp(LocalDateTime.now());

    // Send to all users subscribed to market-data
    messagingTemplate.convertAndSend("/topic/market-data", message);
    log.info("Sent market data update for symbol: {}", symbol);
  }

  public void sendPortfolioUpdate(String userId, double totalValue, double dailyPnL) {
    PortfolioUpdate update = new PortfolioUpdate();
    update.setUserId(userId);
    update.setTotalValue(totalValue);
    update.setDailyPnL(dailyPnL);
    update.setTimestamp(System.currentTimeMillis());

    WebSocketMessage message = new WebSocketMessage();
    message.setType("portfolio-update");
    message.setData(update);
    message.setTimestamp(LocalDateTime.now());

    // Send to specific user
    messagingTemplate.convertAndSend("/topic/user/" + userId, message);
    log.info("Sent portfolio update for user: {}", userId);
  }

  public void sendOrderStatusUpdate(
      String userId, String orderId, String status, double executedPrice) {
    OrderStatusUpdate update = new OrderStatusUpdate();
    update.setOrderId(orderId);
    update.setStatus(status);
    update.setExecutedPrice(executedPrice);
    update.setTimestamp(System.currentTimeMillis());

    WebSocketMessage message = new WebSocketMessage();
    message.setType("order-status");
    message.setData(update);
    message.setTimestamp(LocalDateTime.now());

    // Send to specific user
    messagingTemplate.convertAndSend("/topic/user/" + userId, message);
    log.info("Sent order status update for order: {}", orderId);
  }
}
