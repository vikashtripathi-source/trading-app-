# MongoDB Setup for Trading Application

## Database Configuration
- **Database Name**: `trading_app`
- **Collection Name**: `users`
- **Connection URI**: `mongodb://localhost:27017/trading_app`

## Setup Instructions

### Option 1: Using MongoDB Shell
1. Open MongoDB Shell (mongosh or mongo)
2. Run the setup script:
   ```bash
   mongosh trading_app mongodb-setup.js
   ```

### Option 2: Manual Setup
1. Connect to MongoDB:
   ```bash
   mongosh
   ```

2. Switch to trading_app database:
   ```javascript
   use trading_app
   ```

3. Create admin user:
   ```javascript
   db.users.insertOne({
       "_id": "admin123",
       "firstName": "Admin",
       "lastName": "User",
       "email": "admin@gmail.com",
       "password": "$2a$10$YourHashedPasswordHere",
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
   ```

4. Create indexes:
   ```javascript
   db.users.createIndex({ "email": 1 }, { unique: true });
   db.users.createIndex({ "isActive": 1 });
   db.users.createIndex({ "createdAt": 1 });
   ```

## User Document Schema

The `users` collection should contain documents with the following structure:

```javascript
{
    "_id": "string",           // User ID
    "firstName": "string",      // First name
    "lastName": "string",       // Last name
    "email": "string",          // Email (unique)
    "password": "string",       // Hashed password
    "phone": "string",          // Phone number
    "dateOfBirth": "string",    // Date of birth
    "address": {                // Address object
        "street": "string",
        "city": "string",
        "state": "string",
        "zipCode": "string",
        "country": "string",
        "postalCode": "string"
    },
    "preferences": {            // User preferences
        "darkMode": boolean,
        "language": "string",
        "timezone": "string",
        "notifications": boolean
    },
    "security": {               // Security settings
        "twoFactorEnabled": boolean,
        "loginAttempts": number,
        "lockUntil": date
    },
    "createdAt": date,          // Account creation date
    "lastUpdated": date,        // Last update date
    "isVerified": boolean,      // Email verification status
    "isActive": boolean,        // Account active status
    "authorities": ["string"]   // User roles/permissions
}
```

## Verification

After setup, verify the installation:

1. Check if collection exists:
   ```javascript
   show collections
   ```

2. Verify admin user:
   ```javascript
   db.users.find({ email: "admin@gmail.com" }).pretty()
   ```

3. Check indexes:
   ```javascript
   db.users.getIndexes()
   ```

## Important Notes

- The password field will be automatically updated by Spring Security during login
- Email field has a unique index to prevent duplicate registrations
- The admin user can be used for testing the login functionality
- All dates are stored as ISODate objects in MongoDB
