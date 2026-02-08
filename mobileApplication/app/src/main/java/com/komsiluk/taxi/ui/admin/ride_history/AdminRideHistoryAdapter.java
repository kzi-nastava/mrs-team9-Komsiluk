package com.komsiluk.taxi.ui.admin.ride_history;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.admin_ride_history.AdminRideHistoryDTO;
import com.komsiluk.taxi.databinding.ItemAdminRideBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminRideHistoryAdapter extends RecyclerView.Adapter<AdminRideHistoryAdapter.ViewHolder> {

    public interface OnRideClickListener {
        void onRideClick(AdminRideHistoryDTO ride);
    }

    private List<AdminRideHistoryDTO> rides = new ArrayList<>();
    private final OnRideClickListener listener;

    public AdminRideHistoryAdapter(OnRideClickListener listener) {
        this.listener = listener;
    }

    public void setRides(List<AdminRideHistoryDTO> rides) {
        this.rides = rides != null ? rides : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminRideBinding binding = ItemAdminRideBinding.inflate(
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
        private final ItemAdminRideBinding binding;

        ViewHolder(ItemAdminRideBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AdminRideHistoryDTO ride, OnRideClickListener listener, int position) {
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
            binding.tvPrice.setTextColor(textColor);
            binding.divider.setBackgroundColor(secondaryTextColor);

            if (isEven) {
                binding.btnDetails.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
                binding.btnDetails.setTextColor(accentColor);
            } else {
                binding.btnDetails.setBackgroundTintList(ColorStateList.valueOf(accentColor));
                binding.btnDetails.setTextColor(primaryColor);
            }

            StringBuilder routeBuilder = new StringBuilder();
            routeBuilder.append("Pickup: ").append(ride.getStartAddress()).append("\n");

            String routeData = ride.getRoute();
            if (routeData != null && !routeData.isEmpty()) {
                routeBuilder.append("\n");
                String[] stops = routeData.split(",");
                for (int i = 0; i < stops.length; i++) {
                    routeBuilder.append("Station ")
                            .append(i + 1)
                            .append(": ")
                            .append(stops[i].trim())
                            .append("\n");
                }
                routeBuilder.append("\n");
            }

            routeBuilder.append("Destination: ").append(ride.getEndAddress());
            binding.tvRoute.setText(routeBuilder.toString());

            String dateTime = formatDateTime(ride.getStartTime(), ride.getEndTime());
            if (dateTime.isEmpty()) {
                binding.tvDateTime.setVisibility(View.GONE);
            } else {
                binding.tvDateTime.setVisibility(View.VISIBLE);
                binding.tvDateTime.setText(dateTime);
            }

            String priceText = ride.getPrice() != null ? ride.getPrice().toString() + " $" : "N/A";
            binding.tvPrice.setText("Price: " + priceText);

            binding.tvCanceled.setVisibility(View.VISIBLE);
            if (ride.isCanceled()) {
                String canceledBy = ride.getCancellationSource() != null
                        ? ride.getCancellationSource().name() : "Unknown";
                binding.tvCanceled.setText("Canceled by: " + canceledBy);
            } else {
                binding.tvCanceled.setText("Canceled: No");
            }
            binding.tvCanceled.setTextColor(textColor);

            binding.tvPanic.setVisibility(View.VISIBLE);
            if (ride.isPanicTriggered()) {
                binding.tvPanic.setText("⚠ PANIC TRIGGERED");
                binding.tvPanic.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.error));
            } else {
                binding.tvPanic.setText("Panic: No");
                binding.tvPanic.setTextColor(textColor);
            }

            binding.btnDetails.setOnClickListener(v -> {
                if (listener != null) listener.onRideClick(ride);
            });
        }

        private String formatDateTime(String startTime, String endTime) {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            try {
                Date startDate = (startTime != null && !startTime.isEmpty()) ? isoFormat.parse(startTime) : null;

                Date endDate = (endTime != null && !endTime.isEmpty()) ? isoFormat.parse(endTime) : null;

                if (startDate == null) {
                    return "";
                }

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

            } catch (ParseException | NullPointerException e) {
                return startTime + " - " + (endTime != null ? endTime : "N/A");
            }
        }
    }
}
