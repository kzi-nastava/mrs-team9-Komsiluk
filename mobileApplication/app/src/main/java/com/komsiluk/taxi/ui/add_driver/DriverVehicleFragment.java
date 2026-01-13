package com.komsiluk.taxi.ui.add_driver;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.FragmentDriverVehicleBinding;

public class DriverVehicleFragment extends Fragment {

    private FragmentDriverVehicleBinding binding;
    private AddDriverViewModel vm;

    private Drawable normalBg;
    private Drawable errorBg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDriverVehicleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity()).get(AddDriverViewModel.class);

        normalBg = requireContext().getDrawable(R.drawable.bg_input_normal);
        errorBg  = requireContext().getDrawable(R.drawable.bg_input_error);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.item_email_dropdown,
                getResources().getStringArray(R.array.car_types)
        );
        binding.actType.setAdapter(typeAdapter);
        binding.actType.setOnClickListener(v -> binding.actType.showDropDown());

        TextWatcher watcher = new SimpleTextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                validateAll();
            }
        };
        binding.etModel.addTextChangedListener(watcher);
        binding.actType.addTextChangedListener(watcher);
        binding.etLicence.addTextChangedListener(watcher);
        binding.etSeats.addTextChangedListener(watcher);

        binding.btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        binding.btnConfirm.setOnClickListener(v -> {
            if (!validateAll()) return;

            vm.carModel = binding.etModel.getText().toString().trim();
            vm.carType = binding.actType.getText().toString().trim();
            vm.licencePlate = binding.etLicence.getText().toString().trim();
            vm.seats = Integer.parseInt(binding.etSeats.getText().toString().trim());
            vm.petFriendly = binding.cbPetFriendly.isChecked();
            vm.childSeat = binding.cbChildSeat.isChecked();

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.addDriverFragmentContainer, DriverCreatedFragment.newInstance())
                    .commit();
        });
    }

    private boolean validateAll() {
        boolean ok = true;

        ok &= validateText(binding.etModel, binding.tvErrorModel, binding.etModel.getText().toString().trim(),
                getString(R.string.err_model_required), 1);

        ok &= validateText(binding.actType, binding.tvErrorType, binding.actType.getText().toString().trim(),
                getString(R.string.err_type_required), 1);

        ok &= validateText(binding.etLicence, binding.tvErrorLicence, binding.etLicence.getText().toString().trim(),
                getString(R.string.err_licence_required), 1);

        String seatsStr = binding.etSeats.getText() == null ? "" : binding.etSeats.getText().toString().trim();
        if (seatsStr.isEmpty()) {
            showError(binding.etSeats, binding.tvErrorSeats, getString(R.string.err_seats_required));
            ok = false;
        } else {
            try {
                int s = Integer.parseInt(seatsStr);
                if (s <= 0) {
                    showError(binding.etSeats, binding.tvErrorSeats, getString(R.string.err_seats_required));
                    ok = false;
                } else {
                    clearError(binding.etSeats, binding.tvErrorSeats);
                }
            } catch (Exception e) {
                showError(binding.etSeats, binding.tvErrorSeats, getString(R.string.err_seats_required));
                ok = false;
            }
        }

        return ok;
    }

    private boolean validateText(View field, View errorView, String value, String error, int minLen) {
        if (value.isEmpty() || value.length() < minLen) {
            showError(field, errorView, error);
            return false;
        }
        clearError(field, errorView);
        return true;
    }

    private void showError(View field, View errorView, String msg) {
        field.setBackground(errorBg);
        if (errorView instanceof android.widget.TextView) {
            ((android.widget.TextView) errorView).setText(msg);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void clearError(View field, View errorView) {
        field.setBackground(normalBg);
        errorView.setVisibility(View.GONE);
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
