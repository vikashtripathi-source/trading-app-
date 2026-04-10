package com.trad.tech.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketData {
    
    private String symbol;
    
    private double currentPrice;
    
    private double openPrice;
    
    private double highPrice;
    
    private double lowPrice;
    
    private double previousClose;
    
    private double change;
    
    private double changePercentage;
    
    private long volume;
    
    private long averageVolume;
    
    private double marketCap;
    
    private double peRatio;
    
    private long timestamp;
    
    // Additional getters/setters to support service implementations
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public void setPrice(double price) {
        this.currentPrice = price;
    }
    
    public void setChange(double change) {
        this.change = change;
    }
    
    public void setChangePercent(double changePercent) {
        this.changePercentage = changePercent;
    }
    
    public void setVolume(long volume) {
        this.volume = volume;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
