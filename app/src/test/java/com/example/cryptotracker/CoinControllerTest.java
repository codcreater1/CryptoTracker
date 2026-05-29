package com.example.cryptotracker;

import com.example.cryptotracker.controller.CoinController;
import com.example.cryptotracker.model.Coin;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CoinController}.
 *
 * These tests run on the JVM (no Android runtime needed) and cover
 * the search / filter logic that does not require network access.
 */
public class CoinControllerTest {

    private CoinController controller;
    private List<Coin>     sampleCoins;

    @Before
    public void setUp() {
        // Use a fake ApiService – search logic doesn't call the network
        controller  = new CoinController(null);
        sampleCoins = buildSampleCoins();
    }

    // ── searchCoins tests ──────────────────────────────────────────────────

    @Test
    public void searchCoins_emptyQuery_returnsAll() {
        List<Coin> result = controller.searchCoins(sampleCoins, "");
        assertEquals(sampleCoins.size(), result.size());
    }

    @Test
    public void searchCoins_nullQuery_returnsAll() {
        List<Coin> result = controller.searchCoins(sampleCoins, null);
        assertEquals(sampleCoins.size(), result.size());
    }

    @Test
    public void searchCoins_matchByName_caseSensitive() {
        List<Coin> result = controller.searchCoins(sampleCoins, "Bitcoin");
        assertEquals(1, result.size());
        assertEquals("Bitcoin", result.get(0).getName());
    }

    @Test
    public void searchCoins_matchByName_caseInsensitive() {
        List<Coin> result = controller.searchCoins(sampleCoins, "bitcoin");
        assertEquals(1, result.size());
    }

    @Test
    public void searchCoins_matchBySymbol() {
        List<Coin> result = controller.searchCoins(sampleCoins, "eth");
        assertEquals(1, result.size());
        assertEquals("Ethereum", result.get(0).getName());
    }

    @Test
    public void searchCoins_partialMatch() {
        // "coin" should match "Bitcoin" (contains "coin")
        List<Coin> result = controller.searchCoins(sampleCoins, "coin");
        assertEquals(1, result.size());
    }

    @Test
    public void searchCoins_noMatch_returnsEmpty() {
        List<Coin> result = controller.searchCoins(sampleCoins, "ZZZNOMATCH");
        assertTrue(result.isEmpty());
    }

    @Test
    public void searchCoins_whitespaceQuery_returnsAll() {
        List<Coin> result = controller.searchCoins(sampleCoins, "   ");
        assertEquals(sampleCoins.size(), result.size());
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    /**
     * Builds a minimal list of {@link Coin} objects using reflection-free
     * test doubles (we set fields via a tiny builder helper).
     */
    private List<Coin> buildSampleCoins() {
        List<Coin> coins = new ArrayList<>();
        coins.add(makeCoin("bitcoin",  "Bitcoin",  "btc"));
        coins.add(makeCoin("ethereum", "Ethereum", "eth"));
        coins.add(makeCoin("solana",   "Solana",   "sol"));
        return coins;
    }

    /**
     * Creates a {@link Coin} via Gson-style field injection using
     * {@link com.google.gson.Gson} – avoids needing public setters.
     */
    private Coin makeCoin(String id, String name, String symbol) {
        com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
        obj.addProperty("id",     id);
        obj.addProperty("name",   name);
        obj.addProperty("symbol", symbol);
        obj.addProperty("current_price", 1000.0);
        obj.addProperty("price_change_percentage_24h", 1.5);
        obj.addProperty("market_cap_rank", 1);
        return new com.google.gson.Gson().fromJson(obj, Coin.class);
    }
}
