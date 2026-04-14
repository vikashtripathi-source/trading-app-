// MongoDB Setup Script for Market Data Collections
// Database: tradingdb

// Switch to tradingdb database
use tradingdb;

// Create market_data collection with sample data
db.market_data.insertMany([
    {
        "_id": "market_1",
        "symbol": "AAPL",
        "currentPrice": 178.50,
        "openPrice": 175.20,
        "highPrice": 180.00,
        "lowPrice": 174.80,
        "previousClose": 176.30,
        "change": 2.20,
        "changePercentage": 1.25,
        "volume": 52345678,
        "averageVolume": 45000000,
        "marketCap": 2850000000000,
        "peRatio": 29.5,
        "timestamp": Date.now(),
        "createdAt": new Date(),
        "updatedAt": new Date()
    },
    {
        "_id": "market_2",
        "symbol": "GOOGL",
        "currentPrice": 142.80,
        "openPrice": 141.50,
        "highPrice": 143.20,
        "lowPrice": 140.90,
        "previousClose": 141.00,
        "change": 1.80,
        "changePercentage": 1.28,
        "volume": 28456789,
        "averageVolume": 25000000,
        "marketCap": 1820000000000,
        "peRatio": 25.8,
        "timestamp": Date.now(),
        "createdAt": new Date(),
        "updatedAt": new Date()
    },
    {
        "_id": "market_3",
        "symbol": "MSFT",
        "currentPrice": 378.90,
        "openPrice": 375.40,
        "highPrice": 380.00,
        "lowPrice": 374.80,
        "previousClose": 376.20,
        "change": 2.70,
        "changePercentage": 0.72,
        "volume": 19876543,
        "averageVolume": 22000000,
        "marketCap": 2810000000000,
        "peRatio": 32.1,
        "timestamp": Date.now(),
        "createdAt": new Date(),
        "updatedAt": new Date()
    },
    {
        "_id": "market_4",
        "symbol": "AMZN",
        "currentPrice": 145.30,
        "openPrice": 143.80,
        "highPrice": 146.50,
        "lowPrice": 142.90,
        "previousClose": 144.60,
        "change": 0.70,
        "changePercentage": 0.48,
        "volume": 45678901,
        "averageVolume": 48000000,
        "marketCap": 1500000000000,
        "peRatio": 45.2,
        "timestamp": Date.now(),
        "createdAt": new Date(),
        "updatedAt": new Date()
    },
    {
        "_id": "market_5",
        "symbol": "TSLA",
        "currentPrice": 245.60,
        "openPrice": 248.20,
        "highPrice": 250.00,
        "lowPrice": 244.30,
        "previousClose": 247.80,
        "change": -2.20,
        "changePercentage": -0.89,
        "volume": 98765432,
        "averageVolume": 110000000,
        "marketCap": 780000000000,
        "peRatio": 68.5,
        "timestamp": Date.now(),
        "createdAt": new Date(),
        "updatedAt": new Date()
    }
]);

// Create market_indices collection
db.market_indices.insertMany([
    {
        "_id": "index_1",
        "name": "S&P 500",
        "value": 4567.80,
        "change": 15.40,
        "changePercent": 0.34,
        "lastUpdated": new Date()
    },
    {
        "_id": "index_2",
        "name": "NASDAQ",
        "value": 14234.50,
        "change": 89.20,
        "changePercent": 0.63,
        "lastUpdated": new Date()
    },
    {
        "_id": "index_3",
        "name": "DOW",
        "value": 35678.90,
        "change": -45.30,
        "changePercent": -0.13,
        "lastUpdated": new Date()
    }
]);

