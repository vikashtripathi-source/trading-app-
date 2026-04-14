package com.trad.tech.service;

import com.trad.tech.model.Trade;
import java.util.List;

public interface TradeService {

  Trade createTrade(Trade trade);

  List<Trade> getAllTrades();

  Trade getTrade(String id);

  Trade updateTrade(String id, Trade trade);

  void deleteTrade(String id);

  List<Trade> getUserTrades(String userId);

  Trade createTradeForUser(String userId, Trade trade);
}
