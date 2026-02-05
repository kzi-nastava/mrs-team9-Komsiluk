package com.komsiluk.taxi.ui.ride;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.ride.RideResponse;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class ScheduledActivity extends BaseNavDrawerActivity implements ScheduledAdapter.Listener {

    private RecyclerView rv;
    private TextView tvEmpty;

    private ScheduledAdapter adapter;
    private final ArrayList<ScheduledRide> items = new ArrayList<>();
    private ScheduledViewModel vm;

    @Inject
    SessionManager sessionManager;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_scheduled;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rv = findViewById(R.id.rvScheduled);
        tvEmpty = findViewById(R.id.tvScheduledEmpty);

        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ScheduledAdapter(items, this);
        rv.setAdapter(adapter);

        updateEmptyState();

        vm = new androidx.lifecycle.ViewModelProvider(this).get(ScheduledViewModel.class);

        vm.getState().observe(this, st -> {
            if (st == null) return;

            if (st.loading) {
                updateEmptyState();
                return;
            }

            if (st.error != null) {
                android.widget.Toast.makeText(this, st.error, android.widget.Toast.LENGTH_LONG).show();
                items.clear();
                adapter.notifyDataSetChanged();
                updateEmptyState();
                return;
            }

            items.clear();
            if (st.data != null) {
                for (RideResponse r : st.data) {
                    items.add(mapToUi(r));
                }
            }
            adapter.notifyDataSetChanged();
            updateEmptyState();
        });

        Long userId = sessionManager != null ? sessionManager.getUserId() : null;
        if (userId == null) {
            android.widget.Toast.makeText(this, "Not logged in.", android.widget.Toast.LENGTH_LONG).show();
            return;
        }

        vm.loadScheduled(userId);
    }
    private void updateEmptyState() {
        boolean empty = items.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private ScheduledRide mapToUi(RideResponse r) {
        ArrayList<String> stations = new java.util.ArrayList<>();
        if (r.getStops() != null) stations.addAll(r.getStops());

        ArrayList<String> users = new java.util.ArrayList<>();
        if (r.getPassengerEmails() != null) users.addAll(r.getPassengerEmails());

        String name = "Ride #" + (r.getId() != null ? r.getId() : "");

        String scheduled = "";

        String raw = r.getScheduledAt();
        if (raw == null) raw = "";
        raw = raw.trim();

        try {
            LocalDateTime dt;

            if (raw.contains("T")) {
                dt = LocalDateTime.parse(raw); // ISO_LOCAL_DATE_TIME
            } else {
                DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                dt = LocalDateTime.parse(raw, inFmt);
            }

            DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
            scheduled = dt.format(outFmt);

        } catch (Exception e) {
            try {
                String hhmm = raw.replace('T', ' ');
                int idx = hhmm.indexOf(' ');
                if (idx != -1 && hhmm.length() >= idx + 6) {
                    scheduled = hhmm.substring(idx + 1, idx + 6); // "09:00"
                } else {
                    scheduled = raw;
                }
            } catch (Exception ignored) {
                scheduled = raw;
            }
        }

        String km = String.format(Locale.getDefault(), "%.1f", r.getDistanceKm());

        return new ScheduledRide(
                name,
                r.getStartAddress(),
                r.getEndAddress(),
                stations,
                users,
                r.getVehicleType(),
                r.isPetFriendly(),
                r.isBabyFriendly(),
                km,
                r.getEstimatedDurationMin().toString(),
                scheduled
        );
    }

    @Override
    public void onCardClicked(ScheduledRide ride) {
        ScheduledDialogs.showScheduledDetails(this, ride, () -> {
        });
    }
}
