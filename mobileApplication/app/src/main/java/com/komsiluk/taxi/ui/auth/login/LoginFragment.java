package com.komsiluk.taxi.ui.auth.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.AdminActivity;
import com.komsiluk.taxi.DriverActivity;
import com.komsiluk.taxi.MainActivity;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.UserActivity;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.databinding.FragmentLoginBinding;
import com.komsiluk.taxi.ui.auth.rider_registration.RiderRegistrationFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    @Inject
    AuthManager authManager;

    private FragmentLoginBinding binding;

    private Drawable normalBg;
    private Drawable errorBg;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
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
        errorBg = requireContext().getDrawable(R.drawable.bg_input_error);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
                validatePassword();
            }
        };

        binding.etEmail.addTextChangedListener(watcher);
        binding.etPassword.addTextChangedListener(watcher);

        binding.btnLogin.setOnClickListener(v -> {
            boolean ok1 = validateEmail();
            boolean ok2 = validatePassword();
            if (!ok1 || !ok2) return;

            boolean success = this.authManager.login(
                    binding.etEmail.getText().toString().trim(),
                    binding.etPassword.getText().toString().trim()
            );

            if (!success) {
                Toast.makeText(
                        requireContext(),
                        getString(R.string.auth_invalid_credentials),
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Toast.makeText(
                    requireContext(),
                    getString(R.string.auth_login_success),
                    Toast.LENGTH_SHORT
            ).show();

            UserRole role = authManager.getRole();
            Context ctx = requireContext();
            Intent intent;

            switch (role) {
                case PASSENGER:
                    intent = new Intent(ctx, UserActivity.class);
                    break;
                case DRIVER:
                    intent = new Intent(ctx, DriverActivity.class);
                    break;
                case ADMIN:
                    intent = new Intent(ctx, AdminActivity.class);
                    break;
                default:
                    intent = new Intent(ctx, MainActivity.class);
                    break;
            }

            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.authFragmentContainer, new ForgotPasswordFragment())
                    .commit();
        });
        binding.tvCreateAccount.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.authFragmentContainer, new RiderRegistrationFragment())
                    .commit();
        });
    }

    private boolean validateEmail() {
        String email = binding.etEmail.getText().toString().trim();
        String error = null;

        if (email.isEmpty()) {
            error = getString(R.string.error_email_required);
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = getString(R.string.error_email_invalid);
        }

        if (error != null) {
            binding.etEmail.setBackground(errorBg);
            binding.tvEmailError.setText(error);
            binding.tvEmailError.setVisibility(View.VISIBLE);
            return false;
        } else {
            binding.etEmail.setBackground(normalBg);
            binding.tvEmailError.setText("");
            binding.tvEmailError.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean validatePassword() {
        String pwd = binding.etPassword.getText().toString();
        String error = null;

        if (pwd.isEmpty()) {
            error = getString(R.string.error_password_required);
        } else if (pwd.length() < 8) {
            error = getString(R.string.error_password_too_short);
        }

        if (error != null) {
            binding.etPassword.setBackground(errorBg);
            binding.tvPasswordError.setText(error);
            binding.tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else {
            binding.etPassword.setBackground(normalBg);
            binding.tvPasswordError.setText("");
            binding.tvPasswordError.setVisibility(View.GONE);
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
