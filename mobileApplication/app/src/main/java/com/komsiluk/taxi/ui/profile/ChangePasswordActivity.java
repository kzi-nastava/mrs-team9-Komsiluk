package com.komsiluk.taxi.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.komsiluk.taxi.R;
import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.databinding.ActivityChangePasswordBinding;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etPassword;
    private EditText etRepeat;
    private TextView tvPasswordError;
    private TextView tvRepeatError;

    private android.graphics.drawable.Drawable normalBg;
    private android.graphics.drawable.Drawable errorBg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        ActivityChangePasswordBinding binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View root = binding.getRoot();
        Window window = getWindow();

        root.setBackgroundColor(ContextCompat.getColor(this, R.color.black));

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, root);
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etPassword = findViewById(R.id.etPassword);
        etRepeat = findViewById(R.id.etRepeat);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvRepeatError = findViewById(R.id.tvRepeatError);

        normalBg = ContextCompat.getDrawable(this, R.drawable.bg_input_normal);
        errorBg = ContextCompat.getDrawable(this, R.drawable.bg_input_error);

        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);

        // live validation
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
