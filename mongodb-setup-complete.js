// MongoDB Setup Script for Trading Application
// Database: tradingdb

// Switch to tradingdb database
use tradingdb;

// Create collections and insert sample data

// 1. Users Collection
// Note: Users will be created through the registration process
// No hardcoded users are inserted to avoid conflicts with dynamic user data

// 2. Orders Collection
// Note: Sample orders will be created when users place actual orders
// No hardcoded orders to avoid conflicts with dynamic user data

// 3. Trades Collection
// Note: Sample trades will be created when orders are executed
// No hardcoded trades to avoid conflicts with dynamic user data

// 4. Portfolios Collection
// Note: Sample portfolios will be created when users register
// No hardcoded portfolios to avoid conflicts with dynamic user data

// 5. Watchlists Collection
// Note: Sample watchlists will be created when users create them
// No hardcoded watchlists to avoid conflicts with dynamic user data

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
print("Ready for dynamic user data through application registration");
