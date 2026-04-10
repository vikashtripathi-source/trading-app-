package com.trad.tech.dto;

import lombok.Data;

@Data
public class MarketDataUpdate {
    
    private String symbol;
    private double price;
    private double change;
    private long timestamp;
}
