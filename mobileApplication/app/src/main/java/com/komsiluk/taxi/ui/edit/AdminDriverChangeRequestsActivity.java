package com.komsiluk.taxi.ui.edit;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;

public class AdminDriverChangeRequestsActivity extends BaseNavDrawerActivity
        implements DriverChangeRequestsAdapter.Listener {

    private RecyclerView rv;
    private View emptyPanel;
    private final ArrayList<DriverChangeRequest> mock = new ArrayList<>();

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_admin_driver_change_requests;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rv = findViewById(R.id.rvDriverChangeRequests);
        emptyPanel = findViewById(R.id.panelNoRequests);

        ((TextView)findViewById(R.id.tvAdminDcrTitle)).setText(R.string.admin_dcr_title);

        rv.setLayoutManager(new LinearLayoutManager(this));

        seedMock();
        rv.setAdapter(new DriverChangeRequestsAdapter(mock, this));

        updateEmpty();
    }

    private void updateEmpty() {
        boolean empty = mock.isEmpty();
        emptyPanel.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void seedMock() {

        // Request #1
        ArrayList<String> tags1 = new ArrayList<>();
        tags1.add("Identity");

        ArrayList<DriverChangeRequest.FieldChange> rows1 = new ArrayList<>();
        rows1.add(new DriverChangeRequest.FieldChange("First name", "Test", "Nikola"));
        rows1.add(new DriverChangeRequest.FieldChange("Last name", "DRIVER", "Savić"));

        mock.add(new DriverChangeRequest(
                1L,
                "driver@test.com",
                "Jan 12, 2026, 5:03:21 PM",
                tags1,
                rows1
        ));

        // Request #2
        ArrayList<String> tags2 = new ArrayList<>();
        tags2.add("Identity");
        tags2.add("Address");

        ArrayList<DriverChangeRequest.FieldChange> rows2 = new ArrayList<>();
        rows2.add(new DriverChangeRequest.FieldChange("First name", "Test", "Nikola"));
        rows2.add(new DriverChangeRequest.FieldChange("Last name", "DRIVER", "Savić"));
        rows2.add(new DriverChangeRequest.FieldChange("Address", "Test Address", "Andrije Marića 105"));
        rows2.add(new DriverChangeRequest.FieldChange("City", "Novi Sad", "Majur"));
        rows2.add(new DriverChangeRequest.FieldChange("Phone number", "+381600000000", "0621061386"));

        mock.add(new DriverChangeRequest(
                2L,
                "driver@test.com",
                "Jan 12, 2026, 5:03:34 PM",
                tags2,
                rows2
        ));
    }

    @Override
    public void onRequestClicked(DriverChangeRequest req) {
        AdminDialogs.showDriverChangeRequestDetails(
                this,
                req,
                () -> { /* Reject GUI-only */ },
                () -> { /* Approve GUI-only */ }
        );
    }
}
