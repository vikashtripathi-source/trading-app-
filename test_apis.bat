@echo off
echo Testing various API endpoints...

echo.
echo 1. Testing login endpoint...
powershell -Command "$body = '{\"email\":\"test.user@example.com\",\"password\":\"TestPass123\"}'; try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing; Write-Host 'Login Status:' $response.StatusCode } catch { Write-Host 'Login Error:' $_.Exception.Message }"

echo.
echo 2. Testing market data endpoint...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/api/market/data/AAPL' -Method GET -UseBasicParsing; Write-Host 'Market Data Status:' $response.StatusCode } catch { Write-Host 'Market Data Error:' $_.Exception.Message }"

echo.
echo 3. Testing portfolio endpoint...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/api/portfolios/user/test-user-id' -Method GET -UseBasicParsing; Write-Host 'Portfolio Status:' $response.StatusCode } catch { Write-Host 'Portfolio Error:' $_.Exception.Message }"

echo.
echo 4. Testing orders endpoint...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/api/orders/user/test-user-id' -Method GET -UseBasicParsing; Write-Host 'Orders Status:' $response.StatusCode } catch { Write-Host 'Orders Error:' $_.Exception.Message }"
