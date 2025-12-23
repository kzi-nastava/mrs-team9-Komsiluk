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

import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

public class ChangePasswordActivity extends BaseNavDrawerActivity {

    private EditText etPassword;
    private EditText etRepeat;
    private TextView tvPasswordError;
    private TextView tvRepeatError;

    private Drawable normalBg;
    private Drawable errorBg;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fields
        etPassword = findViewById(R.id.etPassword);
        etRepeat = findViewById(R.id.etRepeat);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvRepeatError = findViewById(R.id.tvRepeatError);

        normalBg = getDrawable(R.drawable.bg_input_normal);
        errorBg = getDrawable(R.drawable.bg_input_error);

        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validatePassword();
                validateRepeat();
            }
        };

        etPassword.addTextChangedListener(watcher);
        etRepeat.addTextChangedListener(watcher);

        btnCancel.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            boolean ok1 = validatePassword();
            boolean ok2 = validateRepeat();
            if (!ok1 || !ok2) return;

            Toast.makeText(this, getString(R.string.change_password_success), Toast.LENGTH_SHORT).show();
            finish();
        });
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
