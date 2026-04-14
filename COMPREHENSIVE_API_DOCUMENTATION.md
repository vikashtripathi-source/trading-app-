# Trading Application - Comprehensive API Documentation

## Overview

This document provides complete API documentation for the Trading Application backend. The API follows RESTful conventions and uses JWT-based authentication.

**Base URL**: `http://localhost:8080/api`

**Authentication**: Bearer Token (JWT) required for most endpoints

## Response Format

All API responses follow the standard `ApiResponse<T>` format:

```json
{
  "status": 200,
  "message": "Success message",
  "data": { ... },
  "path": "/api/endpoint",
  "timestamp": "2024-01-01T12:00:00"
}
```

---

## Authentication API (`/api/auth`)

### User Registration
**POST** `/api/auth/register`

Register a new user account.

**Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "phone": "+1234567890",
  "dateOfBirth": "1990-01-01",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA",
    "postalCode": "10001"
  }
}
```

**Responses**:
- `201 Created`: User registered successfully
- `400 Bad Request`: Invalid user data
- `409 Conflict`: Email already exists

---

### User Login
**POST** `/api/auth/login`

Authenticate user and receive JWT token.

**Request Body**:
```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response**:
```json
{
  "status": 200,
  "message": "Login successful",
  "data": {
    "user": { ... },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Responses**:
- `200 OK`: Login successful
- `401 Unauthorized`: Invalid credentials

---

### User Logout
**POST** `/api/auth/logout`

Logout user and invalidate token.

**Headers**: `Authorization: Bearer {token}`

**Responses**:
- `200 OK`: Logout successful

---

### Get User Profile
**GET** `/api/auth/profile`

Get current user profile information.

**Headers**: `Authorization: Bearer {token}`

**Responses**:
- `200 OK`: Profile retrieved successfully
- `401 Unauthorized`: Invalid token

---

### Activate User
**GET** `/api/auth/activate/{email}`

Activate a user account (admin function).

**Path Parameters**:
- `email`: User email to activate

**Responses**:
- `200 OK`: User activated successfully
- `404 Not Found`: User not found

---

## Portfolio API (`/api/portfolios`)

### Get User Portfolio
**GET** `/api/portfolios/user/{userId}`

Retrieve complete portfolio information for a user.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `userId`: User ID

**Response**:
```json
{
  "status": 200,
  "data": {
    "id": "portfolio123",
    "userId": "user123",
    "name": "Main Portfolio",
    "totalValue": 50000.00,
    "availableBalance": 10000.00,
    "totalInvested": 40000.00,
    "totalPnL": 10000.00,
    "dailyPnL": 500.00,
    "holdings": [
      {
        "symbol": "AAPL",
        "quantity": 100,
        "averagePrice": 150.00,
        "currentPrice": 175.00,
        "totalValue": 17500.00,
        "pnl": 2500.00,
        "pnlPercentage": 16.67
      }
    ],
    "lastUpdated": "2024-01-01T12:00:00"
  }
}
```

**Responses**:
- `200 OK`: Portfolio retrieved successfully
- `404 Not Found`: Portfolio not found
- `401 Unauthorized`: Invalid token
- `403 Forbidden`: Access denied

---

### Get Portfolio Performance
**GET** `/api/portfolios/user/{userId}/performance`

Get portfolio performance metrics.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `userId`: User ID

**Query Parameters**:
- `period` (optional): Time period (default: "1M")

**Response**:
```json
{
  "status": 200,
  "data": {
    "totalValue": 50000.00,
    "totalInvested": 40000.00,
    "totalPnL": 10000.00,
    "dailyPnL": 500.00,
    "availableBalance": 10000.00,
    "totalReturn": 25.0,
    "holdingsCount": 5,
    "period": "1M",
    "lastUpdated": "2024-01-01T12:00:00"
  }
}
```

---

### Get Portfolio Holdings
**GET** `/api/portfolios/user/{userId}/holdings`

Retrieve all holdings in the portfolio.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `userId`: User ID

**Response**:
```json
{
  "status": 200,
  "data": [
    {
      "symbol": "AAPL",
      "quantity": 100,
      "averagePrice": 150.00,
      "currentPrice": 175.00,
      "totalValue": 17500.00,
      "pnl": 2500.00,
      "pnlPercentage": 16.67
    }
  ]
}
```

---

## Order API (`/api/orders`)

### Create Order
**POST** `/api/orders`

Create a new trading order.

**Headers**: `Authorization: Bearer {token}`

**Request Body**:
```json
{
  "symbol": "AAPL",
  "orderType": "LIMIT",
  "side": "BUY",
  "quantity": 100,
  "price": 150.00,
  "stopPrice": 0.00
}
```

**Order Types**: `MARKET`, `LIMIT`, `STOP_LOSS`, `STOP_LIMIT`
**Order Sides**: `BUY`, `SELL`
**Order Statuses**: `PENDING`, `EXECUTED`, `CANCELLED`, `REJECTED`, `PARTIALLY_FILLED`

**Responses**:
- `201 Created`: Order created successfully
- `400 Bad Request`: Invalid order data
- `401 Unauthorized`: Invalid token

---

### Get User Orders
**GET** `/api/orders/user/{userId}`

Retrieve all orders for a user.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `userId`: User ID

**Query Parameters**:
- `status` (optional): Filter by order status

**Response**:
```json
{
  "status": 200,
  "data": [
    {
      "id": "order123",
      "userId": "user123",
      "symbol": "AAPL",
      "orderType": "LIMIT",
      "side": "BUY",
      "quantity": 100,
      "price": 150.00,
      "stopPrice": 0.00,
      "status": "PENDING",
      "createdDate": "2024-01-01T12:00:00",
      "executedDate": null,
      "executedPrice": 0.00,
      "executedQuantity": 0,
      "reason": null
    }
  ]
}
```

---

### Cancel Order
**PUT** `/api/orders/{id}/cancel`

Cancel a pending order.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Order ID

**Responses**:
- `200 OK`: Order cancelled successfully
- `404 Not Found`: Order not found
- `400 Bad Request`: Order cannot be cancelled
- `401 Unauthorized`: Invalid token

---

## Trade API (`/api/trades`)

### Get User Trades
**GET** `/api/trades`

Retrieve all trades for the authenticated user.

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "status": 200,
  "data": [
    {
      "id": "trade123",
      "userId": "user123",
      "symbol": "AAPL",
      "type": "BUY",
      "quantity": 100,
      "price": 150.00,
      "status": "EXECUTED",
      "createdAt": "2024-01-01T12:00:00",
      "updatedAt": "2024-01-01T12:00:00"
    }
  ]
}
```

