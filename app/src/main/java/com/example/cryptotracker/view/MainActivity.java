package com.example.cryptotracker.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cryptotracker.R;
import com.example.cryptotracker.adapter.CoinAdapter;
import com.example.cryptotracker.controller.CoinController;
import com.example.cryptotracker.controller.FavoriteManager;
import com.example.cryptotracker.controller.NetworkUtils;
import com.example.cryptotracker.model.Coin;

import java.util.ArrayList;
import java.util.List;

/**
 * Main screen: searchable list of top 50 cryptocurrencies.
 * Auto-refreshes every 30 seconds via a Handler/Runnable loop.
 */
public class MainActivity extends AppCompatActivity
        implements CoinController.CoinCallback, CoinAdapter.OnCoinClickListener {

    private static final int  COIN_COUNT     = 50;
    private static final long REFRESH_INTERVAL_MS = 60_000L; // 30 seconds

    // UI
    private RecyclerView       recyclerView;
    private ProgressBar        progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout       layoutError;
    private TextView           tvErrorMessage;
    private TextView           tvLastUpdated;
    private SearchView         searchView;

    // State
    private CoinAdapter     adapter;
    private CoinController  controller;
    private FavoriteManager favoriteManager;
    private List<Coin>      allCoins = new ArrayList<>();

    // Auto-refresh
    private final Handler  refreshHandler  = new Handler(Looper.getMainLooper());
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isFinishing()) {
                silentRefresh();
                refreshHandler.postDelayed(this, REFRESH_INTERVAL_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        favoriteManager = new FavoriteManager(this);
        controller      = new CoinController();

        bindViews();
        setupRecyclerView();
        setupSearchView();
        setupSwipeRefresh();
        loadCoins();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favorites badges when returning from DetailActivity
        adapter.notifyDataSetChanged();
        // Restart auto-refresh timer
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    // ── Init ──────────────────────────────────────────────────────────────────

    private void bindViews() {
        recyclerView   = findViewById(R.id.recyclerView);
        progressBar    = findViewById(R.id.progressBar);
        swipeRefresh   = findViewById(R.id.swipeRefresh);
        layoutError    = findViewById(R.id.layoutError);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvLastUpdated  = findViewById(R.id.tvLastUpdated);
        searchView     = findViewById(R.id.searchView);
    }

    private void setupRecyclerView() {
        adapter = new CoinAdapter(this, new ArrayList<>(), this, favoriteManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { filterCoins(q); return true; }
            @Override public boolean onQueryTextChange(String q) { filterCoins(q); return true; }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.purple_primary);
        swipeRefresh.setOnRefreshListener(this::loadCoins);
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    private void loadCoins() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showError(getString(R.string.error_no_internet));
            swipeRefresh.setRefreshing(false);
            return;
        }
        showLoading(true);
        controller.fetchTopCoins(COIN_COUNT, this);
    }

    /** Refresh without showing the spinner (background update). */
    private void silentRefresh() {
        if (!NetworkUtils.isNetworkAvailable(this)) return;
        controller.fetchTopCoins(COIN_COUNT, new CoinController.CoinCallback() {
            @Override
            public void onSuccess(List<Coin> coins) {
                runOnUiThread(() -> {
                    allCoins = coins;
                    String currentQuery = searchView.getQuery().toString();
                    adapter.updateData(controller.searchCoins(allCoins, currentQuery));
                    updateLastRefreshedTime();
                });
            }
            @Override public void onError(String errorMessage) { /* silent – don't disrupt UI */ }
        });
    }

    private void filterCoins(String query) {
        adapter.updateData(controller.searchCoins(allCoins, query));
    }

    private void updateLastRefreshedTime() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US);
        tvLastUpdated.setText("Updated " + sdf.format(new java.util.Date()));
        tvLastUpdated.setVisibility(View.VISIBLE);
    }

    // ── CoinCallback ──────────────────────────────────────────────────────────

    @Override
    public void onSuccess(List<Coin> coins) {
        runOnUiThread(() -> {
            allCoins = coins;
            adapter.updateData(coins);
            showLoading(false);
            layoutError.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            updateLastRefreshedTime();
            // Start auto-refresh after first successful load
            refreshHandler.removeCallbacks(refreshRunnable);
            refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL_MS);
        });
    }

    @Override
    public void onError(String errorMessage) {
        runOnUiThread(() -> {
            showLoading(false);
            showError(errorMessage);
            swipeRefresh.setRefreshing(false);
        });
    }

    // ── CoinAdapter.OnCoinClickListener ───────────────────────────────────────

    @Override
    public void onCoinClick(Coin coin) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_COIN_ID,     coin.getId());
        intent.putExtra(DetailActivity.EXTRA_COIN_NAME,   coin.getName());
        intent.putExtra(DetailActivity.EXTRA_COIN_SYMBOL, coin.getSymbol());
        intent.putExtra(DetailActivity.EXTRA_COIN_IMAGE,  coin.getImageUrl());
        intent.putExtra(DetailActivity.EXTRA_COIN_PRICE,  coin.getCurrentPrice());
        intent.putExtra(DetailActivity.EXTRA_COIN_CHANGE, coin.getPriceChangePercentage24h());
        intent.putExtra(DetailActivity.EXTRA_COIN_MKTCAP, coin.getMarketCap());
        intent.putExtra(DetailActivity.EXTRA_COIN_VOL,    coin.getTotalVolume());
        intent.putExtra(DetailActivity.EXTRA_COIN_HIGH,   coin.getHigh24h());
        intent.putExtra(DetailActivity.EXTRA_COIN_LOW,    coin.getLow24h());
        intent.putExtra(DetailActivity.EXTRA_COIN_SUPPLY, coin.getCirculatingSupply());
        intent.putExtra(DetailActivity.EXTRA_COIN_ATH,    coin.getAllTimeHigh());
        startActivity(intent);
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        layoutError.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvErrorMessage.setText(message);
    }
}
