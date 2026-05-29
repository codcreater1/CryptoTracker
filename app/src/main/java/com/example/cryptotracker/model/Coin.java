package com.example.cryptotracker.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class representing a single cryptocurrency.
 * Maps directly to the JSON response from the CoinGecko API.
 * Follows the Single Responsibility Principle (SRP) – only holds data.
 */
public class Coin {

    @SerializedName("id")
    private String id;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("current_price")
    private double currentPrice;

    @SerializedName("market_cap")
    private long marketCap;

    @SerializedName("market_cap_rank")
    private int marketCapRank;

    @SerializedName("price_change_percentage_24h")
    private double priceChangePercentage24h;

    @SerializedName("total_volume")
    private long totalVolume;

    @SerializedName("high_24h")
    private double high24h;

    @SerializedName("low_24h")
    private double low24h;

    @SerializedName("circulating_supply")
    private double circulatingSupply;

    @SerializedName("ath")
    private double allTimeHigh;

    // ── Getters ──────────────────────────────────────────────────────────────

    /** @return Unique coin identifier (e.g. "bitcoin") */
    public String getId() { return id; }

    /** @return Ticker symbol (e.g. "btc") */
    public String getSymbol() { return symbol; }

    /** @return Full display name (e.g. "Bitcoin") */
    public String getName() { return name; }

    /** @return URL of the coin's logo image */
    public String getImageUrl() { return imageUrl; }

    /** @return Current price in USD */
    public double getCurrentPrice() { return currentPrice; }

    /** @return Market capitalisation in USD */
    public long getMarketCap() { return marketCap; }

    /** @return Global market cap rank */
    public int getMarketCapRank() { return marketCapRank; }

    /** @return 24-hour price change as a percentage */
    public double getPriceChangePercentage24h() { return priceChangePercentage24h; }

    /** @return 24-hour trading volume in USD */
    public long getTotalVolume() { return totalVolume; }

    /** @return 24-hour high price in USD */
    public double getHigh24h() { return high24h; }

    /** @return 24-hour low price in USD */
    public double getLow24h() { return low24h; }

    /** @return Circulating supply of this coin */
    public double getCirculatingSupply() { return circulatingSupply; }

    /** @return All-time high price in USD */
    public double getAllTimeHigh() { return allTimeHigh; }
}
