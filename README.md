# CryptoTracker 🚀

An Android application built with Java that displays real-time cryptocurrency prices using the CoinGecko API.

## Features
- 📋 Top 50 cryptocurrencies ranked by market cap
- 💰 Real-time USD prices with 24h change percentage
- 🔍 Search coins by name or symbol
- 🔄 Auto-refresh every 60 seconds
- ⭐ Save favorite coins (persisted with SharedPreferences)
- 📊 7-day price sparkline chart on detail screen
- 📶 Offline error handling

## Architecture
- **Pattern:** MVC (Model-View-Controller)
- **Model:** `Coin.java`, `MarketChartResponse.java`
- **View:** `MainActivity`, `DetailActivity`, `CoinAdapter`, `SparkLine`
- **Controller:** `CoinController`, `RetrofitClient`, `ApiService`, `NetworkUtils`, `FavoriteManager`

## Tech Stack
| Library | Purpose |
|---|---|
| Retrofit 2 | HTTP API calls |
| Gson | JSON parsing |
| OkHttp | HTTP client |
| Glide | Image loading |
| RecyclerView | List display |
| SwipeRefreshLayout | Pull-to-refresh |

## API
- **CoinGecko Public API** – Free, no API key required
- Endpoint: `GET /coins/markets` (list), `GET /coins/{id}/market_chart` (chart)

## Testing
- **Unit Tests:** 8 tests – search/filter logic (`CoinControllerTest`)
- **UI Tests:** 2 tests – activity launch (`MainActivityTest`)

## Setup
1. Clone the repository
2. Open in Android Studio
3. Wait for Gradle sync
4. Run on emulator or device
