package com.komsiluk.taxi.ui.add_driver;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.auth.AuthActivity;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DriverActivationActivity extends BaseNavDrawerActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_driver_activation;
    }

    @Override
    protected boolean shouldShowBottomNav() {
        return false;
    }

    @Override
    protected int getDrawerMenuResId() {
        return R.menu.menu_guest_drawer;
    }

    private EditText etPassword;
    private EditText etRepeat;
    private TextView tvPasswordError;
    private TextView tvRepeatError;

    private Drawable normalBg;
    private Drawable errorBg;

    private DriverActivationViewModel viewModel;

    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        token = extractToken(getIntent());
        if (token == null) {
            Toast.makeText(this, getString(R.string.driver_activation_missing_token), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etPassword = findViewById(R.id.etPassword);
        etRepeat = findViewById(R.id.etRepeat);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvRepeatError = findViewById(R.id.tvRepeatError);

        normalBg = getDrawable(R.drawable.bg_input_normal);
        errorBg = getDrawable(R.drawable.bg_input_error);

        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);

        viewModel = new ViewModelProvider(this).get(DriverActivationViewModel.class);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                validatePassword();
                validateRepeat();
            }
        };

        etPassword.addTextChangedListener(watcher);
        etRepeat.addTextChangedListener(watcher);

        btnCancel.setOnClickListener(v -> finish());

        viewModel.getSuccessEvent().observe(this, event -> {
            String ok = event.getContentIfNotHandled();
            if (ok == null) return;

            Toast.makeText(this, getString(R.string.driver_activation_success), Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, AuthActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });

        viewModel.getErrorEvent().observe(this, event -> {
            String msg = event.getContentIfNotHandled();
            if (msg == null) return;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        btnConfirm.setOnClickListener(v -> {
            boolean ok1 = validatePassword();
            boolean ok2 = validateRepeat();
            if (!ok1 || !ok2) return;

            String pwd = etPassword.getText().toString();
            viewModel.activate(token, pwd);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String newToken = extractToken(intent);
        if (newToken != null) token = newToken;
    }

    private String extractToken(Intent intent) {
        Uri data = intent == null ? null : intent.getData();
        if (data == null) return null;

        String t = data.getQueryParameter("token");
        if (t == null || t.trim().isEmpty()) return null;
        return t.trim();
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
