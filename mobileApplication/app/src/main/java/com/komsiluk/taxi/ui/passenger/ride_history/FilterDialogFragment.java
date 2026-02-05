package com.komsiluk.taxi.ui.passenger.ride_history;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.DialogDateFilterBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class FilterDialogFragment extends DialogFragment {

    public interface FilterListener {
        void onFilterApplied(String fromDate, String toDate);
        void onFilterCleared();
    }

    private DialogDateFilterBinding binding;
    private FilterListener listener;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());

    public static FilterDialogFragment newInstance(FilterListener listener) {
        FilterDialogFragment fragment = new FilterDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.9f);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogDateFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.etFrom.setOnClickListener(v -> showDatePicker(binding.etFrom));
        binding.etTo.setOnClickListener(v -> showDatePicker(binding.etTo));


        binding.btnClear.setOnClickListener(v -> {
            if (listener != null) listener.onFilterCleared();
            dismiss();
        });

        binding.btnApply.setOnClickListener(v -> {
            String from = binding.etFrom.getText().toString();
            String to = binding.etTo.getText().toString();

            if (from.isEmpty() || to.isEmpty()) {
                Toast.makeText(getContext(), "Please select both dates", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) listener.onFilterApplied(from, to);
            dismiss();
        });
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(
                requireContext(),
                R.style.AppDatePickerDialogTheme,
                (view, year, month, dayOfMonth) -> {
                    LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
                    target.setText(date.format(formatter));
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dlg.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
