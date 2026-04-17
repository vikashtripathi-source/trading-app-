package com.trad.tech.ServiceImpl;

import com.trad.tech.model.Watchlist;
import com.trad.tech.repository.WatchlistRepository;
import com.trad.tech.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class WatchlistServiceImplEnhanced implements WatchlistService {

    private final WatchlistRepository watchlistRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String WATCHLIST_USER_PREFIX = "watchlist:user:";
    private static final String WATCHLIST_BY_ID_PREFIX = "watchlist:id:";

    @Override
    public List<Watchlist> getUserWatchlists(String userId) {
        log.info("Getting watchlists for user: {}", userId);
        String cacheKey = WATCHLIST_USER_PREFIX + userId;

        try {
            // Try Redis cache first
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<Watchlist> cachedWatchlists = (List<Watchlist>) redisTemplate.opsForValue().get(cacheKey);
                if (cachedWatchlists != null) {
                    log.info("Cache hit for user watchlists: {}", userId);
                    return cachedWatchlists;
                }
                log.info("Cache miss for user watchlists: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Redis cache error for user {} watchlists: {}", userId, e.getMessage());
        }

        // Fallback to MongoDB
        List<Watchlist> watchlists = watchlistRepository.findByUserIdOrderByCreatedDateDesc(userId);

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, watchlists, Duration.ofMinutes(3));
                log.info("Cached user watchlists for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to cache user watchlists for user {}: {}", userId, e.getMessage());
        }

        return watchlists;
    }

    @Override
    public Watchlist createWatchlist(Watchlist watchlist) {
        log.info("Creating new watchlist: {}", watchlist);

        // Set creation date and ID if not set
        if (watchlist.getId() == null) {
            watchlist.setId(UUID.randomUUID().toString());
        }
        if (watchlist.getCreatedDate() == null) {
            watchlist.setCreatedDate(LocalDateTime.now());
        }
        if (watchlist.getLastUpdated() == null) {
            watchlist.setLastUpdated(LocalDateTime.now());
        }

        Watchlist savedWatchlist = watchlistRepository.save(watchlist);

        // Evict user watchlists cache
        evictUserWatchlistCache(watchlist.getUserId());

        // Publish to Kafka
        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send("watchlist-events", savedWatchlist);
                log.debug("Published watchlist creation to Kafka for user: {}", watchlist.getUserId());
            }
        } catch (Exception e) {
            log.warn("Failed to publish watchlist creation to Kafka for user {}: {}", watchlist.getUserId(), e.getMessage());
        }

        return savedWatchlist;
    }

    @Override
    public Watchlist getWatchlistById(String id) {
        log.info("Getting watchlist by ID: {}", id);
        String cacheKey = WATCHLIST_BY_ID_PREFIX + id;

        try {
            if (redisTemplate != null) {
                Watchlist cachedWatchlist = (Watchlist) redisTemplate.opsForValue().get(cacheKey);
                if (cachedWatchlist != null) {
                    log.info("Cache hit for watchlist ID: {}", id);
                    return cachedWatchlist;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for watchlist ID {}: {}", id, e.getMessage());
        }

        // Fallback to MongoDB
        Watchlist watchlist = watchlistRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Watchlist not found with ID: " + id));

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, watchlist, Duration.ofMinutes(5));
                log.info("Cached watchlist by ID: {}", id);
            }
        } catch (Exception e) {
            log.warn("Failed to cache watchlist by ID {}: {}", id, e.getMessage());
        }

        return watchlist;
    }

    @Override
    public Watchlist addSymbolToWatchlist(String id, String symbol) {
        log.info("Adding symbol {} to watchlist: {}", symbol, id);

        Watchlist watchlist = getWatchlistById(id);

        // Add symbol if not already present
        if (!watchlist.getSymbols().contains(symbol)) {
            watchlist.getSymbols().add(symbol);
            watchlist.setLastUpdated(LocalDateTime.now());

            Watchlist updatedWatchlist = watchlistRepository.save(watchlist);

            // Update cache
            updateWatchlistCache(updatedWatchlist);
            evictUserWatchlistCache(watchlist.getUserId());

            // Publish to Kafka
            try {
                if (kafkaTemplate != null) {
                    kafkaTemplate.send("watchlist-events", updatedWatchlist);
                    log.debug("Published watchlist update to Kafka for user: {}", watchlist.getUserId());
                }
            } catch (Exception e) {
                log.warn("Failed to publish watchlist update to Kafka for user {}: {}", watchlist.getUserId(), e.getMessage());
            }

            return updatedWatchlist;
        }

        return watchlist;
    }

    @Override
    public Watchlist removeSymbolFromWatchlist(String id, String symbol) {
        log.info("Removing symbol {} from watchlist: {}", symbol, id);

        Watchlist watchlist = getWatchlistById(id);

        // Remove symbol if present
        if (watchlist.getSymbols().remove(symbol)) {
            watchlist.setLastUpdated(LocalDateTime.now());

            Watchlist updatedWatchlist = watchlistRepository.save(watchlist);

            // Update cache
            updateWatchlistCache(updatedWatchlist);
            evictUserWatchlistCache(watchlist.getUserId());

            // Publish to Kafka
            try {
                if (kafkaTemplate != null) {
                    kafkaTemplate.send("watchlist-events", updatedWatchlist);
                    log.debug("Published watchlist update to Kafka for user: {}", watchlist.getUserId());
                }
            } catch (Exception e) {
                log.warn("Failed to publish watchlist update to Kafka for user {}: {}", watchlist.getUserId(), e.getMessage());
            }

            return updatedWatchlist;
        }

        return watchlist;
    }

    @Override
    public void deleteWatchlist(String id) {
        log.info("Deleting watchlist: {}", id);

        Watchlist watchlist = getWatchlistById(id);
        String userId = watchlist.getUserId();

        watchlistRepository.deleteById(id);

        // Evict cache
        evictWatchlistCache(id);
        evictUserWatchlistCache(userId);

        // Publish to Kafka
        try {
            if (kafkaTemplate != null) {
                watchlist.setDeleted(true);
                kafkaTemplate.send("watchlist-events", watchlist);
                log.debug("Published watchlist deletion to Kafka for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to publish watchlist deletion to Kafka for user {}: {}", userId, e.getMessage());
        }
    }

    // Cache management methods
    private void updateWatchlistCache(Watchlist watchlist) {
        try {
            if (redisTemplate != null) {
                String cacheKey = WATCHLIST_BY_ID_PREFIX + watchlist.getId();
                redisTemplate.opsForValue().set(cacheKey, watchlist, Duration.ofMinutes(5));
                log.info("Updated cache for watchlist ID: {}", watchlist.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to update cache for watchlist ID {}: {}", watchlist.getId(), e.getMessage());
        }
    }

    private void evictWatchlistCache(String id) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(WATCHLIST_BY_ID_PREFIX + id);
                log.info("Evicted cache for watchlist ID: {}", id);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache for watchlist ID {}: {}", id, e.getMessage());
        }
    }

    private void evictUserWatchlistCache(String userId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(WATCHLIST_USER_PREFIX + userId);
                log.info("Evicted cache for user watchlists: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache for user watchlists {}: {}", userId, e.getMessage());
        }
    }

    public void evictAllWatchlistCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys(WATCHLIST_USER_PREFIX + "*"));
                redisTemplate.delete(redisTemplate.keys(WATCHLIST_BY_ID_PREFIX + "*"));
                log.info("Evicted all watchlist cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all watchlist cache: {}", e.getMessage());
        }
    }
}
