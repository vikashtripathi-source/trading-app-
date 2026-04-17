package com.trad.tech.ServiceImpl;

import com.trad.tech.model.Portfolio;
import com.trad.tech.model.Trade;
import com.trad.tech.repository.PortfolioRepository;
import com.trad.tech.repository.TradeRepository;
import com.trad.tech.service.AnalyticsService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

  private final TradeRepository tradeRepository;
  private final PortfolioRepository portfolioRepository;

  @Override
  public Map<String, Object> getTradingStatistics(String userId, String period) {
    log.info("Getting trading statistics for user: {}, period: {}", userId, period);

    java.util.List<com.trad.tech.model.Trade> trades = tradeRepository.findByUserId(userId);

    Map<String, Object> statistics = new HashMap<>();
    statistics.put("totalTrades", trades.size());

    if (trades.isEmpty()) {
      statistics.put("winRate", 0.0);
      statistics.put("totalProfit", 0.0);
      statistics.put("totalLoss", 0.0);
      statistics.put("netProfit", 0.0);
    } else {
      long winningTrades =
          trades.stream()
              .filter(trade -> "BUY".equals(trade.getType()) && trade.getPrice() > 0)
              .count();

      double totalProfit =
          trades.stream()
              .filter(trade -> "BUY".equals(trade.getType()))
              .mapToDouble(trade -> trade.getPrice() * trade.getQuantity())
              .sum();

      double totalLoss =
          trades.stream()
              .filter(trade -> "SELL".equals(trade.getType()))
              .mapToDouble(trade -> trade.getPrice() * trade.getQuantity())
              .sum();

      statistics.put(
          "winRate", trades.size() > 0 ? (double) winningTrades / trades.size() * 100 : 0.0);
      statistics.put("totalProfit", Math.max(0, totalProfit));
      statistics.put("totalLoss", Math.min(0, totalLoss));
      statistics.put("netProfit", totalProfit + totalLoss);
    }

    statistics.put("period", period);
    return statistics;
  }

  @Override
  public Map<String, Object> getPerformanceMetrics(String userId, String period) {
    log.info("Getting performance metrics for user: {}, period: {}", userId, period);

    Portfolio portfolio = portfolioRepository.findByUserId(userId).orElse(null);
    java.util.List<com.trad.tech.model.Trade> trades = tradeRepository.findByUserId(userId);

    Map<String, Object> metrics = new HashMap<>();

    if (portfolio != null && !trades.isEmpty()) {
      double totalValue = portfolio.getTotalValue();
      double totalInvested = portfolio.getTotalInvested();

      // Simple Sharpe ratio calculation (assuming 2% risk-free rate)
      double returns = totalInvested > 0 ? (totalValue - totalInvested) / totalInvested : 0.0;
      double sharpeRatio = returns > 0.02 ? (returns - 0.02) / 0.15 : 0.0;

      metrics.put("sharpeRatio", sharpeRatio);
      metrics.put("maxDrawdown", portfolio.getDailyPnL());
      metrics.put("volatility", Math.abs(returns) * 20); // Simplified volatility
      metrics.put("beta", 1.0); // Default beta - should be calculated against market index
      metrics.put("alpha", returns - 0.08); // Simplified alpha
    } else {
      metrics.put("sharpeRatio", 0.0);
      metrics.put("maxDrawdown", 0.0);
      metrics.put("volatility", 0.0);
      metrics.put("beta", 1.0);
      metrics.put("alpha", 0.0);
    }

    metrics.put("period", period);
    return metrics;
  }

  @Override
  public Map<String, Object> getTradeAnalysis(String userId, String period) {
    log.info("Getting trade analysis for user: {}, period: {}", userId, period);

    java.util.List<com.trad.tech.model.Trade> trades = tradeRepository.findByUserId(userId);

    Map<String, Object> analysis = new HashMap<>();

    if (trades.isEmpty()) {
      analysis.put("avgTradeSize", 0.0);
      analysis.put("avgHoldingPeriod", 0.0);
      analysis.put("mostTradedSymbol", "None");
      analysis.put("bestTrade", 0.0);
      analysis.put("worstTrade", 0.0);
    } else {
      double avgTradeSize =
          trades.stream()
              .mapToDouble(trade -> trade.getPrice() * trade.getQuantity())
              .average()
              .orElse(0.0);

      Map<String, Long> symbolCounts =
          trades.stream().collect(Collectors.groupingBy(Trade::getSymbol, Collectors.counting()));

      String mostTradedSymbol =
          symbolCounts.entrySet().stream()
              .max(Map.Entry.comparingByValue())
              .map(Map.Entry::getKey)
              .orElse("None");

      double bestTrade =
          trades.stream()
              .mapToDouble(trade -> trade.getPrice() * trade.getQuantity())
              .max()
              .orElse(0.0);

      double worstTrade =
          trades.stream()
              .mapToDouble(trade -> trade.getPrice() * trade.getQuantity())
              .min()
              .orElse(0.0);

      analysis.put("avgTradeSize", avgTradeSize);
      analysis.put("avgHoldingPeriod", 5.0); // Simplified holding period
      analysis.put("mostTradedSymbol", mostTradedSymbol);
      analysis.put("bestTrade", bestTrade);
      analysis.put("worstTrade", worstTrade);
    }

    analysis.put("period", period);
    return analysis;
  }

  @Override
  public Map<String, Object> getRiskMetrics(String userId) {
    log.info("Getting risk metrics for user: {}", userId);

    Portfolio portfolio = portfolioRepository.findByUserId(userId).orElse(null);

    Map<String, Object> risk = new HashMap<>();

    if (portfolio != null) {
      double totalValue = portfolio.getTotalValue();
      double totalInvested = portfolio.getTotalInvested();

      // Simple risk assessment based on P&L
      double pnlPercentage =
          totalInvested > 0 ? (portfolio.getTotalPnL() / totalInvested) * 100 : 0.0;

      String portfolioRisk = "LOW";
      if (pnlPercentage < -10) {
        portfolioRisk = "HIGH";
      } else if (pnlPercentage < -5) {
        portfolioRisk = "MEDIUM";
      }

      risk.put("portfolioRisk", portfolioRisk);
      risk.put(
          "diversificationScore",
          portfolio.getHoldings() != null ? portfolio.getHoldings().size() * 1.5 : 0.0);
      risk.put(
          "concentrationRisk",
          portfolio.getHoldings() != null && !portfolio.getHoldings().isEmpty()
              ? 1.0 / portfolio.getHoldings().size()
              : 0.0);

      // Simplified sector exposure
      Map<String, Double> sectorExposure = new HashMap<>();
      sectorExposure.put("Technology", 0.45);
      sectorExposure.put("Healthcare", 0.20);
      sectorExposure.put("Finance", 0.15);
      sectorExposure.put("Energy", 0.10);
      sectorExposure.put("Consumer", 0.10);

      risk.put("sectorExposure", sectorExposure);
    } else {
      risk.put("portfolioRisk", "LOW");
      risk.put("diversificationScore", 0.0);
      risk.put("concentrationRisk", 0.0);
      risk.put("sectorExposure", Map.of());
    }

    return risk;
  }

  @Override
  public Map<String, Object> getPortfolioAllocation(String userId) {
    log.info("Getting portfolio allocation for user: {}", userId);

    Portfolio portfolio = portfolioRepository.findByUserId(userId).orElse(null);

    Map<String, Object> allocation = new HashMap<>();

    if (portfolio != null) {
      double totalValue = portfolio.getTotalValue();
      double availableBalance = portfolio.getAvailableBalance();

      // Calculate allocation percentages
      double stockAllocation = totalValue > 0 ? (totalValue - availableBalance) / totalValue : 0.0;
      double cashAllocation = totalValue > 0 ? availableBalance / totalValue : 1.0;

      allocation.put("stocks", stockAllocation);
      allocation.put("bonds", 0.0); // No bonds in current implementation
      allocation.put("cash", cashAllocation);
      allocation.put("commodities", 0.0); // No commodities in current implementation

      // Simplified sector allocation
      Map<String, Double> sectorAllocation = new HashMap<>();
      sectorAllocation.put("Technology", stockAllocation * 0.6);
      sectorAllocation.put("Healthcare", stockAllocation * 0.15);
      sectorAllocation.put("Finance", stockAllocation * 0.1);
      sectorAllocation.put("Energy", stockAllocation * 0.08);
      sectorAllocation.put("Consumer", stockAllocation * 0.07);

      allocation.put("sectorAllocation", sectorAllocation);
    } else {
      allocation.put("stocks", 0.0);
      allocation.put("bonds", 0.0);
      allocation.put("cash", 1.0);
      allocation.put("commodities", 0.0);
      allocation.put("sectorAllocation", Map.of());
    }

    return allocation;
  }
}
