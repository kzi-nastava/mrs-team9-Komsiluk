package com.komsiluk.taxi.ui.auth.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.komsiluk.taxi.AdminActivity;
import com.komsiluk.taxi.DriverActivity;
import com.komsiluk.taxi.MainActivity;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.UserActivity;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.databinding.FragmentLoginBinding;
import com.komsiluk.taxi.ui.auth.rider_registration.RiderRegistrationFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    private Drawable normalBg;
    private Drawable errorBg;

    LoginViewModel viewModel;


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


        binding.etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.email.setValue(s.toString());
                validateEmail();
            }
        });

        binding.etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.password.setValue(s.toString());
                validatePassword();
            }
        });

        viewModel = new ViewModelProvider(requireActivity())
                .get(LoginViewModel.class);



        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), event -> {
            UserRole role = event.getContentIfNotHandled();
            if (role == null) return;

            if (role == UserRole.DRIVER) {
                viewModel.sendInitialLocation(45.2556, 19.8407);
            }

            Toast.makeText(
                    requireContext(),
                    getString(R.string.auth_login_success),
                    Toast.LENGTH_SHORT
            ).show();

            Context ctx = requireContext();
            Intent intent = null;

            switch (role) {
                case PASSENGER:
                    Intent currentIntent = requireActivity().getIntent();

                    String pickupAddr = currentIntent.getStringExtra(MainActivity.EXTRA_PICKUP_ADDRESS);
                    String destAddr = currentIntent.getStringExtra(MainActivity.EXTRA_DEST_ADDRESS);

                    intent = new Intent(ctx, UserActivity.class);

                    if (pickupAddr != null && destAddr != null) {
                        intent.putExtra(MainActivity.EXTRA_PICKUP_ADDRESS, pickupAddr);
                        intent.putExtra(MainActivity.EXTRA_DEST_ADDRESS, destAddr);
                        intent.putExtra(MainActivity.EXTRA_PICKUP_LAT,
                                currentIntent.getDoubleExtra(MainActivity.EXTRA_PICKUP_LAT, 0));
                        intent.putExtra(MainActivity.EXTRA_PICKUP_LNG,
                                currentIntent.getDoubleExtra(MainActivity.EXTRA_PICKUP_LNG, 0));
                        intent.putExtra(MainActivity.EXTRA_DEST_LAT,
                                currentIntent.getDoubleExtra(MainActivity.EXTRA_DEST_LAT, 0));
                        intent.putExtra(MainActivity.EXTRA_DEST_LNG,
                                currentIntent.getDoubleExtra(MainActivity.EXTRA_DEST_LNG, 0));
                    }
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

            // Start the activity and clear the back stack
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish(); // Finish the AuthActivity
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message == null) return;

            Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_SHORT
            ).show();
        });




        binding.btnLogin.setOnClickListener(v -> {
            boolean ok1 = validateEmail();
            boolean ok2 = validatePassword();
            if (!ok1 || !ok2) return;

            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            viewModel.login(email,password);
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

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
