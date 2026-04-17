package com.trad.tech.kafka;

import com.trad.tech.model.Order;
import com.trad.tech.model.Trade;
import com.trad.tech.model.Portfolio;
import com.trad.tech.model.Watchlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EnhancedTradeConsumer {

  private static final Logger logger = LoggerFactory.getLogger(EnhancedTradeConsumer.class);
  
  @Autowired(required = false)
  private RedisTemplate<String, Object> redisTemplate;

  @KafkaListener(topics = "${kafka.topics.trade-events}", groupId = "trading-group")
  public void consumeTradeEvent(Trade trade, Acknowledgment acknowledgment) {
    try {
      if (trade != null) {
        logger.info("Consumed Trade Event: {}", trade);
        processTradeEvent(trade);
      } else {
        logger.warn("Received null trade event message");
      }
      acknowledgment.acknowledge();
    } catch (Exception e) {
      logger.error("Error processing trade event message", e);
      // Still acknowledge to prevent reprocessing of malformed messages
      acknowledgment.acknowledge();
    }
  }

  @KafkaListener(topics = "${kafka.topics.trade-validation}", groupId = "trading-group")
  public void consumeTradeValidation(Trade trade, Acknowledgment acknowledgment) {
    try {
      if (trade != null) {
        logger.info("Consumed Trade for Validation: {}", trade);
        validateTrade(trade);
      } else {
        logger.warn("Received null trade validation message");
      }
      acknowledgment.acknowledge();
    } catch (Exception e) {
      logger.error("Error processing trade validation message", e);
      acknowledgment.acknowledge();
    }
  }

  @KafkaListener(topics = "${kafka.topics.trade-execution}", groupId = "trading-group")
  public void consumeTradeExecution(Trade trade, Acknowledgment acknowledgment) {
    try {
      if (trade != null) {
        logger.info("Consumed Trade Execution: {}", trade);
        executeTrade(trade);
        // Evict related caches
        evictRelatedCaches(trade);
      } else {
        logger.warn("Received null trade execution message");
      }
      acknowledgment.acknowledge();
    } catch (Exception e) {
      logger.error("Error processing trade execution message", e);
      acknowledgment.acknowledge();
    }
  }

  @KafkaListener(topics = "${kafka.topics.trade-errors}", groupId = "trading-group")
  public void consumeTradeError(Trade trade, Acknowledgment acknowledgment) {
    try {
      if (trade != null) {
        logger.error("Consumed Trade Error: {}", trade);
        handleTradeError(trade);
      } else {
        logger.warn("Received null trade error message");
      }
      acknowledgment.acknowledge();
    } catch (Exception e) {
      logger.error("Error processing trade error message", e);
      acknowledgment.acknowledge();
    }
  }

  // Additional consumers for order events
  @KafkaListener(topics = "orders", groupId = "trading-group")
  public void consumeOrderEvent(Order order, Acknowledgment acknowledgment) {
    try {
      if (order != null) {
        logger.info("Consumed Order Event: {}", order);
        processOrderEvent(order);
        // Evict order-related caches
        evictOrderRelatedCaches(order);
      } else {
        logger.warn("Received null order message");
      }
      acknowledgment.acknowledge();
    } catch (Exception e) {
      logger.error("Error processing order message", e);
      acknowledgment.acknowledge();
    }
  }

  @KafkaListener(topics = "order-cancellations", groupId = "trading-group")
  public void consumeOrderCancellation(Order order, Acknowledgment acknowledgment) {
    try {
      if (order != null) {
        logger.info("Consumed Order Cancellation: {}", order);
        processOrderCancellation(order);
        evictOrderRelatedCaches(order);
      } else {
        logger.warn("Received null order cancellation message");
      }
      acknowledgment.acknowledge();
    } catch (Exception e) {
      logger.error("Error processing order cancellation message", e);
      acknowledgment.acknowledge();
    }
  }

  // Market data consumer
  @KafkaListener(topics = "market-data", groupId = "trading-group")
  public void consumeMarketData(Object marketData, Acknowledgment acknowledgment) {
    try {
      if (marketData != null) {
        logger.info("Consumed Market Data: {}", marketData);
        processMarketData(marketData);
      } else {
        logger.warn("Received null market data message");
      }
      acknowledgment.acknowledge();
    } catch (Exception e) {
      logger.error("Error processing market data message", e);
      acknowledgment.acknowledge();
    }
  }

  // Portfolio updates consumer
  @KafkaListener(topics = "portfolio-updates", groupId = "trading-group")
  public void consumePortfolioUpdate(Portfolio portfolio, Acknowledgment acknowledgment) {
    try {
      if (portfolio != null) {
        logger.info("Consumed Portfolio Update: {}", portfolio);
        processPortfolioUpdate(portfolio);
        evictPortfolioRelatedCaches(portfolio);
      } else {
        logger.warn("Received null portfolio update message");
      }
      acknowledgment.acknowledge();
    } catch (Exception e) {
      logger.error("Error processing portfolio update message", e);
      acknowledgment.acknowledge();
    }
  }

  // Watchlist events consumer
  @KafkaListener(topics = "watchlist-events", groupId = "trading-group")
  public void consumeWatchlistEvent(Watchlist watchlist, Acknowledgment acknowledgment) {
    try {
      if (watchlist != null) {
        logger.info("Consumed Watchlist Event: {}", watchlist);
        processWatchlistEvent(watchlist);
        evictWatchlistRelatedCaches(watchlist);
      } else {
        logger.warn("Received null watchlist message");
      }
      acknowledgment.acknowledge();
    } catch (Exception e) {
      logger.error("Error processing watchlist message", e);
      acknowledgment.acknowledge();
    }
  }

  // Processing methods
  private void processTradeEvent(Trade trade) {
    logger.info("Processing trade event: {}", trade.getId());
    // Add trade-specific processing logic here
    CompletableFuture.runAsync(() -> {
      try {
        // Async processing logic
        logger.debug("Async processing trade event: {}", trade.getId());
      } catch (Exception e) {
        logger.error("Error in async trade processing: {}", e.getMessage());
      }
    });
  }

  private void validateTrade(Trade trade) {
    logger.info("Validating trade: {}", trade.getId());
    // Add trade validation logic here
  }

  private void executeTrade(Trade trade) {
    logger.info("Executing trade: {}", trade.getId());
    // Add trade execution logic here
    CompletableFuture.runAsync(() -> {
      try {
        // Async trade execution logic
        logger.debug("Async executing trade: {}", trade.getId());
      } catch (Exception e) {
        logger.error("Error in async trade execution: {}", e.getMessage());
      }
    });
  }

  private void handleTradeError(Trade trade) {
    logger.error("Handling trade error: {}", trade);
    // Add trade error handling logic here
  }

  private void processOrderEvent(Order order) {
    logger.info("Processing order event: {}", order.getId());
    // Add order processing logic here
  }

  private void processOrderCancellation(Order order) {
    logger.info("Processing order cancellation: {}", order.getId());
    // Add order cancellation logic here
  }

  private void processMarketData(Object marketData) {
    logger.info("Processing market data: {}", marketData);
    // Add market data processing logic here
  }

  private void processPortfolioUpdate(Portfolio portfolio) {
    logger.info("Processing portfolio update: {}", portfolio.getUserId());
    // Add portfolio update processing logic here
  }

  private void processWatchlistEvent(Watchlist watchlist) {
    logger.info("Processing watchlist event: {}", watchlist.getId());
    // Add watchlist event processing logic here
  }

  // Cache eviction methods
  private void evictRelatedCaches(Trade trade) {
    try {
      if (redisTemplate != null) {
        // Evict trade-specific caches
        redisTemplate.delete("trade:" + trade.getId());
        redisTemplate.delete("user:trades:" + trade.getUserId());
        
        // Evict portfolio cache as trade affects portfolio
        redisTemplate.delete("portfolio:" + trade.getUserId());
        redisTemplate.delete(redisTemplate.keys("portfolio:performance:" + trade.getUserId() + ":*"));
        redisTemplate.delete("portfolio:holdings:" + trade.getUserId());
        
        logger.info("Evicted related caches for trade: {}", trade.getId());
      }
    } catch (Exception e) {
      logger.warn("Failed to evict caches for trade {}: {}", trade.getId(), e.getMessage());
    }
  }

  private void evictOrderRelatedCaches(Order order) {
    try {
      if (redisTemplate != null) {
        // Evict order-specific caches
        redisTemplate.delete("order:" + order.getId());
        redisTemplate.delete(redisTemplate.keys("user:orders:" + order.getUserId() + ":*"));
        
        // Evict portfolio cache as order affects portfolio
        redisTemplate.delete("portfolio:" + order.getUserId());
        redisTemplate.delete(redisTemplate.keys("portfolio:performance:" + order.getUserId() + ":*"));
        redisTemplate.delete("portfolio:holdings:" + order.getUserId());
        
        logger.info("Evicted order-related caches for order: {}", order.getId());
      }
    } catch (Exception e) {
      logger.warn("Failed to evict order caches for order {}: {}", order.getId(), e.getMessage());
    }
  }

  private void evictPortfolioRelatedCaches(Portfolio portfolio) {
    try {
      if (redisTemplate != null) {
        redisTemplate.delete("portfolio:" + portfolio.getUserId());
        redisTemplate.delete(redisTemplate.keys("portfolio:performance:" + portfolio.getUserId() + ":*"));
        redisTemplate.delete("portfolio:holdings:" + portfolio.getUserId());
        
        logger.info("Evicted portfolio caches for user: {}", portfolio.getUserId());
      }
    } catch (Exception e) {
      logger.warn("Failed to evict portfolio caches for user {}: {}", portfolio.getUserId(), e.getMessage());
    }
  }

  private void evictWatchlistRelatedCaches(Watchlist watchlist) {
    try {
      if (redisTemplate != null) {
        redisTemplate.delete("watchlist:id:" + watchlist.getId());
        redisTemplate.delete("watchlist:user:" + watchlist.getUserId());
        
        logger.info("Evicted watchlist caches for user: {}", watchlist.getUserId());
      }
    } catch (Exception e) {
      logger.warn("Failed to evict watchlist caches for user {}: {}", watchlist.getUserId(), e.getMessage());
    }
  }
}