---

### Get Trade by ID
**GET** `/api/trades/{id}`

Retrieve a specific trade by ID.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Trade ID

**Responses**:
- `200 OK`: Trade retrieved successfully
- `404 Not Found`: Trade not found
- `401 Unauthorized`: Invalid token

---

### Create Trade
**POST** `/api/trades`

Create a new trade.

**Headers**: `Authorization: Bearer {token}`

**Request Body**:
```json
{
  "symbol": "AAPL",
  "type": "BUY",
  "quantity": 100,
  "price": 150.00
}
```

**Trade Types**: `BUY`, `SELL`
**Trade Statuses**: `PENDING`, `EXECUTED`, `CANCELLED`, `REJECTED`

**Responses**:
- `201 Created`: Trade created successfully
- `400 Bad Request`: Invalid trade data
- `401 Unauthorized`: Invalid token

---

### Update Trade
**PUT** `/api/trades/{id}`

Update an existing trade.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Trade ID

**Request Body**: Same as Create Trade

**Responses**:
- `200 OK`: Trade updated successfully
- `404 Not Found`: Trade not found
- `400 Bad Request`: Invalid trade data
- `401 Unauthorized`: Invalid token

---

### Delete Trade
**DELETE** `/api/trades/{id}`

Delete a trade by ID.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Trade ID

**Responses**:
- `204 No Content`: Trade deleted successfully
- `404 Not Found`: Trade not found
- `401 Unauthorized`: Invalid token

---

## Market Data API (`/api/market`)

### Get Market Data for Symbol
**GET** `/api/market/data/{symbol}`

Retrieve current market data for a specific stock.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `symbol`: Stock symbol (e.g., "AAPL")

**Response**:
```json
{
  "status": 200,
  "data": {
    "symbol": "AAPL",
    "currentPrice": 175.50,
    "openPrice": 170.00,
    "highPrice": 180.00,
    "lowPrice": 168.00,
    "previousClose": 169.50,
    "change": 6.00,
    "changePercentage": 3.54,
    "volume": 50000000,
    "averageVolume": 45000000,
    "marketCap": 2800000000000,
    "peRatio": 28.5,
    "timestamp": 1640995200000
  }
}
```

---

### Get Batch Market Data
**GET** `/api/market/data/batch`

Retrieve market data for multiple symbols.

**Headers**: `Authorization: Bearer {token}`

**Query Parameters**:
- `symbols`: Comma-separated stock symbols (e.g., "AAPL,GOOGL,MSFT")

**Response**: Array of MarketData objects

---

### Get Market Indices
**GET** `/api/market/indices`

