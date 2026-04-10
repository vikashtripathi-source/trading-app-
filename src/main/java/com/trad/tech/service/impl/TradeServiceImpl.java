package com.trad.tech.service.impl;

import com.trad.tech.exception.InvalidTradeDataException;
import com.trad.tech.exception.TradeAlreadyDeletedException;
import com.trad.tech.exception.TradeNotFoundException;
import com.trad.tech.kafka.TradeProducer;
import com.trad.tech.model.Trade;
import com.trad.tech.repository.TradeRepository;
import com.trad.tech.service.TradeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TradeServiceImpl implements com.trad.tech.service.TradeService {

    private final TradeRepository repository;
    private final TradeProducer producer;

    public TradeServiceImpl(TradeRepository repository, TradeProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    @Override
    public Trade createTrade(Trade trade) {
        validateTradeData(trade);
        Trade saved = repository.save(trade);
        producer.sendTrade(saved);
        return saved;
    }

    @Override
    public List<Trade> getAllTrades() {
        return repository.findAll();
    }

    @Override
    public Trade getTrade(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTradeDataException("Trade ID cannot be null or empty");
        }
        return repository.findById(id)
                .orElseThrow(() -> new TradeNotFoundException("Trade not found with ID: " + id));
    }

    @Override
    public Trade updateTrade(String id, Trade trade) {
        validateTradeData(trade);
        Trade existing = getTrade(id);
        
        existing.setSymbol(trade.getSymbol());
        existing.setType(trade.getType());
        existing.setQuantity(trade.getQuantity());
        existing.setPrice(trade.getPrice());
        existing.setStatus(trade.getStatus());
        
        return repository.save(existing);
    }

    @Override
    public void deleteTrade(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTradeDataException("Trade ID cannot be null or empty");
        }
        
        Optional<Trade> tradeOptional = repository.findById(id);
        if (tradeOptional.isEmpty()) {
            throw new TradeAlreadyDeletedException(id, true);
        }
        
        repository.deleteById(id);
    }

    @Override
    public List<Trade> getUserTrades(String userId) {
        // TODO: Implement actual database query
        return repository.findAll(); // For now, return all trades
    }

    @Override
    public Trade createTradeForUser(String userId, Trade trade) {
        trade.setUserId(userId);
        return createTrade(trade);
    }
    
    private void validateTradeData(Trade trade) {
        if (trade == null) {
            throw new InvalidTradeDataException("Trade data cannot be null");
        }
        
        if (trade.getSymbol() == null || trade.getSymbol().trim().isEmpty()) {
            throw new InvalidTradeDataException("Trade symbol is required");
        }
        
        if (trade.getType() == null || trade.getType().trim().isEmpty()) {
            throw new InvalidTradeDataException("Trade type is required");
        }
        
        if (!"BUY".equalsIgnoreCase(trade.getType()) && !"SELL".equalsIgnoreCase(trade.getType())) {
            throw new InvalidTradeDataException("Trade type must be either 'BUY' or 'SELL'");
        }
        
        if (trade.getQuantity() <= 0) {
            throw new InvalidTradeDataException("Trade quantity must be greater than 0");
        }
        
        if (trade.getPrice() <= 0) {
            throw new InvalidTradeDataException("Trade price must be greater than 0");
        }
    }
}
