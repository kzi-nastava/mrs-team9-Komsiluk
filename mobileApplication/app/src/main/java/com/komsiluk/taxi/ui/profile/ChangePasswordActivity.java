package com.komsiluk.taxi.ui.profile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

public class ChangePasswordActivity extends BaseNavDrawerActivity {

    private EditText etCurrent;
    private EditText etPassword;
    private EditText etRepeat;
    private TextView tvCurrentError;
    private TextView tvPasswordError;
    private TextView tvRepeatError;

    private Drawable normalBg;
    private Drawable errorBg;

    private ChangePasswordViewModel viewModel;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fields
        etCurrent = findViewById(R.id.etCurrent);
        etPassword = findViewById(R.id.etPassword);
        etRepeat = findViewById(R.id.etRepeat);
        tvCurrentError = findViewById(R.id.tvCurrentError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvRepeatError = findViewById(R.id.tvRepeatError);

        normalBg = getDrawable(R.drawable.bg_input_normal);
        errorBg = getDrawable(R.drawable.bg_input_error);

        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);

        viewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateCurrent();
                validatePassword();
                validateRepeat();
            }
        };

        etCurrent.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etRepeat.addTextChangedListener(watcher);

        btnCancel.setOnClickListener(v -> finish());

        viewModel.getSuccessEvent().observe(this, event -> {
            String msg = event.getContentIfNotHandled();
            if (msg == null) return;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish();
        });

        viewModel.getErrorEvent().observe(this, event -> {
            String msg = event.getContentIfNotHandled();
            if (msg == null) return;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        btnConfirm.setOnClickListener(v -> {
            boolean ok0 = validateCurrent();
            boolean ok1 = validatePassword();
            boolean ok2 = validateRepeat();
            if (!ok0 || !ok1 || !ok2) return;

            String oldPwd = etCurrent.getText().toString();
            String newPwd = etPassword.getText().toString();

            viewModel.changePassword(oldPwd, newPwd);
        });
    }

    private boolean validateCurrent() {
        String pwd = etCurrent.getText().toString();
        String error = null;

        if (pwd.isEmpty()) {
            error = getString(R.string.error_password_required);
        }

        if (error != null) {
            etCurrent.setBackground(errorBg);
            tvCurrentError.setText(error);
            tvCurrentError.setVisibility(View.VISIBLE);
            return false;
        } else {
            etCurrent.setBackground(normalBg);
            tvCurrentError.setText("");
            tvCurrentError.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean validatePassword() {
        String pwd = etPassword.getText().toString();
        String error = null;

        if (pwd.isEmpty()) {
            error = getString(R.string.error_password_required);
        } else if (pwd.length() < 8) {
            error = getString(R.string.error_password_too_short);
        } else if (!pwd.matches(".*[A-Za-z].*")) {
            error = getString(R.string.error_password_letter_required);
        } else if (!pwd.matches(".*\\d.*")) {
            error = getString(R.string.error_password_digit_required);
        }

        if (error != null) {
            etPassword.setBackground(errorBg);
            tvPasswordError.setText(error);
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else {
            etPassword.setBackground(normalBg);
            tvPasswordError.setText("");
            tvPasswordError.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean validateRepeat() {
        String pwd = etPassword.getText().toString();
        String rep = etRepeat.getText().toString();
        String error = null;

        if (rep.isEmpty()) {
            error = getString(R.string.error_repeat_required);
        } else if (!rep.equals(pwd)) {
            error = getString(R.string.error_repeat_mismatch);
        }

        if (error != null) {
            etRepeat.setBackground(errorBg);
            tvRepeatError.setText(error);
            tvRepeatError.setVisibility(View.VISIBLE);
            return false;
        } else {
            etRepeat.setBackground(normalBg);
            tvRepeatError.setText("");
            tvRepeatError.setVisibility(View.GONE);
            return true;
        }
    }
}
