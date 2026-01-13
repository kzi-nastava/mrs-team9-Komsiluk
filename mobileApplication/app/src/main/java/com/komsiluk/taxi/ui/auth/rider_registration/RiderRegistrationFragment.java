package com.komsiluk.taxi.ui.auth.rider_registration;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.FragmentRiderRegistrationBinding;
import com.komsiluk.taxi.ui.add_driver.AddDriverViewModel;
import com.komsiluk.taxi.ui.add_driver.DriverVehicleFragment;

public class RiderRegistrationFragment extends Fragment {

    private static final String ARG_ADMIN_ADD_DRIVER = "ARG_ADMIN_ADD_DRIVER";

    private FragmentRiderRegistrationBinding binding;
    private Drawable normalBg;
    private Drawable errorBg;

    private boolean isAdminAddDriver;
    private AddDriverViewModel vm;

    public static RiderRegistrationFragment newInstanceAdminAddDriver() {
        RiderRegistrationFragment f = new RiderRegistrationFragment();
        Bundle b = new Bundle();
        b.putBoolean(ARG_ADMIN_ADD_DRIVER, true);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentRiderRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isAdminAddDriver = getArguments() != null && getArguments().getBoolean(ARG_ADMIN_ADD_DRIVER, false);
        vm = new ViewModelProvider(requireActivity()).get(AddDriverViewModel.class);

        normalBg = requireContext().getDrawable(R.drawable.bg_input_normal);
        errorBg  = requireContext().getDrawable(R.drawable.bg_input_error);

        if (isAdminAddDriver) {
            binding.tvTitle.setText(getString(R.string.admin_add_driver_title)); // dodaj string
            binding.btnSubmit.setText(getString(R.string.btn_next));             // dodaj string

            binding.tvLabelPassword.setVisibility(View.GONE);
            binding.etPassword.setVisibility(View.GONE);
            binding.tvErrorPassword.setVisibility(View.GONE);

            binding.tvLabelRepeat.setVisibility(View.GONE);
            binding.etRepeat.setVisibility(View.GONE);
            binding.tvErrorRepeat.setVisibility(View.GONE);
        }

        TextWatcher watcher = new SimpleTextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                validateFirstname();
                validateLastname();
                validateAddress();
                validateCity();
                validatePhone();
                validateEmail();
                if (!isAdminAddDriver) {
                    validatePassword();
                    validateRepeat();
                }
            }
        };

        binding.etFirstname.addTextChangedListener(watcher);
        binding.etLastname.addTextChangedListener(watcher);
        binding.etAddress.addTextChangedListener(watcher);
        binding.etCity.addTextChangedListener(watcher);
        binding.etPhone.addTextChangedListener(watcher);
        binding.etEmail.addTextChangedListener(watcher);

        if (!isAdminAddDriver) {
            binding.etPassword.addTextChangedListener(watcher);
            binding.etRepeat.addTextChangedListener(watcher);
        }

        binding.btnSubmit.setOnClickListener(v -> {
            if (!validateAll()) return;

            if (isAdminAddDriver) {
                vm.firstName = binding.etFirstname.getText().toString().trim();
                vm.lastName  = binding.etLastname.getText().toString().trim();
                vm.address   = binding.etAddress.getText().toString().trim();
                vm.city      = binding.etCity.getText().toString().trim();
                vm.phone     = binding.etPhone.getText().toString().trim();
                vm.email     = binding.etEmail.getText().toString().trim();

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.addDriverFragmentContainer, new DriverVehicleFragment())
                        .addToBackStack("add_driver_step1")
                        .commit();
            } else {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.authFragmentContainer, new SuccessfulRegistrationFragment())
                        .commit();
            }
        });
    }

    private boolean validateAll() {
        boolean ok =  validateFirstname() &
                validateLastname() &
                validateAddress() &
                validateCity() &
                validatePhone() &
                validateEmail();

        if (!isAdminAddDriver) {
            ok = ok & validatePassword() & validateRepeat();
        }
        return ok;
    }

    private boolean validateFirstname() {
        return validateText(binding.etFirstname, binding.tvErrorFirstname,
                binding.etFirstname.getText().toString().trim(),
                getString(R.string.err_firstname_required), 2);
    }

    private boolean validateLastname() {
        return validateText(binding.etLastname, binding.tvErrorLastname,
                binding.etLastname.getText().toString().trim(),
                getString(R.string.err_lastname_required), 2);
    }

    private boolean validateAddress() {
        return validateText(binding.etAddress, binding.tvErrorAddress,
                binding.etAddress.getText().toString().trim(),
                getString(R.string.err_address_required), 1);
    }

    private boolean validateCity() {
        return validateText(binding.etCity, binding.tvErrorCity,
                binding.etCity.getText().toString().trim(),
                getString(R.string.err_city_required), 1);
    }

    private boolean validatePhone() {
        String phone = binding.etPhone.getText().toString().trim().replace(" ", "");
        if (phone.isEmpty() || !phone.matches("\\+?\\d{6,}")) {
            showError(binding.etPhone, binding.tvErrorPhone, getString(R.string.err_phone_invalid));
            return false;
        }
        clearError(binding.etPhone, binding.tvErrorPhone);
        return true;
    }

    private boolean validateEmail() {
        String email = binding.etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            showError(binding.etEmail, binding.tvErrorEmail, getString(R.string.error_email_required));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(binding.etEmail, binding.tvErrorEmail, getString(R.string.error_email_invalid));
            return false;
        }
        clearError(binding.etEmail, binding.tvErrorEmail);
        return true;
    }

    private boolean validatePassword() {
        String pwd = binding.etPassword.getText().toString();
        if (pwd.isEmpty() || pwd.length() < 8) {
            showError(binding.etPassword, binding.tvErrorPassword, getString(R.string.error_password_too_short));
            return false;
        }
        clearError(binding.etPassword, binding.tvErrorPassword);
        return true;
    }

    private boolean validateRepeat() {
        String pwd = binding.etPassword.getText().toString();
        String rep = binding.etRepeat.getText().toString();
        if (!rep.equals(pwd)) {
            showError(binding.etRepeat, binding.tvErrorRepeat, getString(R.string.error_repeat_mismatch));
            return false;
        }
        clearError(binding.etRepeat, binding.tvErrorRepeat);
        return true;
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
