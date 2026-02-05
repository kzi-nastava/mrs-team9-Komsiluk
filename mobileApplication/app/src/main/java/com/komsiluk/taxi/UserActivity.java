package com.komsiluk.taxi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideDetailsDTO;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import com.komsiluk.taxi.ui.ride.FavoriteRide;
import com.komsiluk.taxi.ui.ride.FavoritesActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UserActivity extends BaseNavDrawerActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }

    private BottomSheetBehavior<View> sheetBehavior;

    private ChipGroup chipStations, chipUsers;
    private MaterialButton btnAddStation, btnAddUser, btnBookRide;
    private AutoCompleteTextView actCarType, actTime;
    private RadioButton rbNow, rbScheduled;
    private ImageButton btnFavorite;
    private View headerRow;
    private View sheetHandle;

    private boolean isFavorite = false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String rideJson = intent.getStringExtra("ORDER_AGAIN_DTO");
        if (rideJson != null) {
            processOrderAgain(rideJson);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View sheet = findViewById(R.id.bookRideSheet);
        sheetBehavior = BottomSheetBehavior.from(sheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        headerRow = findViewById(R.id.headerRow);
        sheetHandle = findViewById(R.id.sheetHandle);

        setSheetDraggable(false);

        @SuppressLint("ClickableViewAccessibility")
        View.OnTouchListener headerDrag = (v, event) -> {
            switch (event.getActionMasked()) {
                case android.view.MotionEvent.ACTION_DOWN:
                case android.view.MotionEvent.ACTION_MOVE:
                    setSheetDraggable(true);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    setSheetDraggable(false);
                    break;
            }
            return false;
        };

        headerRow.setOnTouchListener(headerDrag);
        sheetHandle.setOnTouchListener(headerDrag);

        chipStations  = findViewById(R.id.chipGroupStations);
        chipUsers     = findViewById(R.id.chipGroupUsers);

        btnAddStation = findViewById(R.id.btnAddStation);
        btnAddUser    = findViewById(R.id.btnAddUser);
        btnBookRide   = findViewById(R.id.btnBookRide);

        actCarType = findViewById(R.id.actCarType);
        actTime    = findViewById(R.id.actTime);

        rbNow = findViewById(R.id.rbNow);
        rbScheduled = findViewById(R.id.rbScheduled);

        btnFavorite = findViewById(R.id.btnFavorite);

        setupCarTypeDropdown();
        setupTimeDropdown();
        setupTimeToggle();
        setupAddButtons();
        setupFavoriteButton();
        setupBookButton();

        Object extra = getIntent().getSerializableExtra(FavoritesActivity.EXTRA_BOOK_FAVORITE);
        if (extra instanceof FavoriteRide) {
            FavoriteRide ride = (FavoriteRide) extra;

            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            applyFavoriteToBookRide(ride);
        }

        String rideJson = getIntent().getStringExtra("ORDER_AGAIN_DTO");
        if (rideJson != null) {
            processOrderAgain(rideJson);
        }

    }

    private void processOrderAgain(String json) {
        Gson gson = new Gson();
        PassengerRideDetailsDTO details = gson.fromJson(json, PassengerRideDetailsDTO.class);

        if (sheetBehavior != null) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        applyRideDetailsToForm(details);
    }


    private void applyRideDetailsToForm(PassengerRideDetailsDTO details) {
        if (details == null) return;

        if (details.getRoute() != null) {
            EditText etPickup = findViewById(R.id.etPickup);
            EditText etDestination = findViewById(R.id.etDestination);
            if (etPickup != null) etPickup.setText(extractStreetAddress(details.getRoute().getStartAddress()));
            if (etDestination != null) etDestination.setText(extractStreetAddress(details.getRoute().getEndAddress()));

            clearStations();
            String stopsData = details.getRoute().getStops();
            if (stopsData != null && !stopsData.isEmpty()) {
                String[] stops = stopsData.split("\\|");
                for (String stop : stops) {
                    addStationChip(extractStreetAddress(stop));
                }
            }
        }

        clearUsers();
        if (details.getPassengerEmails() != null) {
            for (String email : details.getPassengerEmails()) {
                addUserChip(email);
            }
        }

        if (actCarType != null && details.getVehicleType() != null) {
            actCarType.setText(details.getVehicleType().name(), false);
        }

        android.widget.CheckBox cbPet = findViewById(R.id.cbPetFriendly);
        android.widget.CheckBox cbChild = findViewById(R.id.cbChildSeat);
        if (cbPet != null) cbPet.setChecked(details.isPetFriendly());
        if (cbChild != null) cbChild.setChecked(details.isBabyFriendly());

        if (details.getScheduledAt() != null) {
            rbScheduled.setChecked(true);
            actTime.setVisibility(View.VISIBLE);

            String timeOnly = extractTimeFromIso(details.getScheduledAt());
            actTime.setText(timeOnly, false);
        } else {
            rbNow.setChecked(true);
            actTime.setVisibility(View.GONE);
        }
    }

    private String extractTimeFromIso(String isoDate) {
        try {
            if (isoDate.contains("T")) {
                String timePart = isoDate.split("T")[1];
                return timePart.substring(0, 5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String extractStreetAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.trim().isEmpty()) return "";
        return fullAddress.split(",")[0].trim();
    }

    private void applyFavoriteToBookRide(FavoriteRide r) {
        EditText etPickup = findViewById(R.id.etPickup);
        EditText etDestination = findViewById(R.id.etDestination);

        if (etPickup != null) etPickup.setText(r.getPickup());
        if (etDestination != null) etDestination.setText(r.getDestination());

        clearStations();
        for (String s : r.getStations()) {
            addStationChip(s);
        }

        clearUsers();
        for (String u : r.getUsers()) {
            addUserChip(u);
        }

        android.widget.AutoCompleteTextView etCarType = findViewById(R.id.actCarType);
        if (etCarType != null) etCarType.setText(r.getCarType(), false);

        android.widget.CheckBox cbPet = findViewById(R.id.cbPetFriendly);
        android.widget.CheckBox cbChild = findViewById(R.id.cbChildSeat);

        if (cbPet != null) cbPet.setChecked(r.isPetFriendly());
        if (cbChild != null) cbChild.setChecked(r.isChildSeat());
    }

    private void clearStations() {
        chipStations.removeAllViews();
    }

    private void clearUsers() {
        chipUsers.removeAllViews();
    }

    private void setSheetDraggable(boolean draggable) {
        try {
            sheetBehavior.setDraggable(draggable);
        } catch (Throwable ignored) {
        }
    }

    private void setupCarTypeDropdown() {
        String[] carTypes = new String[] {
                getString(R.string.car_type_standard),
                getString(R.string.car_type_luxury),
                getString(R.string.car_type_van)
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carTypes);

        actCarType.setAdapter(adapter);
        actCarType.setText(getString(R.string.car_type_luxury), false);

        actCarType.setOnClickListener(v -> actCarType.showDropDown());
    }

    private void setupTimeDropdown() {
        List<String> slots = buildTimeSlots(15, 5);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, slots);

        actTime.setAdapter(adapter);
        actTime.setOnClickListener(v -> actTime.showDropDown());
    }

    private void setupTimeToggle() {
        rbNow.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) actTime.setVisibility(View.GONE);
        });

        rbScheduled.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) {
                actTime.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(actTime.getText())) {
                    actTime.post(() -> actTime.showDropDown());
                }
            }
        });
    }

    private void setupAddButtons() {
        btnAddStation.setOnClickListener(v -> showAddDialogStyled(
                getString(R.string.dialog_add_station_title),
                getString(R.string.dialog_add_station_hint),
                this::addStationChip
        ));

        btnAddUser.setOnClickListener(v -> showAddDialogStyled(
                getString(R.string.dialog_add_user_title),
                getString(R.string.dialog_add_user_hint),
                this::addUserChip
        ));
    }

    private void setupFavoriteButton() {
        ImageButton btnFavorite = findViewById(R.id.btnFavorite);
        if (btnFavorite == null) return;

        btnFavorite.setOnClickListener(v -> showAddToFavoritesDialog());
    }

    private void showAddToFavoritesDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_to_favorites, null);

        EditText etName = view.findViewById(R.id.etFavName);
        MaterialButton btnCancel = view.findViewById(R.id.btnFavCancel);
        MaterialButton btnConfirm = view.findViewById(R.id.btnFavConfirm);

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupBookButton() {
        btnBookRide.setOnClickListener(v -> showConfirmBookingDialog());
    }

    private void showConfirmBookingDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_booking, null);

        MaterialButton btnCancel = dialogView.findViewById(R.id.btnConfirmCancel);
        MaterialButton btnOk     = dialogView.findViewById(R.id.btnConfirmOk);

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            android.view.WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.55f;
            dialog.getWindow().setAttributes(lp);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showAddDialogStyled(String title, String hint, OnValueAdded callback) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);

        android.widget.TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        android.widget.TextView tvSubtitle = dialogView.findViewById(R.id.tvDialogSubtitle);
        android.widget.EditText et = dialogView.findViewById(R.id.etDialogValue);

        com.google.android.material.button.MaterialButton btnCancel =
                dialogView.findViewById(R.id.btnDialogCancel);
        com.google.android.material.button.MaterialButton btnConfirm =
                dialogView.findViewById(R.id.btnDialogConfirm);

        tvTitle.setText(title);
        tvSubtitle.setText(hint);
        et.setHint(hint);

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String value = et.getText() == null ? "" : et.getText().toString().trim();
            if (!value.isEmpty()) callback.onAdded(value);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addStationChip(String text) {
        chipStations.addView(buildClosableChip(text));
    }

    private void addUserChip(String text) {
        chipUsers.addView(buildClosableChip(text));
    }

    private Chip buildClosableChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);

        chip.setChipBackgroundColorResource(R.color.white);
        chip.setTextColor(getResources().getColor(R.color.text, getTheme()));
        chip.setCloseIconTintResource(R.color.text);

        chip.setOnCloseIconClickListener(v -> ((ChipGroup) chip.getParent()).removeView(chip));
        return chip;
    }

    private List<String> buildTimeSlots(int stepMin, int maxHours) {
        List<String> slots = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        int minute = now.get(Calendar.MINUTE);
        int add = (stepMin - (minute % stepMin)) % stepMin;
        now.add(Calendar.MINUTE, add);
        now.set(Calendar.SECOND, 0);

        Calendar end = (Calendar) now.clone();
        end.add(Calendar.HOUR_OF_DAY, maxHours);

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar t = (Calendar) now.clone();

        while (!t.after(end)) {
            slots.add(fmt.format(t.getTime()));
            t.add(Calendar.MINUTE, stepMin);
        }
        return slots;
    }

    interface OnValueAdded {
        void onAdded(String value);
    }
}
