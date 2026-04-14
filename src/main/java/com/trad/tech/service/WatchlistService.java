package com.trad.tech.service;

import com.trad.tech.model.Watchlist;
import java.util.List;

public interface WatchlistService {

  List<Watchlist> getUserWatchlists(String userId);

  Watchlist createWatchlist(Watchlist watchlist);

  Watchlist getWatchlistById(String id);

  Watchlist addSymbolToWatchlist(String id, String symbol);

  Watchlist removeSymbolFromWatchlist(String id, String symbol);

  void deleteWatchlist(String id);
}
