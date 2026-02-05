package com.komsiluk.taxi.ui.edit;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminDriverChangeRequestsActivity extends BaseNavDrawerActivity implements DriverChangeRequestsAdapter.Listener {

    @Inject
    AuthManager authManager;

    @Override
    protected int getDrawerMenuResId() {
        if (authManager.getRole().equals(UserRole.DRIVER)) {
            return R.menu.menu_driver_drawer;
        } else if (authManager.getRole().equals(UserRole.ADMIN)) {
            return R.menu.menu_admin_drawer;
        }
        return R.menu.menu_app_drawer;
    }

    private RecyclerView rv;
    private View emptyPanel;

    private DriverChangeRequestsAdapter adapter;
    private AdminDriverChangeRequestsViewModel vm;

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
        adapter = new DriverChangeRequestsAdapter(new ArrayList<>(), this);
        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(AdminDriverChangeRequestsViewModel.class);

        vm.getItems().observe(this, list -> {
            adapter.setItems(list); // dodaćemo metodu u adapter
            updateEmpty(list == null || list.isEmpty());
        });

        vm.getToastEvent().observe(this, event -> {
            String msg = event.getContentIfNotHandled();
            if (msg == null) return;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        vm.getOpenDialogEvent().observe(this, event -> {
            DriverChangeRequest req = event.getContentIfNotHandled();
            if (req == null) return;

            AdminDialogs.showDriverChangeRequestDetails(
                    this,
                    req,
                    () -> vm.reject(req.getId()),
                    () -> vm.approve(req.getId())
            );
        });

        vm.fetchPending();
    }

    private void updateEmpty(boolean empty) {
        emptyPanel.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRequestClicked(DriverChangeRequest req) {
        vm.onItemClicked(req);
    }
}
