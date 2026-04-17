// MongoDB Setup Script for Trading Application
// Database: trading_app
// Collection: users

// Switch to trading_app database
use trading_app;

// Note: Users will be created through the registration process
// No hardcoded users are inserted to avoid conflicts with dynamic user data

// Create indexes for better performance
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "isActive": 1 });
db.users.createIndex({ "createdAt": 1 });

// Verify the setup
print("Users collection created successfully!");
print("Indexes created for email, isActive, and createdAt fields");
print("Ready for user registration through the application");
