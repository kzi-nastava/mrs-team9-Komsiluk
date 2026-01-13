package com.komsiluk.taxi.ui.ride;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;

public class ScheduledActivity extends BaseNavDrawerActivity implements ScheduledAdapter.Listener {

    private RecyclerView rv;
    private TextView tvEmpty;

    private final ArrayList<ScheduledRide> mock = new ArrayList<>();

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
        seedMock();

        ScheduledAdapter adapter = new ScheduledAdapter(mock, this);
        rv.setAdapter(adapter);

        updateEmpty();
    }

    private void seedMock() {
        ArrayList<String> stations = new ArrayList<>();
        stations.add("Station address 1");
        stations.add("Station address 2");

        ArrayList<String> users = new ArrayList<>();
        users.add("user1@gmail.com");

        mock.add(new ScheduledRide(
                "Scheduled ride 1",
                "Start location 123",
                "Finish location 123",
                stations,
                users,
                "Luxury",
                true,
                true,
                "17:30"
        ));
    }

    private void updateEmpty() {
        boolean empty = mock.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCardClicked(ScheduledRide ride) {
        ScheduledDialogs.showScheduledDetails(this, ride, () -> {
        });
    }
}
