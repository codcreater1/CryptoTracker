package com.example.cryptotracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptotracker.R;
import com.example.cryptotracker.controller.FavoriteManager;
import com.example.cryptotracker.model.Coin;

import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter binding {@link Coin} objects to item_coin.xml rows.
 * Shows a star icon for favourited coins.
 */
public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinViewHolder> {

    public interface OnCoinClickListener {
        void onCoinClick(Coin coin);
    }

    private List<Coin>          coinList;
    private final Context       context;
    private final OnCoinClickListener listener;
    private final FavoriteManager     favoriteManager;

    public CoinAdapter(Context context, List<Coin> coinList,
                       OnCoinClickListener listener, FavoriteManager favoriteManager) {
        this.context         = context;
        this.coinList        = coinList;
        this.listener        = listener;
        this.favoriteManager = favoriteManager;
    }

    public void updateData(List<Coin> newList) {
        this.coinList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_coin, parent, false);
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        holder.bind(coinList.get(position));
    }

    @Override
    public int getItemCount() { return coinList != null ? coinList.size() : 0; }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    class CoinViewHolder extends RecyclerView.ViewHolder {

        private final CardView  cardView;
        private final ImageView imgCoin;
        private final ImageView imgFavorite;
        private final TextView  tvRank, tvName, tvSymbol, tvPrice, tvChange;

        CoinViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView    = itemView.findViewById(R.id.cardView);
            imgCoin     = itemView.findViewById(R.id.imgCoin);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            tvRank      = itemView.findViewById(R.id.tvRank);
            tvName      = itemView.findViewById(R.id.tvName);
            tvSymbol    = itemView.findViewById(R.id.tvSymbol);
            tvPrice     = itemView.findViewById(R.id.tvPrice);
            tvChange    = itemView.findViewById(R.id.tvChange);
        }

        void bind(Coin coin) {
            tvRank.setText(String.valueOf(coin.getMarketCapRank()));
            tvName.setText(coin.getName());
            tvSymbol.setText(coin.getSymbol().toUpperCase(Locale.US));
            tvPrice.setText(formatPrice(coin.getCurrentPrice()));

            double change = coin.getPriceChangePercentage24h();
            tvChange.setText(String.format(Locale.US, "%.2f%%", change));
            tvChange.setTextColor(context.getColor(
                    change >= 0 ? R.color.green_profit : R.color.red_loss));

            Glide.with(context)
                    .load(coin.getImageUrl())
                    .placeholder(R.drawable.ic_coin_placeholder)
                    .error(R.drawable.ic_coin_placeholder)
                    .circleCrop()
                    .into(imgCoin);

            // ── Favourite star ────────────────────────────────────────────
            boolean isFav = favoriteManager.isFavorite(coin.getId());
            imgFavorite.setImageResource(isFav ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
            imgFavorite.setColorFilter(context.getColor(
                    isFav ? R.color.star_yellow : R.color.text_secondary));

            imgFavorite.setOnClickListener(v -> {
                boolean nowFav = favoriteManager.toggleFavorite(coin.getId());
                imgFavorite.setImageResource(
                        nowFav ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
                imgFavorite.setColorFilter(context.getColor(
                        nowFav ? R.color.star_yellow : R.color.text_secondary));
            });

            cardView.setOnClickListener(v -> listener.onCoinClick(coin));
        }

        private String formatPrice(double price) {
            if (price >= 1.0) return String.format(Locale.US, "$%,.2f", price);
            return String.format(Locale.US, "$%.6f", price);
        }
    }
}
