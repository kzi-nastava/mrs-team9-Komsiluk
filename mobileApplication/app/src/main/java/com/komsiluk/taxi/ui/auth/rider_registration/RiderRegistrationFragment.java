package com.komsiluk.taxi.ui.auth.rider_registration;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.FragmentRiderRegistrationBinding;
import com.komsiluk.taxi.ui.auth.login.ForgotPasswordFragment;
import com.komsiluk.taxi.ui.auth.login.VerificationMessageFragment;

public class RiderRegistrationFragment extends Fragment {

    private FragmentRiderRegistrationBinding binding;

    private Drawable normalBg;
    private Drawable errorBg;

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
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        normalBg = requireContext().getDrawable(R.drawable.bg_input_normal);
        errorBg  = requireContext().getDrawable(R.drawable.bg_input_error);

        TextWatcher watcher = new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateFirstname();
                validateLastname();
                validateAddress();
                validateCity();
                validatePhone();
                validateEmail();
                validatePassword();
                validateRepeat();
            }
        };

        binding.etFirstname.addTextChangedListener(watcher);
        binding.etLastname.addTextChangedListener(watcher);
        binding.etAddress.addTextChangedListener(watcher);
        binding.etCity.addTextChangedListener(watcher);
        binding.etPhone.addTextChangedListener(watcher);
        binding.etEmail.addTextChangedListener(watcher);
        binding.etPassword.addTextChangedListener(watcher);
        binding.etRepeat.addTextChangedListener(watcher);

        binding.btnSubmit.setOnClickListener(v -> {
            if (!validateAll()) return;
            //trebalo bi da se ode na VerificationMessage, ali radi demonstracije idem odmah na ovaj prozor
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.authFragmentContainer, new SuccessfulRegistrationFragment())
                    .commit();
        });
    }

    // =====================
    // Validation helpers
    // =====================

    private boolean validateAll() {
        return  validateFirstname() &
                validateLastname() &
                validateAddress() &
                validateCity() &
                validatePhone() &
                validateEmail() &
                validatePassword() &
                validateRepeat();
    }

    private boolean validateFirstname() {
        return validateText(
                binding.etFirstname,
                binding.tvErrorFirstname,
                binding.etFirstname.getText().toString().trim(),
                getString(R.string.err_firstname_required),
                2
        );
    }

    private boolean validateLastname() {
        return validateText(
                binding.etLastname,
                binding.tvErrorLastname,
                binding.etLastname.getText().toString().trim(),
                getString(R.string.err_lastname_required),
                2
        );
    }

    private boolean validateAddress() {
        return validateText(
                binding.etAddress,
                binding.tvErrorAddress,
                binding.etAddress.getText().toString().trim(),
                getString(R.string.err_address_required),
                1
        );
    }

    private boolean validateCity() {
        return validateText(
                binding.etCity,
                binding.tvErrorCity,
                binding.etCity.getText().toString().trim(),
                getString(R.string.err_city_required),
                1
        );
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

    // =====================
    // UI helpers
    // =====================

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

    // =====================
    // TextWatcher helper
    // =====================

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
