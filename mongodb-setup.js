// MongoDB Setup Script for Trading Application
// Database: trading_app
// Collection: users

// Switch to trading_app database
use trading_app;

// Create users collection with admin user
db.users.insertOne({
    "_id": "admin123",
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@gmail.com",
    "password": "$2a$10$YourHashedPasswordHere", // This will be updated by Spring Security
    "phone": "+1234567890",
    "dateOfBirth": "1990-01-01",
    "address": {
        "street": "123 Main St",
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "country": "USA",
        "postalCode": "12345"
    },
    "preferences": {
        "darkMode": false,
        "language": "en",
        "timezone": "UTC",
        "notifications": true
    },
    "security": {
        "twoFactorEnabled": false,
        "loginAttempts": 0,
        "lockUntil": null
    },
    "createdAt": new Date(),
    "lastUpdated": new Date(),
    "isVerified": true,
    "isActive": true,
    "authorities": ["ROLE_USER"]
});

// Create indexes for better performance
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "isActive": 1 });
db.users.createIndex({ "createdAt": 1 });

// Verify the setup
print("Users collection created successfully!");
print("Admin user inserted with email: admin@gmail.com");
print("Indexes created for email, isActive, and createdAt fields");

// Show the created user
db.users.find({ email: "admin@gmail.com" }).pretty();
