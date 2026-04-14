package com.trad.tech.service.impl;

import com.trad.tech.model.Portfolio;
import com.trad.tech.repository.PortfolioRepository;
import com.trad.tech.service.PortfolioService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

  private final PortfolioRepository portfolioRepository;

  @Override
  public Portfolio getUserPortfolio(String userId) {
    log.info("Getting portfolio for user: {}", userId);

    return portfolioRepository
        .findByUserId(userId)
        .orElseGet(
            () -> {
              // Create default portfolio if not exists
              Portfolio portfolio = new Portfolio();
              portfolio.setUserId(userId);
              portfolio.setName("Main Portfolio");
              portfolio.setTotalValue(0.0);
              portfolio.setAvailableBalance(10000.0);
              portfolio.setTotalInvested(0.0);
              portfolio.setTotalPnL(0.0);
              portfolio.setDailyPnL(0.0);
              portfolio.setHoldings(new ArrayList<>());
              portfolio.setLastUpdated(System.currentTimeMillis());

              return portfolioRepository.save(portfolio);
            });
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
    existingPortfolio.setLastUpdated(System.currentTimeMillis());

    return portfolioRepository.save(existingPortfolio);
  }

  @Override
  public Object getPortfolioPerformance(String userId, String period) {
    log.info("Getting portfolio performance for user: {}, period: {}", userId, period);

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

    return performance;
  }

  @Override
  public List<Portfolio.Holding> getPortfolioHoldings(String userId) {
    log.info("Getting portfolio holdings for user: {}", userId);

    Portfolio portfolio = getUserPortfolio(userId);
    return portfolio.getHoldings();
  }
}
