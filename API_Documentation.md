# Trading Application API Documentation

## Base URL
```
http://localhost:8081
```

## Authentication
All API endpoints (except registration and login) require JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### Authentication APIs (`/api/auth`)

#### User Registration
- **POST** `/api/auth/register`
- **Description**: Register a new user
- **Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "1234567890",
  "dateOfBirth": "1990-01-01",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }
}
```
- **Response**: `201 Created` - User object

#### User Login
- **POST** `/api/auth/login`
- **Description**: Authenticate user and return JWT token
- **Request Body**:
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
- **Response**: `200 OK` - LoginResponse with user and token

#### User Logout
- **POST** `/api/auth/logout`
- **Description**: Logout user and invalidate token
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK`

#### Get User Profile
- **GET** `/api/auth/profile`
- **Description**: Get current user profile
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - User object

---

### Order Management APIs (`/api/orders`)

#### Create Order
- **POST** `/api/orders`
- **Description**: Create a new trading order
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**: Order object
- **Response**: `201 Created` - Order object

#### Get User Orders
- **GET** `/api/orders/user/{userId}`
- **Description**: Get all orders for a user
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: `status` (optional) - Filter by order status
- **Response**: `200 OK` - List of orders

#### Cancel Order
- **PUT** `/api/orders/{id}/cancel`
- **Description**: Cancel a pending order
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - Updated order

---

### Portfolio Management APIs (`/api/portfolios`)

#### Get User Portfolio
- **GET** `/api/portfolios/user/{userId}`
- **Description**: Get complete portfolio for a user
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - Portfolio object

#### Get Portfolio Performance
- **GET** `/api/portfolios/user/{userId}/performance`
- **Description**: Get portfolio performance metrics
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: `period` (default: "1M") - Time period
- **Response**: `200 OK` - Performance data

#### Get Portfolio Holdings
- **GET** `/api/portfolios/user/{userId}/holdings`
- **Description**: Get all holdings in the portfolio
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - List of holdings

---

### Watchlist Management APIs (`/api/watchlists`)

#### Get User Watchlists
- **GET** `/api/watchlists/user/{userId}`
- **Description**: Get all watchlists for a user
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - List of watchlists

#### Create Watchlist
- **POST** `/api/watchlists`
- **Description**: Create a new watchlist
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**: Watchlist object
- **Response**: `201 Created` - Watchlist object

#### Get Watchlist by ID
- **GET** `/api/watchlists/{id}`
- **Description**: Get a specific watchlist
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - Watchlist object

#### Add Symbol to Watchlist
- **POST** `/api/watchlists/{id}/symbols`
- **Description**: Add a stock symbol to watchlist
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: `symbol` - Stock symbol
- **Response**: `200 OK` - Updated watchlist

#### Remove Symbol from Watchlist
- **DELETE** `/api/watchlists/{id}/symbols/{symbol}`
- **Description**: Remove a stock symbol from watchlist
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - Updated watchlist

#### Delete Watchlist
- **DELETE** `/api/watchlists/{id}`
- **Description**: Delete a watchlist
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `204 No Content`

---

### Market Data APIs (`/api/market`)

#### Get Market Data for Symbol
- **GET** `/api/market/data/{symbol}`
- **Description**: Get current market data for a stock
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - MarketData object

#### Get Batch Market Data
- **GET** `/api/market/data/batch`
- **Description**: Get market data for multiple stocks
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: `symbols` - Comma-separated stock symbols
- **Response**: `200 OK` - List of MarketData

#### Get Market Indices
- **GET** `/api/market/indices`
- **Description**: Get major market indices data
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - Indices data

#### Get Top Gainers
- **GET** `/api/market/top-gainers`
- **Description**: Get top gaining stocks
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: `limit` (default: 10) - Number of results
- **Response**: `200 OK` - List of MarketData

#### Get Top Losers
- **GET** `/api/market/top-losers`
- **Description**: Get top losing stocks
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: `limit` (default: 10) - Number of results
- **Response**: `200 OK` - List of MarketData

#### Search Symbols
- **GET** `/api/market/search`
- **Description**: Search for stock symbols
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: 
  - `query` - Search query
  - `limit` (default: 10) - Number of results
- **Response**: `200 OK` - Search results

---

### Trade Management APIs (`/api/trades`)

#### Get User Trades
- **GET** `/api/trades`
- **Description**: Get all trades for authenticated user
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - List of trades

#### Get Trade by ID
- **GET** `/api/trades/{id}`
- **Description**: Get a specific trade
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - Trade object

#### Create Trade
- **POST** `/api/trades`
- **Description**: Create a new trade
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**: Trade object
- **Response**: `201 Created` - Trade object

#### Update Trade
- **PUT** `/api/trades/{id}`
- **Description**: Update an existing trade
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**: Trade object
- **Response**: `200 OK` - Updated trade

#### Delete Trade
- **DELETE** `/api/trades/{id}`
- **Description**: Delete a trade
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `204 No Content`

---

### Analytics APIs (`/api/analytics`)

#### Get Trading Statistics
- **GET** `/api/analytics/user/{userId}/statistics`
- **Description**: Get comprehensive trading statistics
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: `period` (default: "1M") - Time period
- **Response**: `200 OK` - Statistics data

#### Get Performance Metrics
- **GET** `/api/analytics/user/{userId}/performance`
- **Description**: Get performance metrics
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: `period` (default: "1M") - Time period
- **Response**: `200 OK` - Performance metrics

#### Get Trade Analysis
- **GET** `/api/analytics/user/{userId}/trade-analysis`
- **Description**: Get detailed trade analysis
- **Headers**: `Authorization: Bearer <token>`
- **Query Params**: `period` (default: "1M") - Time period
- **Response**: `200 OK` - Trade analysis data

---

### Admin APIs (`/api/admin`)

#### Get All Users
- **GET** `/api/admin/users`
- **Description**: Get all users (admin only)
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - List of users

#### Get User by ID
- **GET** `/api/admin/users/{id}`
- **Description**: Get specific user (admin only)
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - User object

#### Update User
- **PUT** `/api/admin/users/{id}`
- **Description**: Update user (admin only)
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**: User object
- **Response**: `200 OK` - Updated user

#### Delete User
- **DELETE** `/api/admin/users/{id}`
- **Description**: Delete user (admin only)
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `204 No Content`

#### Get All Trades
- **GET** `/api/admin/trades`
- **Description**: Get all trades (admin only)
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - List of trades

#### Get All Orders
- **GET** `/api/admin/orders`
- **Description**: Get all orders (admin only)
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - List of orders

#### Get System Statistics
- **GET** `/api/admin/system/stats`
- **Description**: Get system statistics (admin only)
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` - System statistics

---

## Response Format

All API responses follow this format:
```json
{
  "status": 200,
  "message": "Success message",
  "data": {},
  "path": "/api/endpoint"
}
```

## Error Responses

- **400 Bad Request**: Invalid input data
- **401 Unauthorized**: Invalid or missing token
- **403 Forbidden**: Access denied
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource already exists (e.g., email already registered)
- **500 Internal Server Error**: Server error

## WebSocket Support

Real-time market data and trade updates are available via WebSocket connections. The WebSocket endpoint is configured for real-time communication.

## Swagger Documentation

Interactive API documentation is available at:
```
http://localhost:8081/swagger-ui.html
```

API docs in JSON format:
```
http://localhost:8081/api-docs
```
