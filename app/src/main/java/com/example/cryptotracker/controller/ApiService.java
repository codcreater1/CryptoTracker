package com.example.cryptotracker.controller;

import com.example.cryptotracker.model.Coin;
import com.example.cryptotracker.model.MarketChartResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface defining all CoinGecko API endpoints used by the app.
 * Base URL: https://api.coingecko.com/api/v3/
 */
public interface ApiService {

    /**
     * Fetches a paginated list of coins ranked by market capitalisation.
     */
    @GET("coins/markets")
    Call<List<Coin>> getCoins(
            @Query("vs_currency") String vsCurrency,
            @Query("order")       String order,
            @Query("per_page")    int    perPage,
            @Query("page")        int    page,
            @Query("sparkline")   boolean sparkline
    );

    /**
     * Fetches 7-day hourly market chart data for a single coin.
     *
     * @param coinId     CoinGecko coin ID (e.g. "bitcoin").
     * @param vsCurrency Target currency (e.g. "usd").
     * @param days       Number of days of data (e.g. 7).
     */
    @GET("coins/{id}/market_chart")
    Call<MarketChartResponse> getMarketChart(
            @Path("id")           String coinId,
            @Query("vs_currency") String vsCurrency,
            @Query("days")        int    days
    );
}
