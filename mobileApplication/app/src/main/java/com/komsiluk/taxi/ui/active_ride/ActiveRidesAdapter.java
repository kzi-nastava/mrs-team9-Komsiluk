package com.komsiluk.taxi.ui.active_ride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.ride.RideResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveRidesAdapter extends RecyclerView.Adapter<ActiveRidesAdapter.ViewHolder> {

    private List<RideResponse> rides; // Lista koja se menja (filtrirana)
    private final List<RideResponse> fullList; // Originalna kopija svih podataka
    private final OnRideClickListener listener;
    private final java.util.HashMap<Long, String> driverNames;

    public interface OnRideClickListener {
        void onRideClick(RideResponse ride);
    }

    public ActiveRidesAdapter(List<RideResponse> rides, java.util.HashMap<Long, String> driverNames, OnRideClickListener listener) {
        this.rides = rides;
        this.fullList = new ArrayList<>(rides); // Inicijalizujemo kopiju
        this.driverNames = driverNames;
        this.listener = listener;
    }

    /**
     * Metoda za filtriranje vožnji po imenu ili prezimenu vozača
     */
    public void filter(String text, java.util.HashMap<Long, String> driverMap) {
        List<RideResponse> filteredList = new ArrayList<>();
        String query = text.toLowerCase().trim();

        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (RideResponse ride : fullList) {
                String name = driverMap.get(ride.getDriverId());
                // Proveravamo da li drajver postoji u mapi i da li njegovo ime sadrži uneti tekst
                if (name != null && name.toLowerCase().contains(query)) {
                    filteredList.add(ride);
                }
            }
        }
        this.rides = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_ride, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideResponse ride = rides.get(position);

        Long dId = ride.getDriverId();
        String name = driverNames.getOrDefault(dId, "Unknown");
        holder.tvDriver.setText("Driver: " + name);

        holder.tvPickup.setText(ride.getStartAddress());
        holder.tvDestination.setText(ride.getEndAddress());
        holder.tvPrice.setText(ride.getPrice() + " $");

        holder.itemView.setOnClickListener(v -> listener.onRideClick(ride));
    }

    @Override
    public int getItemCount() { return rides == null ? 0 : rides.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDriver, tvPickup, tvDestination, tvPrice;
        ViewHolder(View v) {
            super(v);
            tvDriver = v.findViewById(R.id.tvDriverName);
            tvPickup = v.findViewById(R.id.tvPickupValue);
            tvDestination = v.findViewById(R.id.tvDestinationValue);
            tvPrice = v.findViewById(R.id.tvRidePrice);
        }
    }
}