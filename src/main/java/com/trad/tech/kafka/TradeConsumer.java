package com.trad.tech.kafka;

import com.trad.tech.model.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TradeConsumer {

  private static final Logger logger = LoggerFactory.getLogger(TradeConsumer.class);
  
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @KafkaListener(topics = "${kafka.topics.trade-events}", groupId = "trading-group")
  public void consumeTradeEvent(Trade trade) {
    try {
      if (trade != null) {
        logger.info("Consumed Trade Event: {}", trade);
      } else {
        logger.warn("Received null trade event message");
      }
    } catch (Exception e) {
      logger.error("Error processing trade event message", e);
    }
  }

  @KafkaListener(topics = "${kafka.topics.trade-validation}", groupId = "trading-group")
  public void consumeTradeValidation(Trade trade) {
    try {
      if (trade != null) {
        logger.info("Consumed Trade for Validation: {}", trade);
        // Process trade validation logic
        validateTrade(trade);
      } else {
        logger.warn("Received null trade validation message");
      }
    } catch (Exception e) {
      logger.error("Error processing trade validation message", e);
    }
  }

  @KafkaListener(topics = "${kafka.topics.trade-execution}", groupId = "trading-group")
  public void consumeTradeExecution(Trade trade) {
    try {
      if (trade != null) {
        logger.info("Consumed Trade Execution: {}", trade);
        executeTrade(trade);
      } else {
        logger.warn("Received null trade execution message");
      }
    } catch (Exception e) {
      logger.error("Error processing trade execution message", e);
    }
  }

  @KafkaListener(topics = "${kafka.topics.trade-errors}", groupId = "trading-group")
  public void consumeTradeError(Trade trade) {
    try {
      if (trade != null) {
        logger.error("Consumed Trade Error: {}", trade);
        // Process trade error handling logic
        handleTradeError(trade);
      } else {
        logger.warn("Received null trade error message");
      }
    } catch (Exception e) {
      logger.error("Error processing trade error message", e);
    }
  }

  // Legacy method for backward compatibility
  @KafkaListener(topics = "${kafka.topics.trade-events}", groupId = "trading-group")
  public void consume(Trade trade) {
    consumeTradeEvent(trade);
  }

  private void validateTrade(Trade trade) {
    logger.info("Validating trade: {}", trade.getId());
    // Add your trade validation logic here
  }

  private void executeTrade(Trade trade) {
    logger.info("Executing trade: {}", trade.getId());
    // Add your trade execution logic here
  }

  private void handleTradeError(Trade trade) {
    logger.error("Handling trade error: {}", trade);
    // Add your trade error handling logic here
  }

  @KafkaListener(topics = "market-data", groupId = "trading-group")
  public void consume(String message) {

    // Example: message = "AAPL:182"
    String[] data = message.split(":");

    String symbol = data[0];
    String price = data[1];

    redisTemplate.opsForValue()
            .set("price:" + symbol, price);
  }
}
