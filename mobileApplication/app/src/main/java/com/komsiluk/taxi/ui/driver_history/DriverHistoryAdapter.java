package com.komsiluk.taxi.ui.driver_history;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.ItemDriverRideBinding;
import com.komsiluk.taxi.databinding.ItemDriverRideSmallBinding;

import java.util.List;

public class DriverHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BIG = 0;
    private static final int TYPE_SMALL = 1;

    public interface OnRideClickListener {
        void onDetailsClick(DriverRide ride);
    }

    private final List<DriverRide> rides;
    private final OnRideClickListener listener;

    public DriverHistoryAdapter(List<DriverRide> rides, OnRideClickListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 2 == 0) ? TYPE_BIG : TYPE_SMALL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_BIG) {
            ItemDriverRideBinding b = ItemDriverRideBinding.inflate(inflater, parent, false);
            return new BigVH(b);
        } else {
            ItemDriverRideSmallBinding b = ItemDriverRideSmallBinding.inflate(inflater, parent, false);
            return new SmallVH(b);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DriverRide r = rides.get(position);

        if (holder instanceof BigVH) {
            ((BigVH) holder).bind(r, listener);
        } else if (holder instanceof SmallVH) {
            ((SmallVH) holder).bind(r, listener);
        }
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    // ---------- helper: label/value in two colors ----------
    private static SpannableString labelValue(View view, String label, String value,
                                              int labelColorRes, int valueColorRes) {
        String full = label + value;
        SpannableString ss = new SpannableString(full);

        int labelColor = ContextCompat.getColor(view.getContext(), labelColorRes);
        int valueColor = ContextCompat.getColor(view.getContext(), valueColorRes);

        ss.setSpan(new ForegroundColorSpan(labelColor), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(valueColor), label.length(), full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private static SpannableString twoLineLabelValue(
            View view,
            String label1, String value1,
            String label2, String value2,
            int labelColorRes, int valueColorRes
    ) {
        String line1 = label1 + value1;
        String line2 = label2 + value2;
        String full = line1 + "\n" + line2;

        SpannableString ss = new SpannableString(full);

        int labelColor = ContextCompat.getColor(view.getContext(), labelColorRes);
        int valueColor = ContextCompat.getColor(view.getContext(), valueColorRes);

        // Line 1
        ss.setSpan(new ForegroundColorSpan(labelColor), 0, label1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(valueColor), label1.length(), line1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Line 2
        int offset = line1.length() + 1; // + '\n'
        ss.setSpan(new ForegroundColorSpan(labelColor), offset, offset + label2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(valueColor), offset + label2.length(), offset + line2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    // ---------------- ViewHolders ----------------

    static class BigVH extends RecyclerView.ViewHolder {
        final ItemDriverRideBinding b;

        BigVH(ItemDriverRideBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(DriverRide r, OnRideClickListener listener) {
            // Datum ostaje žut (boja iz XML-a)
            b.tvDate.setText(r.date);

            // Start/End jedno ispod drugog: label belo, value žuto
            b.tvTimes.setText(twoLineLabelValue(
                    b.getRoot(),
                    "Start: ", r.startTime,
                    "End: ", r.endTime,
                    android.R.color.white, R.color.secondary
            ));

            // Ostalo: label belo, value žuto
            b.tvPickup.setText(labelValue(b.getRoot(), "Pickup: ", r.pickup, android.R.color.white, R.color.secondary));
            b.tvDestination.setText(labelValue(b.getRoot(), "Destination: ", r.destination, android.R.color.white, R.color.secondary));
            b.tvStatus.setText(labelValue(b.getRoot(), "Status: ", r.status, android.R.color.white, R.color.secondary));
            b.tvPassengers.setText(labelValue(b.getRoot(), "Passengers: ", String.valueOf(r.passengers), android.R.color.white, R.color.secondary));
            b.tvKm.setText(labelValue(b.getRoot(), "Kilometers: ", String.valueOf(r.kilometers), android.R.color.white, R.color.secondary));
            b.tvDuration.setText(labelValue(b.getRoot(), "Time: ", r.duration, android.R.color.white, R.color.secondary));
            b.tvPrice.setText(labelValue(b.getRoot(), "Price: ", r.price, android.R.color.white, R.color.secondary));

            b.btnDetails.setOnClickListener(v -> {
                if (listener != null) listener.onDetailsClick(r);
            });
        }
    }

    static class SmallVH extends RecyclerView.ViewHolder {
        final ItemDriverRideSmallBinding b;

        SmallVH(ItemDriverRideSmallBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(DriverRide r, OnRideClickListener listener) {
            // SMALL (žuta): label crna, value tamno-siva (ako nemaš, može primary/crna)
            int labelColor = android.R.color.white;
            int valueColor = R.color.black;

            b.tvDate.setText(r.date);

            b.tvTimes.setText(twoLineLabelValue(
                    b.getRoot(),
                    "Start: ", r.startTime,
                    "End: ", r.endTime,
                    android.R.color.white, R.color.black
            ));

            b.tvPickup.setText(labelValue(b.getRoot(), "Pickup: ", r.pickup, labelColor, valueColor));
            b.tvDestination.setText(labelValue(b.getRoot(), "Destination: ", r.destination, labelColor, valueColor));
            b.tvStatus.setText(labelValue(b.getRoot(), "Status: ", r.status, labelColor, valueColor));
            b.tvPassengers.setText(labelValue(b.getRoot(), "Passengers: ", String.valueOf(r.passengers), labelColor, valueColor));
            b.tvKm.setText(labelValue(b.getRoot(), "Kilometers: ", String.valueOf(r.kilometers), labelColor, valueColor));
            b.tvDuration.setText(labelValue(b.getRoot(), "Time: ", r.duration, labelColor, valueColor));
            b.tvPrice.setText(labelValue(b.getRoot(), "Price: ", r.price, labelColor, valueColor));

            b.btnDetails.setOnClickListener(v -> {
                if (listener != null) listener.onDetailsClick(r);
            });
        }
    }
}
