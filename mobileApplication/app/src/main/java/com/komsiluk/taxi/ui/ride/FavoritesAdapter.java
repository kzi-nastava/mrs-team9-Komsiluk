package com.komsiluk.taxi.ui.ride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.VH> {

    public interface Listener {
        void onCardClicked(FavoriteRide ride);
    }

    private final ArrayList<FavoriteRide> items;
    private final Listener listener;

    public FavoritesAdapter(ArrayList<FavoriteRide> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public @NonNull VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_favorite_ride, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        FavoriteRide r = items.get(position);

        if (h.tvName != null) h.tvName.setText(r.getName());
        if (h.tvPickupValue != null) h.tvPickupValue.setText(r.getPickup());
        if (h.tvDestValue != null) h.tvDestValue.setText(r.getDestination());

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCardClicked(r);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvPickupValue;
        TextView tvDestValue;

        VH(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvFavName);
            tvPickupValue = itemView.findViewById(R.id.tvFavPickupValue);
            tvDestValue = itemView.findViewById(R.id.tvFavDestinationValue);
        }
    }
}
