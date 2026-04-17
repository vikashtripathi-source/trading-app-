package com.trad.tech.ServiceImpl;

import com.trad.tech.model.MarketData;
import com.trad.tech.model.MarketDataEntity;
import com.trad.tech.model.MarketIndex;
import com.trad.tech.model.StockSymbol;
import com.trad.tech.repository.MarketDataRepository;
import com.trad.tech.repository.MarketIndexRepository;
import com.trad.tech.repository.StockSymbolRepository;
import com.trad.tech.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class MarketDataServiceImplEnhanced implements MarketDataService {

    private final MarketDataRepository marketDataRepository;
    private final MarketIndexRepository marketIndexRepository;
    private final StockSymbolRepository stockSymbolRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String MARKET_DATA_KEY_PREFIX = "price:";
    private static final String BATCH_MARKET_DATA_KEY_PREFIX = "batch:";
    private static final String MARKET_INDICES_KEY = "market:indices";
    private static final String TOP_GAINERS_KEY = "market:top_gainers";
    private static final String TOP_LOSERS_KEY = "market:top_losers";
    private static final String SYMBOL_SEARCH_PREFIX = "search:";

    @Override
    public MarketData getMarketData(String symbol) {
        log.info("Getting market data for symbol: {}", symbol);
        String cacheKey = MARKET_DATA_KEY_PREFIX + symbol;

        try {
            // Try Redis cache first
            if (redisTemplate != null) {
                MarketData cachedData = (MarketData) redisTemplate.opsForValue().get(cacheKey);
                if (cachedData != null) {
                    log.info("Cache hit for symbol: {}", symbol);
                    return cachedData;
                }
                log.info("Cache miss for symbol: {}", symbol);
            }
        } catch (Exception e) {
            log.warn("Redis cache error for symbol {}: {}", symbol, e.getMessage());
        }

        // Fallback to MongoDB
        MarketDataEntity entity = marketDataRepository
                .findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Market data not found for symbol: " + symbol));

        MarketData marketData = convertToMarketData(entity);

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, marketData, Duration.ofSeconds(15));
                log.info("Cached market data for symbol: {}", symbol);
            }
        } catch (Exception e) {
            log.warn("Failed to cache market data for symbol {}: {}", symbol, e.getMessage());
        }

        // Publish to Kafka for async processing
        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send("market-data", marketData);
                log.debug("Published market data to Kafka for symbol: {}", symbol);
            }
        } catch (Exception e) {
            log.warn("Failed to publish market data to Kafka for symbol {}: {}", symbol, e.getMessage());
        }

        return marketData;
    }

    @Override
    public List<MarketData> getBatchMarketData(List<String> symbols) {
        log.info("Getting batch market data for symbols: {}", symbols);
        String cacheKey = BATCH_MARKET_DATA_KEY_PREFIX + String.join(",", symbols);

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<MarketData> cachedData = (List<MarketData>) redisTemplate.opsForValue().get(cacheKey);
                if (cachedData != null) {
                    log.info("Cache hit for batch symbols: {}", symbols);
                    return cachedData;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for batch symbols {}: {}", symbols, e.getMessage());
        }

        // Fallback to MongoDB
        List<MarketDataEntity> entities = marketDataRepository.findBySymbolIn(symbols);
        List<MarketData> marketDataList = entities.stream()
                .map(this::convertToMarketData)
                .collect(Collectors.toList());

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, marketDataList, Duration.ofSeconds(10));
                log.info("Cached batch market data for symbols: {}", symbols);
            }
        } catch (Exception e) {
            log.warn("Failed to cache batch market data for symbols {}: {}", symbols, e.getMessage());
        }

        return marketDataList;
    }

    @Override
    public Object getMarketIndices() {
        log.info("Getting market indices");

        try {
            if (redisTemplate != null) {
                Object cachedIndices = redisTemplate.opsForValue().get(MARKET_INDICES_KEY);
                if (cachedIndices != null) {
                    log.info("Cache hit for market indices");
                    return cachedIndices;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for market indices: {}", e.getMessage());
        }

        // Fallback to MongoDB
        List<MarketIndex> indices = marketIndexRepository.findAll();
        Map<String, Object> result = new HashMap<>();

        for (MarketIndex index : indices) {
            Map<String, Object> indexData = new HashMap<>();
            indexData.put("value", index.getValue());
            indexData.put("change", index.getChange());
            indexData.put("changePercent", index.getChangePercent());
            result.put(index.getName(), indexData);
        }

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(MARKET_INDICES_KEY, result, Duration.ofSeconds(30));
                log.info("Cached market indices");
            }
        } catch (Exception e) {
            log.warn("Failed to cache market indices: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public List<MarketData> getTopGainers(int limit) {
        log.info("Getting top {} gainers", limit);
        String cacheKey = TOP_GAINERS_KEY + ":" + limit;

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<MarketData> cachedGainers = (List<MarketData>) redisTemplate.opsForValue().get(cacheKey);
                if (cachedGainers != null) {
                    log.info("Cache hit for top {} gainers", limit);
                    return cachedGainers;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for top gainers: {}", e.getMessage());
        }

        // Fallback to MongoDB
        List<MarketDataEntity> topGainers = marketDataRepository.findTop10ByOrderByChangeDesc();
        List<MarketData> result = topGainers.stream()
                .limit(limit)
                .map(this::convertToMarketData)
                .collect(Collectors.toList());

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, result, Duration.ofSeconds(60));
                log.info("Cached top {} gainers", limit);
            }
        } catch (Exception e) {
            log.warn("Failed to cache top gainers: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public List<MarketData> getTopLosers(int limit) {
        log.info("Getting top {} losers", limit);
        String cacheKey = TOP_LOSERS_KEY + ":" + limit;

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<MarketData> cachedLosers = (List<MarketData>) redisTemplate.opsForValue().get(cacheKey);
                if (cachedLosers != null) {
                    log.info("Cache hit for top {} losers", limit);
                    return cachedLosers;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for top losers: {}", e.getMessage());
        }

        // Fallback to MongoDB
        List<MarketDataEntity> topLosers = marketDataRepository.findTop10ByOrderByChangeAsc();
        List<MarketData> result = topLosers.stream()
                .limit(limit)
                .map(this::convertToMarketData)
                .collect(Collectors.toList());

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, result, Duration.ofSeconds(60));
                log.info("Cached top {} losers", limit);
            }
        } catch (Exception e) {
            log.warn("Failed to cache top losers: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public List<Object> searchSymbols(String query, int limit) {
        log.info("Searching symbols with query: {}, limit: {}", query, limit);
        String cacheKey = SYMBOL_SEARCH_PREFIX + query + ":" + limit;

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<Object> cachedResults = (List<Object>) redisTemplate.opsForValue().get(cacheKey);
                if (cachedResults != null) {
                    log.info("Cache hit for symbol search: {}", query);
                    return cachedResults;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for symbol search {}: {}", query, e.getMessage());
        }

        // Fallback to MongoDB
        List<StockSymbol> symbols = stockSymbolRepository
                .findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
        List<Object> result = symbols.stream()
                .limit(limit)
                .map(this::convertToSearchResult)
                .collect(Collectors.toList());

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(5));
                log.info("Cached symbol search results for query: {}", query);
            }
        } catch (Exception e) {
            log.warn("Failed to cache symbol search results for query {}: {}", query, e.getMessage());
        }

        return result;
    }

    // Cache eviction methods
    public void evictMarketDataCache(String symbol) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(MARKET_DATA_KEY_PREFIX + symbol);
                log.info("Evicted cache for symbol: {}", symbol);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache for symbol {}: {}", symbol, e.getMessage());
        }
    }

    public void evictAllMarketDataCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys(MARKET_DATA_KEY_PREFIX + "*"));
                redisTemplate.delete(MARKET_INDICES_KEY);
                redisTemplate.delete(redisTemplate.keys(TOP_GAINERS_KEY + "*"));
                redisTemplate.delete(redisTemplate.keys(TOP_LOSERS_KEY + "*"));
                log.info("Evicted all market data cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all market data cache: {}", e.getMessage());
        }
    }

    private MarketData convertToMarketData(MarketDataEntity entity) {
        MarketData marketData = new MarketData();
        marketData.setSymbol(entity.getSymbol());
        marketData.setCurrentPrice(entity.getCurrentPrice());
        marketData.setOpenPrice(entity.getOpenPrice());
        marketData.setHighPrice(entity.getHighPrice());
        marketData.setLowPrice(entity.getLowPrice());
        marketData.setPreviousClose(entity.getPreviousClose());
        marketData.setChange(entity.getChange());
        marketData.setChangePercentage(entity.getChangePercentage());
        marketData.setVolume(entity.getVolume());
        marketData.setAverageVolume(entity.getAverageVolume());
        marketData.setMarketCap(entity.getMarketCap());
        marketData.setPeRatio(entity.getPeRatio());
        marketData.setTimestamp(entity.getTimestamp());
        return marketData;
    }

    private Map<String, Object> convertToSearchResult(StockSymbol symbol) {
        Map<String, Object> result = new HashMap<>();
        result.put("symbol", symbol.getSymbol());
        result.put("name", symbol.getName());
        result.put("type", symbol.getType());
        result.put("exchange", symbol.getExchange());
        result.put("sector", symbol.getSector());
        return result;
    }
}
