import requests

# Activate the user
response = requests.post("http://localhost:8081/api/auth/activate/vikash.tripathi@example.com")
print("Status Code:", response.status_code)
print("Response:", response.text)
