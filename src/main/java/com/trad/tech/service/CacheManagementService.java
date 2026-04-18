package com.trad.tech.service;


public interface CacheManagementService {


    void evictMarketDataCache(String symbol);

    void evictAllMarketDataCache();

    void evictPortfolioCache(String userId);

    void evictAllPortfolioCache();

    void evictWatchlistCache(String userId);

    void evictWatchlistById(String watchlistId);

    void evictAllWatchlistCache();

    void evictOrderCache(String orderId);

    void evictUserOrdersCache(String userId);

    void evictAllOrderCache();

    void evictTradeCache(String tradeId);

    void evictUserTradesCache(String userId);

    void evictAllTradeCache();

    void evictCacheOnOrderEvent(String userId, String symbol);

    void evictCacheOnTradeEvent(String userId, String symbol);

    void evictCacheOnPortfolioUpdate(String userId);

    void evictCacheOnWatchlistEvent(String userId);

    long getCacheKeyCount();

    void clearAllCache();

    void warmupMarketDataCache(String symbol);

    void warmupPortfolioCache(String userId);
}