Retrieve major market indices data.

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "status": 200,
  "data": {
    "S&P 500": { "value": 4500.00, "change": 25.00, "changePercent": 0.56 },
    "DOW JONES": { "value": 35000.00, "change": 150.00, "changePercent": 0.43 },
    "NASDAQ": { "value": 14000.00, "change": 100.00, "changePercent": 0.72 }
  }
}
```

---

### Get Top Gainers
**GET** `/api/market/top-gainers`

Retrieve top gaining stocks.

**Headers**: `Authorization: Bearer {token}`

**Query Parameters**:
- `limit` (optional): Number of results (default: 10)

**Response**: Array of MarketData objects sorted by gain percentage

---

### Get Top Losers
**GET** `/api/market/top-losers`

Retrieve top losing stocks.

**Headers**: `Authorization: Bearer {token}`

**Query Parameters**:
- `limit` (optional): Number of results (default: 10)

**Response**: Array of MarketData objects sorted by loss percentage

---

### Search Symbols
**GET** `/api/market/search`

Search for stock symbols by name or symbol.

**Headers**: `Authorization: Bearer {token}`

**Query Parameters**:
- `query`: Search query
- `limit` (optional): Number of results (default: 10)

**Response**:
```json
{
  "status": 200,
  "data": [
    {
      "symbol": "AAPL",
      "name": "Apple Inc.",
      "sector": "Technology"
    }
  ]
}
```

---

## Watchlist API (`/api/watchlists`)

### Get User Watchlists
**GET** `/api/watchlists/user/{userId}`

Retrieve all watchlists for a user.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `userId`: User ID

**Response**:
```json
{
  "status": 200,
  "data": [
    {
      "id": "watchlist123",
      "userId": "user123",
      "name": "Tech Stocks",
      "symbols": ["AAPL", "GOOGL", "MSFT"],
      "createdDate": "2024-01-01T12:00:00",
      "lastUpdated": "2024-01-01T12:00:00",
      "isDefault": true
    }
  ]
}
```

---

### Create Watchlist
**POST** `/api/watchlists`

Create a new watchlist.

**Headers**: `Authorization: Bearer {token}`

**Request Body**:
```json
{
  "name": "Tech Stocks",
  "symbols": ["AAPL", "GOOGL", "MSFT"],
  "isDefault": false
}
```

**Responses**:
- `201 Created`: Watchlist created successfully
- `400 Bad Request`: Invalid watchlist data
- `401 Unauthorized`: Invalid token

---

### Get Watchlist by ID
**GET** `/api/watchlists/{id}`

Retrieve a specific watchlist.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Watchlist ID

**Responses**:
- `200 OK`: Watchlist retrieved successfully
- `404 Not Found`: Watchlist not found
- `401 Unauthorized`: Invalid token
- `403 Forbidden`: Access denied

---

### Add Symbol to Watchlist
**POST** `/api/watchlists/{id}/symbols`

Add a stock symbol to watchlist.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Watchlist ID

**Query Parameters**:
- `symbol`: Stock symbol to add

**Responses**:
- `200 OK`: Symbol added successfully
- `404 Not Found`: Watchlist not found
- `400 Bad Request`: Invalid symbol
- `401 Unauthorized`: Invalid token
- `403 Forbidden`: Access denied

---

### Remove Symbol from Watchlist
**DELETE** `/api/watchlists/{id}/symbols/{symbol}`

Remove a stock symbol from watchlist.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Watchlist ID
- `symbol`: Stock symbol to remove

**Responses**:
- `200 OK`: Symbol removed successfully
- `404 Not Found`: Watchlist not found
- `400 Bad Request`: Invalid symbol
- `401 Unauthorized`: Invalid token
- `403 Forbidden`: Access denied

---

### Delete Watchlist
**DELETE** `/api/watchlists/{id}`

Delete a watchlist.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Watchlist ID

**Responses**:
- `204 No Content`: Watchlist deleted successfully
- `404 Not Found`: Watchlist not found
- `401 Unauthorized`: Invalid token
- `403 Forbidden`: Access denied

---

## Analytics API (`/api/analytics`)

### Get Trading Statistics
**GET** `/api/analytics/user/{userId}/statistics`

Retrieve comprehensive trading statistics.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `userId`: User ID

**Query Parameters**:
- `period` (optional): Time period (default: "1M")

**Response**:
```json
{
  "status": 200,
  "data": {
    "totalTrades": 150,
    "winningTrades": 90,
    "losingTrades": 60,
    "winRate": 60.0,
    "totalProfit": 15000.00,
    "totalLoss": 8000.00,
    "netProfit": 7000.00,
    "averageWin": 166.67,
    "averageLoss": 133.33,
    "profitFactor": 1.875,
    "maxDrawdown": 2000.00,
    "sharpeRatio": 1.25
  }
}
```

---

### Get Performance Metrics
**GET** `/api/analytics/user/{userId}/performance`

Retrieve performance metrics.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `userId`: User ID

**Query Parameters**:
- `period` (optional): Time period (default: "1M")

**Response**:
```json
{
  "status": 200,
  "data": {
    "totalReturn": 15.5,
    "annualizedReturn": 18.2,
    "volatility": 12.3,
    "maxDrawdown": 8.7,
    "calmarRatio": 2.1,
    "sortinoRatio": 1.8,
    "beta": 0.95,
    "alpha": 3.2
  }
}
```

---

### Get Trade Analysis
**GET** `/api/analytics/user/{userId}/trade-analysis`

Retrieve detailed trade analysis.

**Headers**: `Authorization: Bearer {token}`

**Path Parameters**:
- `userId`: User ID

**Query Parameters**:
- `period` (optional): Time period (default: "1M")

**Response**:
```json
{
  "status": 200,
  "data": {
    "bestTrade": { "symbol": "AAPL", "profit": 2500.00, "return": 25.0 },
    "worstTrade": { "symbol": "TSLA", "loss": 1500.00, "return": -15.0 },
    "averageHoldTime": 5.2,
    "tradesByDay": { "Monday": 30, "Tuesday": 25, ... },
    "tradesBySector": { "Technology": 45, "Healthcare": 20, ... },
    "positionSizeAnalysis": {
      "small": 60,
      "medium": 30,
      "large": 10
    }
  }
}
```

---

## Error Handling

### Standard Error Response Format

```json
{
  "status": 400,
  "message": "Error description",
  "data": null,
  "path": "/api/endpoint",
  "timestamp": "2024-01-01T12:00:00"
}
```

### Common HTTP Status Codes

- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `204 No Content`: Resource deleted successfully
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required or invalid
- `403 Forbidden`: Access denied
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict (e.g., duplicate email)
- `500 Internal Server Error`: Server error

---

## Authentication & Security

### JWT Token Structure

```json
{
  "sub": "user123",
  "email": "user@example.com",
  "roles": ["ROLE_USER"],
  "iat": 1640995200,
  "exp": 1641081600
}
```

### Security Headers

All API endpoints require:
- `Authorization: Bearer {jwt_token}`

### Token Validation

- Tokens expire after 24 hours
- Include token in Authorization header for all protected endpoints
- Use `/api/auth/logout` to invalidate tokens

---

## Rate Limiting

- Authentication endpoints: 5 requests per minute
- Market data endpoints: 100 requests per minute
- All other endpoints: 60 requests per minute

---

## WebSocket Integration

Real-time updates are available through WebSocket connections for:
- Market data updates
- Order status changes
- Portfolio value updates
- Trade executions

**WebSocket Endpoint**: `ws://localhost:8080/ws`

