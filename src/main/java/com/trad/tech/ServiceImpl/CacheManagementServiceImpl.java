package com.trad.tech.ServiceImpl;

import com.trad.tech.service.CacheManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class CacheManagementServiceImpl implements CacheManagementService{

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    // Market data cache management
    public void evictMarketDataCache(String symbol) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete("price:" + symbol);
                redisTemplate.delete(redisTemplate.keys("batch:*" + symbol + "*"));
                redisTemplate.delete("market:indices");
                redisTemplate.delete(redisTemplate.keys("market:top_*"));
                redisTemplate.delete(redisTemplate.keys("search:*" + symbol + "*"));
                log.info("Evicted market data cache for symbol: {}", symbol);
            }
        } catch (Exception e) {
            log.warn("Failed to evict market data cache for symbol {}: {}", symbol, e.getMessage());
        }
    }

    public void evictAllMarketDataCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys("price:*"));
                redisTemplate.delete(redisTemplate.keys("batch:*"));
                redisTemplate.delete("market:indices");
                redisTemplate.delete(redisTemplate.keys("market:top_*"));
                redisTemplate.delete(redisTemplate.keys("search:*"));
                log.info("Evicted all market data cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all market data cache: {}", e.getMessage());
        }
    }

    // Portfolio cache management
    public void evictPortfolioCache(String userId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete("portfolio:" + userId);
                redisTemplate.delete(redisTemplate.keys("portfolio:performance:" + userId + ":*"));
                redisTemplate.delete("portfolio:holdings:" + userId);
                log.info("Evicted portfolio cache for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict portfolio cache for user {}: {}", userId, e.getMessage());
        }
    }

    public void evictAllPortfolioCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys("portfolio:*"));
                log.info("Evicted all portfolio cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all portfolio cache: {}", e.getMessage());
        }
    }

    // Watchlist cache management
    public void evictWatchlistCache(String userId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys("watchlist:user:" + userId));
                log.info("Evicted watchlist cache for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict watchlist cache for user {}: {}", userId, e.getMessage());
        }
    }

    public void evictWatchlistById(String watchlistId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete("watchlist:id:" + watchlistId);
                log.info("Evicted watchlist cache for ID: {}", watchlistId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict watchlist cache for ID {}: {}", watchlistId, e.getMessage());
        }
    }

    public void evictAllWatchlistCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys("watchlist:*"));
                log.info("Evicted all watchlist cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all watchlist cache: {}", e.getMessage());
        }
    }

    // Order cache management
    public void evictOrderCache(String orderId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete("order:" + orderId);
                log.info("Evicted order cache for ID: {}", orderId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict order cache for ID {}: {}", orderId, e.getMessage());
        }
    }

    public void evictUserOrdersCache(String userId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys("user:orders:" + userId + ":*"));
                log.info("Evicted user orders cache for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict user orders cache for user {}: {}", userId, e.getMessage());
        }
    }

    public void evictAllOrderCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys("order:*"));
                redisTemplate.delete(redisTemplate.keys("user:orders:*"));
                log.info("Evicted all order cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all order cache: {}", e.getMessage());
        }
    }

    // Trade cache management
    public void evictTradeCache(String tradeId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete("trade:" + tradeId);
                log.info("Evicted trade cache for ID: {}", tradeId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict trade cache for ID {}: {}", tradeId, e.getMessage());
        }
    }

    public void evictUserTradesCache(String userId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete("user:trades:" + userId);
                log.info("Evicted user trades cache for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict user trades cache for user {}: {}", userId, e.getMessage());
        }
    }

    public void evictAllTradeCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys("trade:*"));
                redisTemplate.delete(redisTemplate.keys("user:trades:*"));
                redisTemplate.delete("trades:all");
                log.info("Evicted all trade cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all trade cache: {}", e.getMessage());
        }
    }

    // Comprehensive cache eviction for business events
    public void evictCacheOnOrderEvent(String userId, String symbol) {
        log.info("Evicting caches on order event for user: {}, symbol: {}", userId, symbol);
        evictUserOrdersCache(userId);
        evictPortfolioCache(userId);
        if (symbol != null) {
            evictMarketDataCache(symbol);
        }
    }

    public void evictCacheOnTradeEvent(String userId, String symbol) {
        log.info("Evicting caches on trade event for user: {}, symbol: {}", userId, symbol);
        evictUserTradesCache(userId);
        evictPortfolioCache(userId);
        if (symbol != null) {
            evictMarketDataCache(symbol);
        }
    }

    public void evictCacheOnPortfolioUpdate(String userId) {
        log.info("Evicting caches on portfolio update for user: {}", userId);
        evictPortfolioCache(userId);
    }

    public void evictCacheOnWatchlistEvent(String userId) {
        log.info("Evicting caches on watchlist event for user: {}", userId);
        evictWatchlistCache(userId);
    }

    // Cache statistics and health check
    public long getCacheKeyCount() {
        try {
            if (redisTemplate != null) {
                Set<String> keys = redisTemplate.keys("*");
                return keys != null ? keys.size() : 0;
            }
        } catch (Exception e) {
            log.warn("Failed to get cache key count: {}", e.getMessage());
        }
        return 0;
    }

    public void clearAllCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.getConnectionFactory().getConnection().flushAll();
                log.info("Cleared all cache");
            }
        } catch (Exception e) {
            log.warn("Failed to clear all cache: {}", e.getMessage());
        }
    }

    // Cache warming methods
    public void warmupMarketDataCache(String symbol) {
        log.info("Warming up market data cache for symbol: {}", symbol);
        // This would typically call the market data service to pre-populate cache
        // Implementation depends on specific requirements
    }

    public void warmupPortfolioCache(String userId) {
        log.info("Warming up portfolio cache for user: {}", userId);
        // This would typically call the portfolio service to pre-populate cache
        // Implementation depends on specific requirements
    }
}
