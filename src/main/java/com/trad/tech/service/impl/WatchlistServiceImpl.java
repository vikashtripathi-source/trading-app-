package com.trad.tech.service.impl;

import com.trad.tech.model.Watchlist;
import com.trad.tech.repository.WatchlistRepository;
import com.trad.tech.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistServiceImpl implements WatchlistService {
    
    private final WatchlistRepository watchlistRepository;
    
    @Override
    public List<Watchlist> getUserWatchlists(String userId) {
        log.info("Getting watchlists for user: {}", userId);
        
        return watchlistRepository.findByUserIdOrderByCreatedDateDesc(userId);
    }
    
    @Override
    public Watchlist createWatchlist(Watchlist watchlist) {
        log.info("Creating new watchlist: {}", watchlist);
        
        // Set creation date and ID if not set
        if (watchlist.getId() == null) {
            watchlist.setId(UUID.randomUUID().toString());
        }
        if (watchlist.getCreatedDate() == null) {
            watchlist.setCreatedDate(LocalDateTime.now());
        }
        if (watchlist.getLastUpdated() == null) {
            watchlist.setLastUpdated(LocalDateTime.now());
        }
        
        return watchlistRepository.save(watchlist);
    }
    
    @Override
    public Watchlist getWatchlistById(String id) {
        log.info("Getting watchlist by ID: {}", id);
        
        return watchlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Watchlist not found with ID: " + id));
    }
    
    @Override
    public Watchlist addSymbolToWatchlist(String id, String symbol) {
        log.info("Adding symbol {} to watchlist: {}", symbol, id);
        
        Watchlist watchlist = getWatchlistById(id);
        
        // Add symbol if not already present
        if (!watchlist.getSymbols().contains(symbol)) {
            watchlist.getSymbols().add(symbol);
            watchlist.setLastUpdated(LocalDateTime.now());
            
            return watchlistRepository.save(watchlist);
        }
        
        return watchlist;
    }
    
    @Override
    public Watchlist removeSymbolFromWatchlist(String id, String symbol) {
        log.info("Removing symbol {} from watchlist: {}", symbol, id);
        
        Watchlist watchlist = getWatchlistById(id);
        
        // Remove symbol if present
        if (watchlist.getSymbols().remove(symbol)) {
            watchlist.setLastUpdated(LocalDateTime.now());
            
            return watchlistRepository.save(watchlist);
        }
        return watchlist;
    }
    
    @Override
    public void deleteWatchlist(String id) {
        log.info("Deleting watchlist: {}", id);
        
        watchlistRepository.deleteById(id);
    }
}