**Connection**: Include JWT token as query parameter or in initial message.

---

## Data Models

### User Model
```json
{
  "id": "string",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phone": "string",
  "dateOfBirth": "string",
  "address": {
    "street": "string",
    "city": "string",
    "state": "string",
    "zipCode": "string",
    "country": "string",
    "postalCode": "string"
  },
  "preferences": {
    "darkMode": "boolean",
    "language": "string",
    "timezone": "string",
    "notifications": "boolean"
  },
  "security": {
    "twoFactorEnabled": "boolean",
    "loginAttempts": "number",
    "lockUntil": "datetime"
  },
  "createdAt": "datetime",
  "lastUpdated": "datetime",
  "verified": "boolean",
  "active": "boolean"
}
```

### Portfolio Model
```json
{
  "id": "string",
  "userId": "string",
  "name": "string",
  "totalValue": "number",
  "availableBalance": "number",
  "totalInvested": "number",
  "totalPnL": "number",
  "dailyPnL": "number",
  "holdings": [Holding],
  "lastUpdated": "datetime"
}
```

### Holding Model
```json
{
  "symbol": "string",
  "quantity": "number",
  "averagePrice": "number",
  "currentPrice": "number",
  "totalValue": "number",
  "pnl": "number",
  "pnlPercentage": "number"
}
```

---

## Testing

### Example cURL Commands

**Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'
```

**Get Portfolio**:
```bash
curl -X GET http://localhost:8080/api/portfolios/user/{userId} \
  -H "Authorization: Bearer {token}"
```

**Create Order**:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"symbol":"AAPL","orderType":"LIMIT","side":"BUY","quantity":100,"price":150.00}'
```

---

## Swagger/OpenAPI Documentation

Interactive API documentation is available at:
`http://localhost:8080/swagger-ui.html`

This provides:
- Interactive API testing
- Request/response examples
- Parameter documentation
- Authentication setup

---

## Support

For API support and questions:
- Check the Swagger UI for interactive testing
- Review error messages for detailed information
- Ensure proper authentication for protected endpoints
- Validate request data formats and constraints
