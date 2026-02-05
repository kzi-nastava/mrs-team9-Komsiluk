package com.komsiluk.taxi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.komsiluk.taxi.data.remote.ride.RideResponse;
import com.komsiluk.taxi.data.remote.ride.RideService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class DriverActivity extends BaseNavDrawerActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_driver;
    }

    @Override
    protected int getDrawerMenuResId() {
        return R.menu.menu_driver_drawer;
    }

    private MapView map;

    // Bottom sheet
    private BottomSheetBehavior<View> sheetBehavior;
    private View currentRideSheet;

    private TextView tvPickupValue;
    private TextView tvDestinationValue;

    private View rowStartCancel;
    private View rowInRide;

    private MaterialButton btnCancel;
    private MaterialButton btnStart;

    private MaterialButton btnFinish;
    private MaterialButton btnStop;
    private MaterialButton btnReport;
    private MaterialButton btnPanic;

    private RideResponse currentRide;
    private boolean rideStartedUi = false;

    private boolean startInFlight= false;

    @Inject
    SessionManager sessionManager;

    @Inject
    RideService rideService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);
        map.getController().setZoom(13.5);
        map.getController().setCenter(new GeoPoint(45.2671, 19.8335));

        BoundingBox nsBox = new BoundingBox(
                45.35, 19.95,
                45.20, 19.75
        );

        map.setScrollableAreaLimitDouble(nsBox);
        map.setMinZoomLevel(12.0);
        map.setMaxZoomLevel(20.0);

        currentRideSheet = findViewById(R.id.currentRideSheet);
        sheetBehavior = BottomSheetBehavior.from(currentRideSheet);

        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        tvPickupValue = findViewById(R.id.tvPickupValue);
        tvDestinationValue = findViewById(R.id.tvDestinationValue);

        rowStartCancel = findViewById(R.id.rowStartCancel);
        rowInRide = findViewById(R.id.rowInRide);

        btnCancel = findViewById(R.id.btnCancelRide);
        btnStart = findViewById(R.id.btnStartRide);

        btnFinish = findViewById(R.id.btnFinishRide);
        btnStop = findViewById(R.id.btnStopRide);
        btnReport = findViewById(R.id.btnReportRide);
        btnPanic = findViewById(R.id.btnPanicRide);

        btnCancel.setOnClickListener(v -> {
            Toast.makeText(this, "Cancel not implemented yet.", Toast.LENGTH_SHORT).show();
        });

        btnStart.setOnClickListener(v -> showConfirmStartDialog());

        btnFinish.setOnClickListener(v -> Toast.makeText(this, "Finish not implemented yet.", Toast.LENGTH_SHORT).show());
        btnStop.setOnClickListener(v -> Toast.makeText(this, "Stop not implemented yet.", Toast.LENGTH_SHORT).show());
        btnReport.setOnClickListener(v -> Toast.makeText(this, "Report not implemented yet.", Toast.LENGTH_SHORT).show());
        btnPanic.setOnClickListener(v -> Toast.makeText(this, "Panic not implemented yet.", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchCurrentRideAndUpdateUi();
    }

    private void fetchCurrentRideAndUpdateUi() {
        Long driverId = sessionManager != null ? sessionManager.getUserId() : null;
        if (driverId == null) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }

        if (rideService == null) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }

        rideService.getDriverCurrentRide(driverId).enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> resp) {

                if (resp.code() == 204) {
                    currentRide = null;
                    rideStartedUi = false;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    return;
                }

                if (!resp.isSuccessful() || resp.body() == null) {
                    currentRide = null;
                    rideStartedUi = false;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    return;
                }

                currentRide = resp.body();

                String pickup = safe(currentRide.getStartAddress());
                String dest = safe(currentRide.getEndAddress());

                tvPickupValue.setText(pickup);
                tvDestinationValue.setText(dest);

                rideStartedUi = isRideActiveStatus(currentRide.getStatus());
                applyRideButtonsUi();

                applyRideButtonsUi();

                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                currentRide = null;
                rideStartedUi = false;
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private boolean isRideActiveStatus(String statusRaw) {
        if (statusRaw == null) return false;

        String s = statusRaw.trim().toUpperCase();

        return s.equals("ACTIVE");
    }

    private void applyRideButtonsUi() {
        if (rideStartedUi) {
            rowStartCancel.setVisibility(View.GONE);
            rowInRide.setVisibility(View.VISIBLE);
        } else {
            rowStartCancel.setVisibility(View.VISIBLE);
            rowInRide.setVisibility(View.GONE);
        }
    }

    private void showConfirmStartDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_start_ride, null, false);

        ImageButton btnClose = v.findViewById(R.id.btnStartClose);
        MaterialButton btnCancel = v.findViewById(R.id.btnStartCancel);
        MaterialButton btnConfirm = v.findViewById(R.id.btnStartConfirm);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(v)
                .setCancelable(true)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        View.OnClickListener dismiss = x -> dialog.dismiss();
        btnClose.setOnClickListener(dismiss);
        btnCancel.setOnClickListener(dismiss);

        btnConfirm.setOnClickListener(x -> {
            if (startInFlight) return;

            Long rideId = getCurrentRideId();
            if (rideId == null) {
                Toast.makeText(this, "Ride ID missing.", Toast.LENGTH_LONG).show();
                return;
            }

            startInFlight = true;

            btnConfirm.setEnabled(false);
            btnCancel.setEnabled(false);
            btnClose.setEnabled(false);

            startRideOnBackend(
                    rideId,
                    () -> {
                        startInFlight = false;

                        rideStartedUi = true;
                        applyRideButtonsUi();

                        dialog.dismiss();
                    },
                    (errMsg) -> {
                        startInFlight = false;

                        btnConfirm.setEnabled(true);
                        btnCancel.setEnabled(true);
                        btnClose.setEnabled(true);

                        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                    }
            );
        });
    }

    private void startRideOnBackend(Long rideId, Runnable onSuccess, java.util.function.Consumer<String> onError) {
        if (rideService == null) {
            onError.accept("Ride service is not available.");
            return;
        }

        rideService.startRide(rideId).enqueue(new retrofit2.Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    String msg = "Start failed (" + resp.code() + ")";
                    onError.accept(msg);
                    return;
                }

                currentRide = resp.body();
                onSuccess.run();
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                onError.accept("Network error: " + (t.getMessage() == null ? "unknown" : t.getMessage()));
            }
        });
    }

    private Long getCurrentRideId() {
        if (currentRide == null) return null;
        try { return currentRide.getId(); } catch (Throwable ignored) { return null; }
    }

    private String safe(String s) {
        if (s == null) return "";
        String t = s.trim();
        return t.isEmpty() ? "" : t;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    protected void onPause() {
        if (map != null) map.onPause();
        super.onPause();
    }
}
