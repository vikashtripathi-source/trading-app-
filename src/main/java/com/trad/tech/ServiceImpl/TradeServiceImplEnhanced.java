package com.trad.tech.ServiceImpl;

import com.trad.tech.exception.InvalidTradeDataException;
import com.trad.tech.exception.TradeAlreadyDeletedException;
import com.trad.tech.exception.TradeNotFoundException;
import com.trad.tech.model.Trade;
import com.trad.tech.repository.TradeRepository;
import com.trad.tech.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class TradeServiceImplEnhanced implements TradeService {

    private final TradeRepository repository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TRADE_KEY_PREFIX = "trade:";
    private static final String USER_TRADES_PREFIX = "user:trades:";

    @Override
    public Trade createTrade(Trade trade) {
        validateTradeData(trade);
        
        // Save to MongoDB (existing synchronous behavior)
        Trade saved = repository.save(trade);

        // Publish to Kafka for async processing
        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send("trade-execution", saved);
                log.info("Published trade execution to Kafka: {}", saved.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to publish trade execution to Kafka: {}", e.getMessage());
        }

        // Evict user trades cache
        evictUserTradesCache(trade.getUserId());

        return saved;
    }

    @Override
    public List<Trade> getAllTrades() {
        log.info("Getting all trades");
        String cacheKey = "trades:all";

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<Trade> cachedTrades = (List<Trade>) redisTemplate.opsForValue().get(cacheKey);
                if (cachedTrades != null) {
                    log.info("Cache hit for all trades");
                    return cachedTrades;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for all trades: {}", e.getMessage());
        }

        // Fallback to MongoDB
        List<Trade> trades = repository.findAll();

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, trades, Duration.ofMinutes(1));
                log.info("Cached all trades");
            }
        } catch (Exception e) {
            log.warn("Failed to cache all trades: {}", e.getMessage());
        }

        return trades;
    }

    @Override
    public Trade getTrade(String id) {
        log.info("Getting trade by ID: {}", id);
        String cacheKey = TRADE_KEY_PREFIX + id;

        try {
            if (redisTemplate != null) {
                Trade cachedTrade = (Trade) redisTemplate.opsForValue().get(cacheKey);
                if (cachedTrade != null) {
                    log.info("Cache hit for trade ID: {}", id);
                    return cachedTrade;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for trade ID {}: {}", id, e.getMessage());
        }

        // Fallback to MongoDB
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTradeDataException("Trade ID cannot be null or empty");
        }
        
        Trade trade = repository
                .findById(id)
                .orElseThrow(() -> new TradeNotFoundException("Trade not found with ID: " + id));

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, trade, Duration.ofMinutes(5));
                log.info("Cached trade by ID: {}", id);
            }
        } catch (Exception e) {
            log.warn("Failed to cache trade by ID {}: {}", id, e.getMessage());
        }

        return trade;
    }

    @Override
    public Trade updateTrade(String id, Trade trade) {
        validateTradeData(trade);
        Trade existing = getTrade(id);

        existing.setSymbol(trade.getSymbol());
        existing.setType(trade.getType());
        existing.setQuantity(trade.getQuantity());
        existing.setPrice(trade.getPrice());
        existing.setStatus(trade.getStatus());

        // Save to MongoDB
        Trade updatedTrade = repository.save(existing);

        // Publish to Kafka
        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send("trade-updates", updatedTrade);
                log.info("Published trade update to Kafka: {}", updatedTrade.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to publish trade update to Kafka: {}", e.getMessage());
        }

        // Update cache
        updateTradeCache(updatedTrade);
        evictUserTradesCache(updatedTrade.getUserId());

        return updatedTrade;
    }

    @Override
    public void deleteTrade(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTradeDataException("Trade ID cannot be null or empty");
        }

        Optional<Trade> tradeOptional = repository.findById(id);
        if (tradeOptional.isEmpty()) {
            throw new TradeAlreadyDeletedException(id, true);
        }

        Trade trade = tradeOptional.get();
        String userId = trade.getUserId();

        repository.deleteById(id);

        // Evict cache
        evictTradeCache(id);
        evictUserTradesCache(userId);

        // Publish to Kafka
        try {
            if (kafkaTemplate != null) {
                trade.setDeleted(true);
                kafkaTemplate.send("trade-deletions", trade);
                log.info("Published trade deletion to Kafka: {}", id);
            }
        } catch (Exception e) {
            log.warn("Failed to publish trade deletion to Kafka: {}", e.getMessage());
        }
    }

    @Override
    public List<Trade> getUserTrades(String userId) {
        log.info("Getting trades for user: {}", userId);
        String cacheKey = USER_TRADES_PREFIX + userId;

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<Trade> cachedTrades = (List<Trade>) redisTemplate.opsForValue().get(cacheKey);
                if (cachedTrades != null) {
                    log.info("Cache hit for user trades: {}", userId);
                    return cachedTrades;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for user {} trades: {}", userId, e.getMessage());
        }

        // Fallback to MongoDB
        List<Trade> trades = repository.findByUserId(userId);

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, trades, Duration.ofMinutes(3));
                log.info("Cached user trades for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to cache user trades for user {}: {}", userId, e.getMessage());
        }

        return trades;
    }

    @Override
    public Trade createTradeForUser(String userId, Trade trade) {
        trade.setUserId(userId);
        return createTrade(trade);
    }

    // Cache management methods
    private void updateTradeCache(Trade trade) {
        try {
            if (redisTemplate != null) {
                String cacheKey = TRADE_KEY_PREFIX + trade.getId();
                redisTemplate.opsForValue().set(cacheKey, trade, Duration.ofMinutes(5));
                log.info("Updated cache for trade ID: {}", trade.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to update cache for trade ID {}: {}", trade.getId(), e.getMessage());
        }
    }

    private void evictTradeCache(String id) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(TRADE_KEY_PREFIX + id);
                log.info("Evicted cache for trade ID: {}", id);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache for trade ID {}: {}", id, e.getMessage());
        }
    }

    private void evictUserTradesCache(String userId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(USER_TRADES_PREFIX + userId);
                log.info("Evicted cache for user trades: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache for user trades {}: {}", userId, e.getMessage());
        }
    }

    public void evictAllTradeCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys(TRADE_KEY_PREFIX + "*"));
                redisTemplate.delete(redisTemplate.keys(USER_TRADES_PREFIX + "*"));
                redisTemplate.delete("trades:all");
                log.info("Evicted all trade cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all trade cache: {}", e.getMessage());
        }
    }

    private void validateTradeData(Trade trade) {
        if (trade == null) {
            throw new InvalidTradeDataException("Trade data cannot be null");
        }

        if (trade.getSymbol() == null || trade.getSymbol().trim().isEmpty()) {
            throw new InvalidTradeDataException("Trade symbol is required");
        }

        if (trade.getType() == null || trade.getType().trim().isEmpty()) {
            throw new InvalidTradeDataException("Trade type is required");
        }

        if (!"BUY".equalsIgnoreCase(trade.getType()) && !"SELL".equalsIgnoreCase(trade.getType())) {
            throw new InvalidTradeDataException("Trade type must be either 'BUY' or 'SELL'");
        }

        if (trade.getQuantity() <= 0) {
            throw new InvalidTradeDataException("Trade quantity must be greater than 0");
        }

        if (trade.getPrice() <= 0) {
            throw new InvalidTradeDataException("Trade price must be greater than 0");
        }
    }
}
