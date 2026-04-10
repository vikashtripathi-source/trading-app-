# Trading Application API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
All API endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Response Format
All responses follow this structure:
```json
{
  "data": {}, // or []
  "message": "Success message",
  "timestamp": "2026-04-10T13:47:00.000Z",
  "status": 200
}
```

Error responses:
```json
{
  "timestamp": "2026-04-10T13:47:00.000Z",
  "status": 404,
  "error": "Trade Not Found",
  "message": "Trade not found with ID: abc123",
  "path": "/api/trades/abc123"
}
```

---

## Trade API

### Get All Trades
```http
GET /api/trades
```

**Response:**
```json
{
  "data": [
    {
      "id": "64f8a1b2c3d4e5f6a7b8c9d0",
      "symbol": "AAPL",
      "type": "BUY",
      "quantity": 100,
      "price": 150.25,
      "status": "EXECUTED",
      "createdAt": "2026-04-10T10:30:00.000Z"
    }
  ]
}
```

### Get Trade by ID
```http
GET /api/trades/{id}
```

**Response:**
```json
{
  "data": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d0",
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 100,
    "price": 150.25,
    "status": "EXECUTED",
    "createdAt": "2026-04-10T10:30:00.000Z"
  }
}
```

### Create Trade
```http
POST /api/trades
```

**Request Body:**
```json
{
  "symbol": "AAPL",
  "type": "BUY",
  "quantity": 100,
  "price": 150.25,
  "status": "PENDING"
}
```

**Response:** `201 Created`
```json
{
  "data": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d0",
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 100,
    "price": 150.25,
    "status": "PENDING",
    "createdAt": "2026-04-10T13:47:00.000Z"
  }
}
```

### Update Trade
```http
PUT /api/trades/{id}
```

**Request Body:**
```json
{
  "symbol": "AAPL",
  "type": "BUY",
  "quantity": 150,
  "price": 155.50,
  "status": "EXECUTED"
}
```

### Delete Trade
```http
DELETE /api/trades/{id}
```

**Response:** `204 No Content`

---

## Portfolio API

### Get User Portfolio
```http
GET /api/portfolios/user/{userId}
```

**Response:**
```json
{
  "data": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d1",
    "userId": "user123",
    "name": "Main Portfolio",
    "totalValue": 50000.00,
    "availableBalance": 10000.00,
    "totalInvested": 40000.00,
    "totalPnL": 10000.00,
    "dailyPnL": 250.00,
    "lastUpdated": "2026-04-10T13:47:00.000Z",
    "holdings": [
      {
        "symbol": "AAPL",
        "quantity": 100,
        "averagePrice": 150.25,
        "currentPrice": 155.50,
        "totalValue": 15550.00,
        "pnl": 525.00,
        "pnlPercentage": 3.49
      }
    ]
  }
}
```

### Get Portfolio Performance
```http
GET /api/portfolios/user/{userId}/performance?period=1M
```

**Query Parameters:**
- `period`: 1D, 1W, 1M, 3M, 6M, 1Y, ALL

**Response:**
```json
{
  "data": {
    "period": "1M",
    "startValue": 45000.00,
    "endValue": 50000.00,
    "return": 5000.00,
    "returnPercentage": 11.11,
    "dailyReturns": [
      {"date": "2026-03-10", "value": 45200.00},
      {"date": "2026-03-11", "value": 45500.00}
    ],
    "benchmark": {
      "name": "S&P 500",
      "return": 8.50
    }
  }
}
```

### Get Portfolio Holdings
```http
GET /api/portfolios/user/{userId}/holdings
```

---

## Order API

### Create Order
```http
POST /api/orders
```

**Request Body:**
```json
{
  "userId": "user123",
  "symbol": "AAPL",
  "orderType": "LIMIT",
  "side": "BUY",
  "quantity": 100,
  "price": 150.00,
  "stopPrice": 0.0
}
```

