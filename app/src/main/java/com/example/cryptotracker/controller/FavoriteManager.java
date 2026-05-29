package com.example.cryptotracker.controller;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages the user's favourite coins using {@link SharedPreferences}.
 *
 * Coin IDs (e.g. "bitcoin", "ethereum") are persisted as a String set
 * so they survive app restarts.
 *
 * Follows the Single Responsibility Principle – only handles favourite state.
 */
public class FavoriteManager {

    private static final String PREFS_NAME = "crypto_favorites";
    private static final String KEY_FAVS   = "favorite_ids";

    private final SharedPreferences prefs;

    public FavoriteManager(Context context) {
        prefs = context.getApplicationContext()
                       .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Returns {@code true} if the given coin ID is marked as favourite. */
    public boolean isFavorite(String coinId) {
        return getFavorites().contains(coinId);
    }

    /** Adds a coin ID to favourites. */
    public void addFavorite(String coinId) {
        Set<String> favs = new HashSet<>(getFavorites());
        favs.add(coinId);
        prefs.edit().putStringSet(KEY_FAVS, favs).apply();
    }

    /** Removes a coin ID from favourites. */
    public void removeFavorite(String coinId) {
        Set<String> favs = new HashSet<>(getFavorites());
        favs.remove(coinId);
        prefs.edit().putStringSet(KEY_FAVS, favs).apply();
    }

    /** Toggles favourite state and returns the new state. */
    public boolean toggleFavorite(String coinId) {
        if (isFavorite(coinId)) {
            removeFavorite(coinId);
            return false;
        } else {
            addFavorite(coinId);
            return true;
        }
    }

    /** Returns an unmodifiable copy of all saved favourite IDs. */
    public Set<String> getFavorites() {
        return prefs.getStringSet(KEY_FAVS, new HashSet<>());
    }
}
