package com.trad.tech.service;

import com.trad.tech.model.Portfolio;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PortfolioService {
    
    Portfolio getUserPortfolio(String userId);
    
    Portfolio updatePortfolio(String userId, Portfolio portfolio);
    
    Object getPortfolioPerformance(String userId, String period);
    
    List<Portfolio.Holding> getPortfolioHoldings(String userId);
}
