package com.example.cryptotracker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Maps the JSON response from the CoinGecko /market_chart endpoint.
 *
 * The "prices" field is a list of [timestamp, price] pairs.
 * We only need the price values for the sparkline chart.
 */
public class MarketChartResponse {

    /**
     * Each inner list contains exactly two elements: [unixTimestampMs, priceUsd].
     */
    @SerializedName("prices")
    private List<List<Double>> prices;

    /**
     * Returns the raw price matrix from the API.
     */
    public List<List<Double>> getPrices() {
        return prices;
    }

    /**
     * Extracts only the price values (index 1 of each pair) into a flat list.
     *
     * @return List of USD prices, oldest first.
     */
    public List<Double> getPriceValues() {
        List<Double> values = new java.util.ArrayList<>();
        if (prices != null) {
            for (List<Double> pair : prices) {
                if (pair != null && pair.size() >= 2) {
                    values.add(pair.get(1));
                }
            }
        }
        return values;
    }
}
