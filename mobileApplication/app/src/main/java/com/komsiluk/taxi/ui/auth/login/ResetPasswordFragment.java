package com.komsiluk.taxi.ui.auth.login;

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
import androidx.lifecycle.ViewModelProvider;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.FragmentResetPasswordBinding;
import com.komsiluk.taxi.ui.auth.AuthActivity;


public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;

    private Drawable normalBg;
    private Drawable errorBg;

    ResetPasswordViewModel viewModel;

    private String token;

    public static ResetPasswordFragment newInstance(String token) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            token = getArguments().getString("token");
        }
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


        viewModel = new ViewModelProvider(requireActivity())
                .get(ResetPasswordViewModel.class);

        binding.etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.newPassword.setValue(s.toString());
                validatePassword();
            }
        });

        binding.etRepeat.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.confirmPassword.setValue(s.toString());
                validateRepeat();
            }
        });

        viewModel.getSuccessEvent().observe(getViewLifecycleOwner(), event -> {
            Boolean success = event.getContentIfNotHandled();

            if (success == null) return;

            ((AuthActivity) requireActivity())
                    .showLogin();
            Toast.makeText(
                    requireContext(),
                    getString(R.string.auth_reset_success),
                    Toast.LENGTH_SHORT
            ).show();
        });

        viewModel.getErrorMessageEvent().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message == null) return;

            Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_SHORT
            ).show();
        });

        binding.btnConfirm.setOnClickListener(v -> {
            boolean ok1 = validatePassword();
            boolean ok2 = validateRepeat();
            if (!ok1 || !ok2) return;



            viewModel.resetPassword(
                    token,
                    binding.etPassword.getText().toString().trim(),
                    binding.etRepeat.getText().toString().trim());
        });

        binding.btnCancel.setOnClickListener(v -> {
            requireActivity().finish();
        });

    }

    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$";

    private boolean validatePassword() {
        String pwd = binding.etPassword.getText().toString().trim();
        String error = null;

        if (pwd.isEmpty()) {
            error = getString(R.string.error_password_required);
        } else if (!pwd.matches(PASSWORD_REGEX)) {
            error = getString(R.string.error_password_invalid_format);
        }

        if (error != null) {
            binding.etPassword.setBackground(errorBg);
            binding.tvPasswordError.setText(error);
            binding.tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else {
            binding.etPassword.setBackground(normalBg);
            binding.tvPasswordError.setText(null);
            binding.tvPasswordError.setVisibility(View.GONE);
            return true;
        }
    }


    private boolean validateRepeat() {
        String pwd = binding.etPassword.getText().toString();
        String rep = binding.etRepeat.getText().toString();
        String error = null;

        if (rep.isEmpty()) {
            error = getString(R.string.error_repeat_required);
        } else if (!rep.equals(pwd)) {
            error = getString(R.string.error_repeat_mismatch);
        }

        if (error != null) {
            binding.etRepeat.setBackground(errorBg);
            binding.tvRepeatError.setText(error);
            binding.tvRepeatError.setVisibility(View.VISIBLE);
            return false;
        } else {
            binding.etRepeat.setBackground(normalBg);
            binding.tvRepeatError.setText(null);
            binding.tvRepeatError.setVisibility(View.GONE);
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
