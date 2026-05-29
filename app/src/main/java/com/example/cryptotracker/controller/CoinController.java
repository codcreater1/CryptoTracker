package com.example.cryptotracker.controller;

import com.example.cryptotracker.model.Coin;
import com.example.cryptotracker.model.MarketChartResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller layer between the View (Activities) and the Model/API.
 *
 * Responsibilities:
 *  - Fetch top coins list.
 *  - Fetch 7-day market chart for a single coin.
 *  - Filter/search coins in memory.
 *
 * Follows SRP and DIP (depends on abstractions, not concrete Views).
 */
public class CoinController {

    /** Callback for the coin list. */
    public interface CoinCallback {
        void onSuccess(List<Coin> coins);
        void onError(String errorMessage);
    }

    /** Callback for the market chart data. */
    public interface ChartCallback {
        void onSuccess(List<Double> prices);
        void onError(String errorMessage);
    }

    private final ApiService apiService;

    /** Creates a controller backed by the shared RetrofitClient. */
    public CoinController() {
        this.apiService = RetrofitClient.getInstance().getApiService();
    }

    /** Constructor for unit-test injection. */
    public CoinController(ApiService apiService) {
        this.apiService = apiService;
    }

    // ── Coin list ─────────────────────────────────────────────────────────────

    /**
     * Fetches the top {@code count} cryptocurrencies ranked by market cap (USD).
     */
    public void fetchTopCoins(int count, CoinCallback callback) {
        Call<List<Coin>> call = apiService.getCoins(
                "usd", "market_cap_desc", count, 1, false);

        call.enqueue(new Callback<List<Coin>>() {
            @Override
            public void onResponse(Call<List<Coin>> call, Response<List<Coin>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Coin>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // ── Market chart ──────────────────────────────────────────────────────────

    /**
     * Fetches 7-day hourly price data for a single coin.
     *
     * @param coinId   CoinGecko coin ID (e.g. "bitcoin").
     * @param callback Receives a flat list of price values or an error.
     */
    public void fetchMarketChart(String coinId, ChartCallback callback) {
        Call<MarketChartResponse> call = apiService.getMarketChart(coinId, "usd", 7);

        call.enqueue(new Callback<MarketChartResponse>() {
            @Override
            public void onResponse(Call<MarketChartResponse> call,
                                   Response<MarketChartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getPriceValues());
                } else {
                    callback.onError("Chart error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MarketChartResponse> call, Throwable t) {
                callback.onError("Chart network error: " + t.getMessage());
            }
        });
    }

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Filters coins by name or symbol (case-insensitive, in-memory).
     */
    public List<Coin> searchCoins(List<Coin> coins, String query) {
        if (query == null || query.trim().isEmpty()) return coins;
        List<Coin> results = new ArrayList<>();
        String lower = query.toLowerCase().trim();
        for (Coin coin : coins) {
            if (coin.getName().toLowerCase().contains(lower)
                    || coin.getSymbol().toLowerCase().contains(lower)) {
                results.add(coin);
            }
        }
        return results;
    }
}
