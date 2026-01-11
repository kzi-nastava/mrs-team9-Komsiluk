package com.komsiluk.taxi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

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
        setupFavorite();
        setupBookButton();
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

    private void setupFavorite() {
        btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            btnFavorite.setImageResource(isFavorite ? R.drawable.favorite : R.drawable.favorite);
        });
    }

    private void setupBookButton() {
        btnBookRide.setOnClickListener(v -> {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
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
