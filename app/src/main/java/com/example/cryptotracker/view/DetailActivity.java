package com.example.cryptotracker.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.cryptotracker.R;
import com.example.cryptotracker.controller.CoinController;
import com.example.cryptotracker.controller.FavoriteManager;

import java.util.Locale;

/**
 * Detail screen: shows full stats, 7-day sparkline chart, and favourite toggle.
 */
public class DetailActivity extends AppCompatActivity {

    // Intent extra keys
    public static final String EXTRA_COIN_ID     = "coin_id";
    public static final String EXTRA_COIN_NAME   = "coin_name";
    public static final String EXTRA_COIN_SYMBOL = "coin_symbol";
    public static final String EXTRA_COIN_IMAGE  = "coin_image";
    public static final String EXTRA_COIN_PRICE  = "coin_price";
    public static final String EXTRA_COIN_CHANGE = "coin_change";
    public static final String EXTRA_COIN_MKTCAP = "coin_market_cap";
    public static final String EXTRA_COIN_VOL    = "coin_volume";
    public static final String EXTRA_COIN_HIGH   = "coin_high";
    public static final String EXTRA_COIN_LOW    = "coin_low";
    public static final String EXTRA_COIN_SUPPLY = "coin_supply";
    public static final String EXTRA_COIN_ATH    = "coin_ath";

    // UI
    private ImageView  imgCoinDetail, imgFavDetail;
    private TextView   tvDetailName, tvDetailSymbol, tvDetailPrice, tvDetailChange;
    private TextView   tvDetailMarketCap, tvDetailVolume, tvDetailHigh;
    private TextView   tvDetailLow, tvDetailSupply, tvDetailAth;
    private SparkLine  sparkLine;
    private ProgressBar chartProgress;
    private TextView   tvChartLabel;

    // State
    private String          coinId;
    private CoinController  controller;
    private FavoriteManager favoriteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        controller      = new CoinController();
        favoriteManager = new FavoriteManager(this);

        setupToolbar();
        bindViews();
        populateData();
        loadChart();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void bindViews() {
        imgCoinDetail     = findViewById(R.id.imgCoinDetail);
        imgFavDetail      = findViewById(R.id.imgFavDetail);
        tvDetailName      = findViewById(R.id.tvDetailName);
        tvDetailSymbol    = findViewById(R.id.tvDetailSymbol);
        tvDetailPrice     = findViewById(R.id.tvDetailPrice);
        tvDetailChange    = findViewById(R.id.tvDetailChange);
        tvDetailMarketCap = findViewById(R.id.tvDetailMarketCap);
        tvDetailVolume    = findViewById(R.id.tvDetailVolume);
        tvDetailHigh      = findViewById(R.id.tvDetailHigh);
        tvDetailLow       = findViewById(R.id.tvDetailLow);
        tvDetailSupply    = findViewById(R.id.tvDetailSupply);
        tvDetailAth       = findViewById(R.id.tvDetailAth);
        sparkLine         = findViewById(R.id.sparkLine);
        chartProgress     = findViewById(R.id.chartProgress);
        tvChartLabel      = findViewById(R.id.tvChartLabel);
    }

    private void populateData() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) { finish(); return; }

        coinId          = extras.getString(EXTRA_COIN_ID, "");
        String name     = extras.getString(EXTRA_COIN_NAME,   "");
        String symbol   = extras.getString(EXTRA_COIN_SYMBOL, "");
        String imageUrl = extras.getString(EXTRA_COIN_IMAGE,  "");
        double price    = extras.getDouble(EXTRA_COIN_PRICE,   0);
        double change   = extras.getDouble(EXTRA_COIN_CHANGE,  0);
        long   mktCap   = extras.getLong  (EXTRA_COIN_MKTCAP,  0);
        long   volume   = extras.getLong  (EXTRA_COIN_VOL,     0);
        double high     = extras.getDouble(EXTRA_COIN_HIGH,    0);
        double low      = extras.getDouble(EXTRA_COIN_LOW,     0);
        double supply   = extras.getDouble(EXTRA_COIN_SUPPLY,  0);
        double ath      = extras.getDouble(EXTRA_COIN_ATH,     0);

        Glide.with(this).load(imageUrl)
                .placeholder(R.drawable.ic_coin_placeholder)
                .circleCrop().into(imgCoinDetail);

        tvDetailName.setText(name);
        tvDetailSymbol.setText(symbol.toUpperCase(Locale.US));
        tvDetailPrice.setText(formatPrice(price));
        tvDetailChange.setText(String.format(Locale.US, "%.2f%%", change));
        tvDetailChange.setTextColor(getColor(change >= 0 ? R.color.green_profit : R.color.red_loss));
        tvDetailMarketCap.setText(formatLargeNumber(mktCap));
        tvDetailVolume.setText(formatLargeNumber(volume));
        tvDetailHigh.setText(formatPrice(high));
        tvDetailLow.setText(formatPrice(low));
        tvDetailSupply.setText(formatSupply(supply, symbol));
        tvDetailAth.setText(formatPrice(ath));

        // Favourite button
        updateFavButton();
        imgFavDetail.setOnClickListener(v -> {
            favoriteManager.toggleFavorite(coinId);
            updateFavButton();
        });
    }

    private void updateFavButton() {
        boolean isFav = favoriteManager.isFavorite(coinId);
        imgFavDetail.setImageResource(isFav ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        imgFavDetail.setColorFilter(getColor(isFav ? R.color.star_yellow : R.color.text_secondary));
    }

    private void loadChart() {
        chartProgress.setVisibility(View.VISIBLE);
        sparkLine.setVisibility(View.GONE);

        controller.fetchMarketChart(coinId, new CoinController.ChartCallback() {
            @Override
            public void onSuccess(java.util.List<Double> prices) {
                runOnUiThread(() -> {
                    chartProgress.setVisibility(View.GONE);
                    sparkLine.setVisibility(View.VISIBLE);
                    sparkLine.setPrices(prices);
                    tvChartLabel.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    chartProgress.setVisibility(View.GONE);
                    tvChartLabel.setText("Chart unavailable");
                    tvChartLabel.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    // ── Formatting ────────────────────────────────────────────────────────────

    private String formatPrice(double price) {
        if (price >= 1.0) return String.format(Locale.US, "$%,.2f", price);
        return String.format(Locale.US, "$%.6f", price);
    }

    private String formatLargeNumber(long number) {
        if (number >= 1_000_000_000L)
            return String.format(Locale.US, "$%.2fB", number / 1_000_000_000.0);
        if (number >= 1_000_000L)
            return String.format(Locale.US, "$%.2fM", number / 1_000_000.0);
        return String.format(Locale.US, "$%,d", number);
    }

    private String formatSupply(double supply, String symbol) {
        return String.format(Locale.US, "%,.0f %s", supply, symbol.toUpperCase(Locale.US));
    }
}
