package com.komsiluk.taxi.driver.history;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;
import java.util.List;

public class DriverHistoryActivity extends BaseNavDrawerActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_driver_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ NE diramo navbar ovde (nema navTitle/btnNavRight u novom navbaru)
        // Drawer/hamburger radi iz BaseNavDrawerActivity preko btnNavMenu.

        RecyclerView rv = findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<DriverRide> dummy = createDummy();
        DriverHistoryAdapter adapter = new DriverHistoryAdapter(dummy, ride -> {
            RideDetailsDialogFragment.newInstance(ride)
                    .show(getSupportFragmentManager(), "ride_details");
        });
        rv.setAdapter(adapter);

        findViewById(R.id.btnFilter).setOnClickListener(v ->
                Toast.makeText(this, "Filter clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private List<DriverRide> createDummy() {
        List<DriverRide> list = new ArrayList<>();
        list.add(new DriverRide("13.12.2025", "12:00", "14:14",
                "Brace Ribnikar 45", "Ilariona Ruvarca 27",
                "completed", 3, 100, "2h 14min", "200$"));
        list.add(new DriverRide("13.12.2025", "10:05", "10:55",
                "Bulevar cara Lazara", "FTN",
                "completed", 1, 12, "50min", "15$"));
        list.add(new DriverRide("12.12.2025", "19:10", "19:40",
                "Detelinara", "Centar",
                "completed", 2, 8, "30min", "10$"));
        return list;
    }
}
