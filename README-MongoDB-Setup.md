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

3. Note: Users will be created through the application registration process
   - No hardcoded users are inserted to avoid conflicts with dynamic user data
   - The application will handle user creation when users register

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

2. Check indexes:
   ```javascript
   db.users.getIndexes()
   ```

3. Verify database is ready:
   ```javascript
   db.stats()
   ```

## Important Notes

- The password field will be automatically updated by Spring Security during login
- Email field has a unique index to prevent duplicate registrations
- Users will be created through the application registration process
- All dates are stored as ISODate objects in MongoDB
