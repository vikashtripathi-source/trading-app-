package com.trad.tech.ServiceImpl;

import com.trad.tech.model.MarketData;
import com.trad.tech.model.MarketDataEntity;
import com.trad.tech.model.MarketIndex;
import com.trad.tech.model.StockSymbol;
import com.trad.tech.repository.MarketDataRepository;
import com.trad.tech.repository.MarketIndexRepository;
import com.trad.tech.repository.StockSymbolRepository;
import com.trad.tech.service.MarketDataService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketDataServiceImpl implements MarketDataService {

  private final MarketDataRepository marketDataRepository;
  private final MarketIndexRepository marketIndexRepository;
  private final StockSymbolRepository stockSymbolRepository;

  @Override
  public MarketData getMarketData(String symbol) {
    log.info("Getting market data for symbol: {}", symbol);

    MarketDataEntity entity =
        marketDataRepository
            .findBySymbol(symbol)
            .orElseThrow(() -> new RuntimeException("Market data not found for symbol: " + symbol));

    return convertToMarketData(entity);
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

  @Override
  public List<MarketData> getBatchMarketData(List<String> symbols) {
    log.info("Getting batch market data for symbols: {}", symbols);

    List<MarketDataEntity> entities = marketDataRepository.findBySymbolIn(symbols);
    return entities.stream().map(this::convertToMarketData).collect(Collectors.toList());
  }

  @Override
  public Object getMarketIndices() {
    log.info("Getting market indices");

    List<MarketIndex> indices = marketIndexRepository.findAll();
    Map<String, Object> result = new HashMap<>();

    for (MarketIndex index : indices) {
      Map<String, Object> indexData = new HashMap<>();
      indexData.put("value", index.getValue());
      indexData.put("change", index.getChange());
      indexData.put("changePercent", index.getChangePercent());
      result.put(index.getName(), indexData);
    }

    return result;
  }

  @Override
  public List<MarketData> getTopGainers(int limit) {
    log.info("Getting top {} gainers", limit);

    List<MarketDataEntity> topGainers = marketDataRepository.findTop10ByOrderByChangeDesc();
    return topGainers.stream()
        .limit(limit)
        .map(this::convertToMarketData)
        .collect(Collectors.toList());
  }

  @Override
  public List<MarketData> getTopLosers(int limit) {
    log.info("Getting top {} losers", limit);

    List<MarketDataEntity> topLosers = marketDataRepository.findTop10ByOrderByChangeAsc();
    return topLosers.stream()
        .limit(limit)
        .map(this::convertToMarketData)
        .collect(Collectors.toList());
  }

  @Override
  public List<Object> searchSymbols(String query, int limit) {
    log.info("Searching symbols with query: {}, limit: {}", query, limit);

    List<StockSymbol> symbols =
        stockSymbolRepository.findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(
            query, query);
    return symbols.stream()
        .limit(limit)
        .map(this::convertToSearchResult)
        .collect(Collectors.toList());
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
