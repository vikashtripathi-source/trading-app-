package com.trad.tech.service;

import org.springframework.stereotype.Service;

import java.util.Map;


public interface AnalyticsService {
    
    Map<String, Object> getTradingStatistics(String userId, String period);
    
    Map<String, Object> getPerformanceMetrics(String userId, String period);
    
    Map<String, Object> getTradeAnalysis(String userId, String period);
    
    Map<String, Object> getRiskMetrics(String userId);
    
    Map<String, Object> getPortfolioAllocation(String userId);
}
