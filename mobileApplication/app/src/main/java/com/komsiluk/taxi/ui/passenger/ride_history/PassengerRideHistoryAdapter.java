package com.komsiluk.taxi.ui.passenger.ride_history;


import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.ItemPassengerRideBinding;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideHistoryDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PassengerRideHistoryAdapter extends RecyclerView.Adapter<PassengerRideHistoryAdapter.ViewHolder> {

    public interface OnRideClickListener {
        void onRideClick(PassengerRideHistoryDTO ride);
    }

    private List<PassengerRideHistoryDTO> rides = new ArrayList<>();
    private final OnRideClickListener listener;

    public PassengerRideHistoryAdapter(OnRideClickListener listener) {
        this.listener = listener;
    }

    public void setRides(List<PassengerRideHistoryDTO> rides) {
        this.rides = rides != null ? rides : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPassengerRideBinding binding = ItemPassengerRideBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(rides.get(position), listener, position);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPassengerRideBinding binding;

        ViewHolder(ItemPassengerRideBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PassengerRideHistoryDTO ride, OnRideClickListener listener, int position) {
            boolean isEven = position % 2 == 0;


            int backgroundRes = isEven ? R.drawable.bg_ride_card_small : R.drawable.bg_ride_card;
            binding.getRoot().setBackgroundResource(backgroundRes);

            int textColor = ContextCompat.getColor(itemView.getContext(),
                    isEven ? R.color.text : R.color.secondary);

            int secondaryTextColor = ContextCompat.getColor(itemView.getContext(),
                    isEven ? R.color.text : R.color.white);

            int accentColor = ContextCompat.getColor(itemView.getContext(), R.color.secondary);
            int primaryColor = ContextCompat.getColor(itemView.getContext(), R.color.primary);

            binding.tvDateTime.setTextColor(secondaryTextColor);
            binding.tvRoute.setTextColor(textColor);
            binding.divider.setBackgroundColor(secondaryTextColor);

            if (isEven) {
                binding.btnDetails.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
                binding.btnDetails.setTextColor(accentColor);
            } else {
                binding.btnDetails.setBackgroundTintList(ColorStateList.valueOf(accentColor));
                binding.btnDetails.setTextColor(primaryColor);
            }

            StringBuilder routeBuilder = new StringBuilder();

            routeBuilder.append("Pickup: ").append(ride.getStartAddress()).append("\n\n");

            String routeData = ride.getRoute();
            if (routeData != null && !routeData.isEmpty()) {
                String[] stops = routeData.split(",");
                for (int i = 0; i < stops.length; i++) {
                    routeBuilder.append("Station ")
                            .append(i + 1)
                            .append(": ")
                            .append(stops[i].trim())
                            .append("\n");
                }
            }

            routeBuilder.append("\nDestination: ").append(ride.getEndAddress());

            binding.tvRoute.setText(routeBuilder.toString());


            String dateTime = formatDateTime(ride.getStartTime(), ride.getEndTime());
            binding.tvDateTime.setText(dateTime);

            binding.btnDetails.setOnClickListener(v -> {
                if (listener != null) listener.onRideClick(ride);
            });

        }

        private String formatDateTime(String startTime, String endTime) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                Date startDate = isoFormat.parse(startTime);
                Date endDate = endTime != null ? isoFormat.parse(endTime) : null;

                String formattedStartDate = dateFormat.format(startDate);
                String formattedStartTime = timeFormat.format(startDate);

                if (endDate == null) {
                    return formattedStartDate + "  " + formattedStartTime + " - N/A";
                }

                String formattedEndTime = timeFormat.format(endDate);

                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startDate);
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);

                boolean sameDay = startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR) &&
                        startCal.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR);

                if (sameDay) {
                    return formattedStartDate + "  " + formattedStartTime + " - " + formattedEndTime;
                } else {
                    String formattedEndDate = dateFormat.format(endDate);
                    return formattedStartDate + " " + formattedStartTime + " - " + formattedEndDate + " " + formattedEndTime;
                }

            } catch (ParseException e) {
                return startTime + " - " + (endTime != null ? endTime : "N/A");
            }
        }
    }
}
