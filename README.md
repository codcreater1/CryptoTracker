# CryptoTracker 🚀

Android uygulaması – CoinGecko API kullanarak gerçek zamanlı kripto para fiyatlarını gösterir.

## Özellikler
- Top 50 kripto para listesi (market cap sıralı)
- Gerçek zamanlı USD fiyatları + 24 saatlik değişim yüzdesi
- Coin arama (isim veya sembol)
- Pull-to-refresh (aşağı çekerek yenile)
- Detay ekranı: fiyat, market cap, hacim, 24h high/low, ATH, dolaşımdaki arz
- İnternet yokken anlamlı hata mesajı
- Karanlık tema

## Mimari
- **MVC** pattern
- **Model:** `Coin.java`
- **View:** `MainActivity`, `DetailActivity`, `CoinAdapter`
- **Controller:** `CoinController`, `RetrofitClient`, `ApiService`, `NetworkUtils`

## Kullanılan Kütüphaneler
| Kütüphane | Amaç |
|---|---|
| Retrofit 2 | HTTP API çağrıları |
| Gson | JSON parse |
| OkHttp | HTTP istemcisi |
| Glide | Coin logo yükleme |
| RecyclerView | Liste görünümü |
| SwipeRefreshLayout | Pull-to-refresh |

## API
- **CoinGecko** – Ücretsiz, API key gerektirmez
- Endpoint: `GET /coins/markets?vs_currency=usd&order=market_cap_desc`

## Kurulum
1. Android Studio'da **File → Open** → bu klasörü seç
2. Gradle sync tamamlanmasını bekle
3. Emülatör veya fiziksel cihazda **Run** (▶️)

## Testler
- **Unit testler:** `app/src/test/` → Android Studio'da sağ tık → Run
- **UI testler:** `app/src/androidTest/` → Emülatör gerektirir