**Order Types:**
- `MARKET`: Market order
- `LIMIT`: Limit order
- `STOP_LOSS`: Stop loss order
- `STOP_LIMIT`: Stop limit order

**Order Sides:**
- `BUY`
- `SELL`

**Response:** `201 Created`

### Get User Orders
```http
GET /api/orders/user/{userId}?status=PENDING
```

**Query Parameters:**
- `status` (optional): PENDING, EXECUTED, CANCELLED, REJECTED, PARTIALLY_FILLED

### Cancel Order
```http
PUT /api/orders/{id}/cancel
```

---

## Watchlist API

### Get User Watchlists
```http
GET /api/watchlists/user/{userId}
```

**Response:**
```json
{
  "data": [
    {
      "id": "64f8a1b2c3d4e5f6a7b8c9d2",
      "userId": "user123",
      "name": "Tech Stocks",
      "symbols": ["AAPL", "GOOGL", "MSFT", "TSLA"],
      "createdDate": "2026-04-01T10:00:00.000Z",
      "lastUpdated": "2026-04-10T13:47:00.000Z",
      "isDefault": true
    }
  ]
}
```

### Create Watchlist
```http
POST /api/watchlists
```

**Request Body:**
```json
{
  "userId": "user123",
  "name": "Energy Stocks",
  "symbols": ["XOM", "CVX", "BP"],
  "isDefault": false
}
```

### Add Symbol to Watchlist
```http
POST /api/watchlists/{id}/symbols?symbol=AAPL
```

### Remove Symbol from Watchlist
```http
DELETE /api/watchlists/{id}/symbols/AAPL
```

---

## Market Data API

### Get Market Data for Symbol
```http
GET /api/market/data/{symbol}
```

**Response:**
```json
{
  "data": {
    "symbol": "AAPL",
    "currentPrice": 155.50,
    "openPrice": 152.00,
    "highPrice": 156.75,
    "lowPrice": 151.25,
    "previousClose": 151.00,
    "change": 4.50,
    "changePercentage": 2.98,
    "volume": 52341000,
    "averageVolume": 45000000,
    "marketCap": 2450000000000,
    "peRatio": 28.5,
    "timestamp": 1649625600000
  }
}
```

### Get Batch Market Data
```http
GET /api/market/data/batch?symbols=AAPL,GOOGL,MSFT
```

### Get Market Indices
```http
GET /api/market/indices
```

**Response:**
```json
{
  "data": {
    "indices": [
      {
        "name": "S&P 500",
        "symbol": "^GSPC",
        "value": 4525.12,
        "change": 25.34,
        "changePercentage": 0.56
      },
      {
        "name": "NASDAQ",
        "symbol": "^IXIC",
        "value": 14250.89,
        "change": 75.23,
        "changePercentage": 0.53
      }
    ]
  }
}
```

### Get Top Gainers
```http
GET /api/market/top-gainers?limit=10
```

### Get Top Losers
```http
GET /api/market/top-losers?limit=10
```

### Search Symbols
```http
GET /api/market/search?query=Apple&limit=10
```

**Response:**
```json
{
  "data": [
    {
      "symbol": "AAPL",
      "name": "Apple Inc.",
      "type": "Equity",
      "exchange": "NASDAQ"
    }
  ]
}
```

---

## Analytics API

### Get Trading Statistics
```http
GET /api/analytics/user/{userId}/statistics?period=1M
```

**Response:**
```json
{
  "data": {
    "totalTrades": 45,
    "winningTrades": 28,
    "losingTrades": 17,
    "winRate": 62.22,
    "averageWin": 325.50,
    "averageLoss": -125.75,
    "profitFactor": 2.59,
    "totalPnL": 5850.00,
    "averageHoldingPeriod": 3.2
  }
}
```

### Get Performance Metrics
```http
GET /api/analytics/user/{userId}/performance?period=1M
```