// Create stock_symbols collection
db.stock_symbols.insertMany([
    {
        "_id": "symbol_1",
        "symbol": "AAPL",
        "name": "Apple Inc.",
        "type": "STOCK",
        "exchange": "NASDAQ",
        "sector": "Technology",
        "industry": "Consumer Electronics",
        "active": true
    },
    {
        "_id": "symbol_2",
        "symbol": "GOOGL",
        "name": "Alphabet Inc.",
        "type": "STOCK",
        "exchange": "NASDAQ",
        "sector": "Technology",
        "industry": "Internet Services",
        "active": true
    },
    {
        "_id": "symbol_3",
        "symbol": "MSFT",
        "name": "Microsoft Corporation",
        "type": "STOCK",
        "exchange": "NASDAQ",
        "sector": "Technology",
        "industry": "Software",
        "active": true
    },
    {
        "_id": "symbol_4",
        "symbol": "AMZN",
        "name": "Amazon.com Inc.",
        "type": "STOCK",
        "exchange": "NASDAQ",
        "sector": "Consumer Cyclical",
        "industry": "E-commerce",
        "active": true
    },
    {
        "_id": "symbol_5",
        "symbol": "TSLA",
        "name": "Tesla Inc.",
        "type": "STOCK",
        "exchange": "NASDAQ",
        "sector": "Consumer Cyclical",
        "industry": "Auto Manufacturers",
        "active": true
    },
    {
        "_id": "symbol_6",
        "symbol": "META",
        "name": "Meta Platforms Inc.",
        "type": "STOCK",
        "exchange": "NASDAQ",
        "sector": "Technology",
        "industry": "Internet Services",
        "active": true
    },
    {
        "_id": "symbol_7",
        "symbol": "NVDA",
        "name": "NVIDIA Corporation",
        "type": "STOCK",
        "exchange": "NASDAQ",
        "sector": "Technology",
        "industry": "Semiconductors",
        "active": true
    },
    {
        "_id": "symbol_8",
        "symbol": "NFLX",
        "name": "Netflix Inc.",
        "type": "STOCK",
        "exchange": "NASDAQ",
        "sector": "Technology",
        "industry": "Entertainment",
        "active": true
    },
    {
        "_id": "symbol_9",
        "symbol": "IBM",
        "name": "International Business Machines",
        "type": "STOCK",
        "exchange": "NYSE",
        "sector": "Technology",
        "industry": "IT Services",
        "active": true
    },
    {
        "_id": "symbol_10",
        "symbol": "INTC",
        "name": "Intel Corporation",
        "type": "STOCK",
        "exchange": "NASDAQ",
        "sector": "Technology",
        "industry": "Semiconductors",
        "active": true
    }
]);

// Create indexes for market_data collection
db.market_data.createIndex({ "symbol": 1 }, { unique: true });
db.market_data.createIndex({ "timestamp": -1 });
db.market_data.createIndex({ "change": -1 });
db.market_data.createIndex({ "change": 1 });
db.market_data.createIndex({ "volume": -1 });

// Create indexes for market_indices collection
db.market_indices.createIndex({ "name": 1 }, { unique: true });
db.market_indices.createIndex({ "lastUpdated": -1 });

// Create indexes for stock_symbols collection
db.stock_symbols.createIndex({ "symbol": 1 }, { unique: true });
db.stock_symbols.createIndex({ "name": "text" });
db.stock_symbols.createIndex({ "active": 1 });
db.stock_symbols.createIndex({ "sector": 1 });
db.stock_symbols.createIndex({ "exchange": 1 });

// Verify the setup
print("Market data collections created successfully!");
print("Collections created:");
print("- market_data: " + db.market_data.countDocuments() + " documents");
print("- market_indices: " + db.market_indices.countDocuments() + " documents");
print("- stock_symbols: " + db.stock_symbols.countDocuments() + " documents");
print("Indexes created for optimal performance");

// Show sample data
print("\nSample market data:");
db.market_data.find().limit(2).pretty();

print("\nSample market indices:");
db.market_indices.find().limit(2).pretty();

print("\nSample stock symbols:");
db.stock_symbols.find().limit(3).pretty();
