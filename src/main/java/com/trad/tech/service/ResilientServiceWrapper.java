package com.trad.tech.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@Slf4j
public class ResilientServiceWrapper {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Execute Redis operation with fallback handling
     */
    public <T> T executeWithRedisFallback(Supplier<T> redisOperation, Supplier<T> fallbackOperation, String operationName) {
        try {
            if (redisTemplate != null) {
                T result = redisOperation.get();
                log.debug("Redis operation '{}' completed successfully", operationName);
                return result;
            } else {
                log.warn("Redis not available, using fallback for operation: {}", operationName);
                return fallbackOperation.get();
            }
        } catch (Exception e) {
            log.warn("Redis operation '{}' failed, using fallback: {}", operationName, e.getMessage());
            return fallbackOperation.get();
        }
    }

    /**
     * Execute Redis operation without return value with fallback handling
     */
    public void executeRedisWithFallback(Runnable redisOperation, String operationName) {
        try {
            if (redisTemplate != null) {
                redisOperation.run();
                log.debug("Redis operation '{}' completed successfully", operationName);
            } else {
                log.warn("Redis not available for operation: {}", operationName);
            }
        } catch (Exception e) {
            log.warn("Redis operation '{}' failed: {}", operationName, e.getMessage());
        }
    }

    /**
     * Execute Kafka operation with fallback handling
     */
    public void executeKafkaWithFallback(Runnable kafkaOperation, String operationName) {
        try {
            if (kafkaTemplate != null) {
                kafkaOperation.run();
                log.debug("Kafka operation '{}' completed successfully", operationName);
            } else {
                log.warn("Kafka not available for operation: {}", operationName);
            }
        } catch (Exception e) {
            log.warn("Kafka operation '{}' failed: {}", operationName, e.getMessage());
        }
    }

    /**
     * Check if Redis is available
     */
    public boolean isRedisAvailable() {
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set("health:check", "ok", java.time.Duration.ofSeconds(10));
                String result = (String) redisTemplate.opsForValue().get("health:check");
                redisTemplate.delete("health:check");
                return "ok".equals(result);
            }
        } catch (Exception e) {
            log.warn("Redis health check failed: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Check if Kafka is available
     */
    public boolean isKafkaAvailable() {
        try {
            if (kafkaTemplate != null) {
                // Try to send a health check message
                kafkaTemplate.send("health-check", "ping");
                log.debug("Kafka health check completed successfully");
                return true;
            }
        } catch (Exception e) {
            log.warn("Kafka health check failed: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Get cache value with fallback
     */
    @SuppressWarnings("unchecked")
    public <T> T getFromCache(String key, Class<T> type) {
        try {
            if (redisTemplate != null) {
                Object value = redisTemplate.opsForValue().get(key);
                if (value != null && type.isInstance(value)) {
                    log.debug("Cache hit for key: {}", key);
                    return (T) value;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get from cache for key '{}': {}", key, e.getMessage());
        }
        return null;
    }

    /**
     * Set cache value with error handling
     */
    public <T> void setCache(String key, T value, java.time.Duration ttl) {
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(key, value, ttl);
                log.debug("Cached value for key: {}", key);
            }
        } catch (Exception e) {
            log.warn("Failed to cache value for key '{}': {}", key, e.getMessage());
        }
    }

    /**
     * Evict cache with error handling
     */
    public void evictCache(String key) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(key);
                log.debug("Evicted cache for key: {}", key);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache for key '{}': {}", key, e.getMessage());
        }
    }

    /**
     * Evict cache pattern with error handling
     */
    public void evictCachePattern(String pattern) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys(pattern));
                log.debug("Evicted cache pattern: {}", pattern);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache pattern '{}': {}", pattern, e.getMessage());
        }
    }

    /**
     * Publish to Kafka with error handling
     */
    public void publishToKafka(String topic, Object message) {
        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send(topic, message);
                log.debug("Published message to topic: {}", topic);
            }
        } catch (Exception e) {
            log.warn("Failed to publish message to topic '{}': {}", topic, e.getMessage());
        }
    }

    /**
     * Execute operation with circuit breaker pattern
     */
    public <T> T executeWithCircuitBreaker(Supplier<T> operation, Supplier<T> fallback, String operationName, int maxFailures) {
        String circuitKey = "circuit:" + operationName;
        
        try {
            // Check circuit breaker state
            Integer failureCount = (Integer) getFromCache(circuitKey, Integer.class);
            if (failureCount != null && failureCount >= maxFailures) {
                log.warn("Circuit breaker open for operation: {}, using fallback", operationName);
                return fallback.get();
            }

            // Execute operation
            T result = operation.get();
            
            // Reset failure count on success
            if (failureCount != null) {
                evictCache(circuitKey);
            }
            
            log.debug("Operation '{}' completed successfully", operationName);
            return result;
            
        } catch (Exception e) {
            // Increment failure count
            Integer currentFailures = (Integer) getFromCache(circuitKey, Integer.class);
            int newFailures = (currentFailures != null ? currentFailures : 0) + 1;
            setCache(circuitKey, newFailures, java.time.Duration.ofMinutes(5));
            
            log.warn("Operation '{}' failed ({} failures), using fallback: {}", operationName, newFailures, e.getMessage());
            return fallback.get();
        }
    }

    /**
     * Execute operation with retry logic
     */
    public <T> T executeWithRetry(Supplier<T> operation, Supplier<T> fallback, String operationName, int maxRetries) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                T result = operation.get();
                if (attempt > 1) {
                    log.info("Operation '{}' succeeded on attempt {}", operationName, attempt);
                }
                return result;
            } catch (Exception e) {
                lastException = e;
                log.warn("Operation '{}' failed on attempt {}: {}", operationName, attempt, e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(100 * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        log.warn("Operation '{}' failed after {} attempts, using fallback", operationName, maxRetries);
        return fallback.get();
    }
}
