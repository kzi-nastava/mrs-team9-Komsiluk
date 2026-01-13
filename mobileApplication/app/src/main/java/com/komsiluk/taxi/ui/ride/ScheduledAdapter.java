package com.komsiluk.taxi.ui.ride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;

import java.util.ArrayList;

public class ScheduledAdapter extends RecyclerView.Adapter<ScheduledAdapter.VH> {

    public interface Listener {
        void onCardClicked(ScheduledRide ride);
    }

    private final ArrayList<ScheduledRide> items;
    private final Listener listener;

    public ScheduledAdapter(ArrayList<ScheduledRide> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public @NonNull VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_scheduled_ride, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ScheduledRide r = items.get(position);

        h.tvName.setText(r.getName());
        h.tvPickupValue.setText(r.getPickup());
        h.tvDestValue.setText(r.getDestination());
        h.tvScheduledValue.setText(r.getScheduledTime());

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCardClicked(r);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvName, tvPickupValue, tvDestValue, tvScheduledValue;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSchName);
            tvPickupValue = itemView.findViewById(R.id.tvSchPickupValue);
            tvDestValue = itemView.findViewById(R.id.tvSchDestinationValue);
            tvScheduledValue = itemView.findViewById(R.id.tvSchScheduledValue);
        }
    }
}
