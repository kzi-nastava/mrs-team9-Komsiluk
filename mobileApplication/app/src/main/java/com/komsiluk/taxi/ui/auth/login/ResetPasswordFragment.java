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

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.FragmentResetPasswordBinding;
import com.komsiluk.taxi.ui.auth.AuthActivity;


public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;

    private Drawable normalBg;
    private Drawable errorBg;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
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
                validatePassword();
                validateRepeat();
            }
        };

        binding.etPassword.addTextChangedListener(watcher);
        binding.etRepeat.addTextChangedListener(watcher);

        binding.btnConfirm.setOnClickListener(v -> {
            boolean ok1 = validatePassword();
            boolean ok2 = validateRepeat();
            if (!ok1 || !ok2) return;

            Toast.makeText(
                    requireContext(),
                    getString(R.string.auth_reset_success),
                    Toast.LENGTH_SHORT
            ).show();

            ((AuthActivity) requireActivity())
                    .showLogin();
        });

        binding.btnCancel.setOnClickListener(v -> {
            requireActivity().finish();
        });

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
            binding.tvRepeatError.setText("");
            binding.tvRepeatError.setVisibility(View.GONE);
            return true;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
