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
import com.komsiluk.taxi.databinding.FragmentForgotPasswordBinding;
import com.komsiluk.taxi.ui.auth.AuthActivity;


public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;

    private Drawable normalBg;
    private Drawable errorBg;

    ForgotPasswordViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
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
                .get(ForgotPasswordViewModel.class);


        viewModel.getSuccessEvent().observe(getViewLifecycleOwner(), event -> {
            Boolean success = event.getContentIfNotHandled();

            if (success == null) return;

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.authFragmentContainer, new ForgotPasswordMessageFragment())
                    .commit();
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

        binding.etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.email.setValue(s.toString());
                validateEmail();
            }
        });

        binding.btnCancel.setOnClickListener(v -> {
            ((AuthActivity) requireActivity())
                    .showLogin();
        });

        binding.btnSendEmail.setOnClickListener(v -> {
            boolean ok = validateEmail();
            if (!ok) return;

            viewModel.forgotPassword(binding.etEmail.getText().toString().trim());
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