**Response:**
```json
{
  "data": {
    "sharpeRatio": 1.85,
    "maxDrawdown": -8.5,
    "volatility": 15.2,
    "beta": 1.12,
    "alpha": 3.4,
    "totalReturn": 12.5,
    "annualizedReturn": 15.8
  }
}
```

### Get Trade Analysis
```http
GET /api/analytics/user/{userId}/trade-analysis?period=1M
```

**Response:**
```json
{
  "data": {
    "bestTrade": {
      "symbol": "TSLA",
      "pnl": 1250.00,
      "percentage": 8.5
    },
    "worstTrade": {
      "symbol": "NFLX",
      "pnl": -450.00,
      "percentage": -3.2
    },
    "symbolPerformance": [
      {"symbol": "AAPL", "trades": 12, "pnl": 850.00},
      {"symbol": "GOOGL", "trades": 8, "pnl": 425.00}
    ],
    "monthlyBreakdown": [
      {"month": "2026-03", "trades": 23, "pnl": 3200.00},
      {"month": "2026-04", "trades": 22, "pnl": 2650.00}
    ]
  }
}
```

---

## WebSocket API

### Real-time Updates

Connect to WebSocket for real-time updates:
```
ws://localhost:8080/ws
```

**Subscribe to topics:**
```json
{
  "action": "subscribe",
  "topics": ["market-data", "portfolio-updates", "order-status"]
}
```

**Market Data Update:**
```json
{
  "type": "market-data",
  "data": {
    "symbol": "AAPL",
    "price": 155.75,
    "change": 0.25,
    "timestamp": 1649625600000
  }
}
```

**Portfolio Update:**
```json
{
  "type": "portfolio-update",
  "data": {
    "userId": "user123",
    "totalValue": 50250.00,
    "dailyPnL": 250.00,
    "timestamp": 1649625600000
  }
}
```

**Order Status Update:**
```json
{
  "type": "order-status",
  "data": {
    "orderId": "64f8a1b2c3d4e5f6a7b8c9d3",
    "status": "EXECUTED",
    "executedPrice": 155.50,
    "timestamp": 1649625600000
  }
}
```

---

## Error Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 410 | Gone (Already Deleted) |
| 422 | Unprocessable Entity |
| 429 | Too Many Requests |
| 500 | Internal Server Error |

---

## Rate Limits

- **Standard endpoints**: 100 requests per minute
- **Market data endpoints**: 200 requests per minute
- **WebSocket connections**: 5 concurrent connections per user

---

## Testing

### Example cURL Commands

**Create Trade:**
```bash
curl -X POST http://localhost:8080/api/trades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 100,
    "price": 150.25,
    "status": "PENDING"
  }'
```

**Get Market Data:**
```bash
curl -X GET http://localhost:8080/api/market/data/AAPL \
  -H "Authorization: Bearer <token>"
```

**Get Portfolio:**
```bash
curl -X GET http://localhost:8080/api/portfolios/user/user123 \
  -H "Authorization: Bearer <token>"
```

---

## SDK Examples

### JavaScript/TypeScript

```typescript
// API Client Setup
class TradingAPI {
  constructor(private baseURL: string, private token: string) {}

  async createTrade(trade: Trade): Promise<Trade> {
    const response = await fetch(`${this.baseURL}/trades`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.token}`
      },
      body: JSON.stringify(trade)
    });
    return response.json();
  }

  async getPortfolio(userId: string): Promise<Portfolio> {
    const response = await fetch(`${this.baseURL}/portfolios/user/${userId}`, {
      headers: { 'Authorization': `Bearer ${this.token}` }
    });
    return response.json();
  }
}
```

### React Hook Example

```typescript
// Custom Hook for Trading
function usePortfolio(userId: string) {
  const [portfolio, setPortfolio] = useState<Portfolio | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchPortfolio = async () => {
      try {
        const response = await api.getPortfolio(userId);
        setPortfolio(response.data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchPortfolio();
  }, [userId]);

  return { portfolio, loading, error };
}
```
