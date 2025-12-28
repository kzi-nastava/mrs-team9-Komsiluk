package com.komsiluk.taxi.ui.shared;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.MainActivity;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class InfoMessageActivity extends BaseNavDrawerActivity {

    @Inject
    AuthManager authManager;

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_BUTTON = "extra_button";
    public static final String EXTRA_IS_LOGOUT = "extra_is_logout";


    public static Intent createIntent(Context ctx, String title, String message, String buttonText) {
        Intent i = new Intent(ctx, InfoMessageActivity.class);
        i.putExtra(EXTRA_TITLE, title);
        i.putExtra(EXTRA_MESSAGE, message);
        i.putExtra(EXTRA_BUTTON, buttonText);
        i.putExtra(EXTRA_IS_LOGOUT, false);
        return i;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_info_message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String message = getIntent().getStringExtra(EXTRA_MESSAGE);
        String button = getIntent().getStringExtra(EXTRA_BUTTON);
        boolean isLogout = getIntent().getBooleanExtra(EXTRA_IS_LOGOUT, false);

        TextView tvTitle = findViewById(R.id.tvInfoTitle);
        TextView tvMessage = findViewById(R.id.tvInfoMessage);
        MaterialButton btnDone = findViewById(R.id.btnInfoDone);

        if (title != null) tvTitle.setText(title);
        if (message != null) tvMessage.setText(message);
        if (button != null) btnDone.setText(button);

        btnDone.setOnClickListener(v -> {
            if (isLogout) {
                authManager.logout();

                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                );
                startActivity(i);
                finish();
            } else {
                finish();
            }
        });
    }

    public static Intent createLogoutIntent(Context ctx, String title, String message, String buttonText) {
        Intent i = new Intent(ctx, InfoMessageActivity.class);
        i.putExtra(EXTRA_TITLE, title);
        i.putExtra(EXTRA_MESSAGE, message);
        i.putExtra(EXTRA_BUTTON, buttonText);
        i.putExtra(EXTRA_IS_LOGOUT, true);
        return i;
    }
}
