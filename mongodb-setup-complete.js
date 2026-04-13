// MongoDB Setup Script for Trading Application
// Database: tradingdb

// Switch to tradingdb database
use tradingdb;

// Create collections and insert sample data

// 1. Users Collection
db.users.insertOne({
    "_id": "admin123",
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@gmail.com",
    "password": "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKk0iGpE1k0QF6J9QGwP1zvJe", // "Pass@123" encoded
    "phone": "+1234567890",
    "dateOfBirth": "1980-01-01",
    "address": {
        "street": "123 Admin St",
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "country": "USA"
    },
    "preferences": {
        "theme": "light",
        "language": "en",
        "notifications": true,
        "twoFactorEnabled": false
    },
    "security": {
        "loginAttempts": 0,
        "lastLogin": null,
        "lockedUntil": null,
        "passwordResetToken": null,
        "passwordResetExpires": null
    },
    "createdAt": new Date("2024-01-01T00:00:00Z"),
    "lastUpdated": new Date("2024-01-01T00:00:00Z"),
    "isVerified": true,
    "isActive": true
});

// 2. Orders Collection
db.orders.insertOne({
    "_id": "order123",
    "userId": "admin123",
    "symbol": "AAPL",
    "orderType": "MARKET",
    "side": "BUY",
    "quantity": 100,
    "price": 150.25,
    "stopPrice": null,
    "status": "EXECUTED",
    "createdDate": new Date("2024-01-01T10:00:00Z"),
    "executedDate": new Date("2024-01-01T10:00:05Z"),
    "executedPrice": 150.25,
    "executedQuantity": 100,
    "reason": null
});

// 3. Trades Collection
db.trades.insertOne({
    "_id": "trade123",
    "userId": "admin123",
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 100,
    "price": 150.25,
    "status": "EXECUTED",
    "createdAt": new Date("2024-01-01T10:00:00Z"),
    "updatedAt": new Date("2024-01-01T10:00:05Z")
});

// 4. Portfolios Collection
db.portfolios.insertOne({
    "_id": "portfolio123",
    "userId": "admin123",
    "name": "Main Portfolio",
    "totalValue": 15025.00,
    "availableBalance": 5000.00,
    "totalInvested": 10000.00,
    "totalPnL": 5025.00,
    "dailyPnL": 125.50,
    "lastUpdated": new Date("2024-01-01T16:00:00Z"),
    "holdings": [
        {
            "symbol": "AAPL",
            "quantity": 100,
            "averagePrice": 145.00,
            "currentPrice": 150.25,
            "totalValue": 15025.00,
            "pnl": 525.00,
            "pnlPercentage": 3.62
        }
    ]
});

// 5. Watchlists Collection
db.watchlists.insertOne({
    "_id": "watchlist123",
    "userId": "admin123",
    "name": "Tech Stocks",
    "symbols": ["AAPL", "GOOGL", "MSFT", "TSLA"],
    "createdDate": new Date("2024-01-01T00:00:00Z"),
    "lastUpdated": new Date("2024-01-01T12:00:00Z"),
    "isDefault": true
});

// Create indexes for better performance
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "isActive": 1 });
db.users.createIndex({ "createdAt": 1 });

db.orders.createIndex({ "userId": 1 });
db.orders.createIndex({ "symbol": 1 });
db.orders.createIndex({ "status": 1 });
db.orders.createIndex({ "createdDate": 1 });
db.orders.createIndex({ "userId": 1, "status": 1 });

db.trades.createIndex({ "userId": 1 });
db.trades.createIndex({ "symbol": 1 });
db.trades.createIndex({ "status": 1 });
db.trades.createIndex({ "createdAt": 1 });
db.trades.createIndex({ "userId": 1, "createdAt": 1 });

db.portfolios.createIndex({ "userId": 1 }, { unique: true });
db.portfolios.createIndex({ "lastUpdated": 1 });

db.watchlists.createIndex({ "userId": 1 });
db.watchlists.createIndex({ "name": 1 });
db.watchlists.createIndex({ "createdDate": 1 });
db.watchlists.createIndex({ "userId": 1, "isDefault": 1 });

print("MongoDB setup completed successfully!");
print("Created collections: users, orders, trades, portfolios, watchlists");
print("Created indexes for optimal performance");
print("Sample data inserted for testing");
