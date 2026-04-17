package com.trad.tech.ServiceImpl;

import com.trad.tech.model.Portfolio;
import com.trad.tech.repository.PortfolioRepository;
import com.trad.tech.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImplEnhanced implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PORTFOLIO_KEY_PREFIX = "portfolio:";
    private static final String PORTFOLIO_PERFORMANCE_PREFIX = "portfolio:performance:";
    private static final String PORTFOLIO_HOLDINGS_PREFIX = "portfolio:holdings:";

    @Override
    public Portfolio getUserPortfolio(String userId) {
        log.info("Getting portfolio for user: {}", userId);
        String cacheKey = PORTFOLIO_KEY_PREFIX + userId;

        try {
            // Try Redis cache first
            if (redisTemplate != null) {
                Portfolio cachedPortfolio = (Portfolio) redisTemplate.opsForValue().get(cacheKey);
                if (cachedPortfolio != null) {
                    log.info("Cache hit for portfolio of user: {}", userId);
                    return cachedPortfolio;
                }
                log.info("Cache miss for portfolio of user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Redis cache error for user {} portfolio: {}", userId, e.getMessage());
        }

        // Fallback to MongoDB
        Portfolio portfolio = portfolioRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    // Create default portfolio if not exists
                    Portfolio newPortfolio = new Portfolio();
                    newPortfolio.setUserId(userId);
                    newPortfolio.setName("Main Portfolio");
                    newPortfolio.setTotalValue(0.0);
                    newPortfolio.setAvailableBalance(10000.0);
                    newPortfolio.setTotalInvested(0.0);
                    newPortfolio.setTotalPnL(0.0);
                    newPortfolio.setDailyPnL(0.0);
                    newPortfolio.setHoldings(new ArrayList<>());
                    newPortfolio.setLastUpdated(
                            java.time.LocalDateTime.ofEpochSecond(
                                    System.currentTimeMillis() / 1000, 0, java.time.ZoneOffset.UTC));

                    return portfolioRepository.save(newPortfolio);
                });

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, portfolio, Duration.ofMinutes(5));
                log.info("Cached portfolio for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to cache portfolio for user {}: {}", userId, e.getMessage());
        }

        return portfolio;
    }

    @Override
    public Portfolio updatePortfolio(String userId, Portfolio portfolio) {
        log.info("Updating portfolio for user: {}", userId);

        Portfolio existingPortfolio = getUserPortfolio(userId);

        // Update fields
        existingPortfolio.setName(portfolio.getName());
        existingPortfolio.setTotalValue(portfolio.getTotalValue());
        existingPortfolio.setAvailableBalance(portfolio.getAvailableBalance());
        existingPortfolio.setTotalInvested(portfolio.getTotalInvested());
        existingPortfolio.setTotalPnL(portfolio.getTotalPnL());
        existingPortfolio.setDailyPnL(portfolio.getDailyPnL());
        existingPortfolio.setHoldings(portfolio.getHoldings());
        existingPortfolio.setLastUpdated(
                java.time.LocalDateTime.ofEpochSecond(
                        System.currentTimeMillis() / 1000, 0, java.time.ZoneOffset.UTC));

        Portfolio updatedPortfolio = portfolioRepository.save(existingPortfolio);

        // Update cache
        try {
            if (redisTemplate != null) {
                String cacheKey = PORTFOLIO_KEY_PREFIX + userId;
                redisTemplate.opsForValue().set(cacheKey, updatedPortfolio, Duration.ofMinutes(5));
                log.info("Updated cache for portfolio of user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to update cache for user {} portfolio: {}", userId, e.getMessage());
        }

        // Publish to Kafka
        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send("portfolio-updates", updatedPortfolio);
                log.debug("Published portfolio update to Kafka for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to publish portfolio update to Kafka for user {}: {}", userId, e.getMessage());
        }

        return updatedPortfolio;
    }

    @Override
    public Object getPortfolioPerformance(String userId, String period) {
        log.info("Getting portfolio performance for user: {}, period: {}", userId, period);
        String cacheKey = PORTFOLIO_PERFORMANCE_PREFIX + userId + ":" + period;

        try {
            if (redisTemplate != null) {
                Object cachedPerformance = redisTemplate.opsForValue().get(cacheKey);
                if (cachedPerformance != null) {
                    log.info("Cache hit for portfolio performance of user: {}, period: {}", userId, period);
                    return cachedPerformance;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for user {} performance: {}", userId, e.getMessage());
        }

        // Fallback to MongoDB
        Portfolio portfolio = getUserPortfolio(userId);

        // Calculate actual performance from portfolio data
        Map<String, Object> performance = new HashMap<>();
        performance.put("totalValue", portfolio.getTotalValue());
        performance.put("totalInvested", portfolio.getTotalInvested());
        performance.put("totalPnL", portfolio.getTotalPnL());
        performance.put("dailyPnL", portfolio.getDailyPnL());
        performance.put("availableBalance", portfolio.getAvailableBalance());
        performance.put(
                "totalReturn",
                portfolio.getTotalInvested() > 0
                        ? (portfolio.getTotalValue() - portfolio.getTotalInvested())
                        / portfolio.getTotalInvested()
                        * 100
                        : 0.0);
        performance.put(
                "holdingsCount", portfolio.getHoldings() != null ? portfolio.getHoldings().size() : 0);
        performance.put("period", period);
        performance.put("lastUpdated", portfolio.getLastUpdated());

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, performance, Duration.ofMinutes(3));
                log.info("Cached portfolio performance for user: {}, period: {}", userId, period);
            }
        } catch (Exception e) {
            log.warn("Failed to cache portfolio performance for user {}: {}", userId, e.getMessage());
        }

        return performance;
    }

    @Override
    public List<Portfolio.Holding> getPortfolioHoldings(String userId) {
        log.info("Getting portfolio holdings for user: {}", userId);
        String cacheKey = PORTFOLIO_HOLDINGS_PREFIX + userId;

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<Portfolio.Holding> cachedHoldings = (List<Portfolio.Holding>) redisTemplate.opsForValue().get(cacheKey);
                if (cachedHoldings != null) {
                    log.info("Cache hit for portfolio holdings of user: {}", userId);
                    return cachedHoldings;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for user {} holdings: {}", userId, e.getMessage());
        }

        // Fallback to MongoDB
        Portfolio portfolio = getUserPortfolio(userId);
        List<Portfolio.Holding> holdings = portfolio.getHoldings();

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, holdings, Duration.ofMinutes(2));
                log.info("Cached portfolio holdings for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to cache portfolio holdings for user {}: {}", userId, e.getMessage());
        }

        return holdings;
    }

    // Cache eviction methods
    public void evictPortfolioCache(String userId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(PORTFOLIO_KEY_PREFIX + userId);
                redisTemplate.delete(redisTemplate.keys(PORTFOLIO_PERFORMANCE_PREFIX + userId + ":*"));
                redisTemplate.delete(PORTFOLIO_HOLDINGS_PREFIX + userId);
                log.info("Evicted all portfolio cache for user: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict portfolio cache for user {}: {}", userId, e.getMessage());
        }
    }

    public void evictAllPortfolioCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys(PORTFOLIO_KEY_PREFIX + "*"));
                redisTemplate.delete(redisTemplate.keys(PORTFOLIO_PERFORMANCE_PREFIX + "*"));
                redisTemplate.delete(redisTemplate.keys(PORTFOLIO_HOLDINGS_PREFIX + "*"));
                log.info("Evicted all portfolio cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all portfolio cache: {}", e.getMessage());
        }
    }
}
