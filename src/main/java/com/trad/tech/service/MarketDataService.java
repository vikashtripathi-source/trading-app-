package com.trad.tech.service;

import com.trad.tech.model.MarketData;
import java.util.List;

public interface MarketDataService {

  MarketData getMarketData(String symbol);

  List<MarketData> getBatchMarketData(List<String> symbols);

  Object getMarketIndices();

  List<MarketData> getTopGainers(int limit);

  List<MarketData> getTopLosers(int limit);

  List<Object> searchSymbols(String query, int limit);
}
