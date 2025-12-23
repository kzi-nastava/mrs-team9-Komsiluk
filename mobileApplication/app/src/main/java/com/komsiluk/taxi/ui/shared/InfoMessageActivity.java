package com.komsiluk.taxi.ui.shared;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

public class InfoMessageActivity extends BaseNavDrawerActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_BUTTON = "extra_button";

    public static Intent createIntent(Context ctx, String title, String message, String buttonText) {
        Intent i = new Intent(ctx, InfoMessageActivity.class);
        i.putExtra(EXTRA_TITLE, title);
        i.putExtra(EXTRA_MESSAGE, message);
        i.putExtra(EXTRA_BUTTON, buttonText);
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

        TextView tvTitle = findViewById(R.id.tvInfoTitle);
        TextView tvMessage = findViewById(R.id.tvInfoMessage);
        MaterialButton btnDone = findViewById(R.id.btnInfoDone);

        if (title != null) tvTitle.setText(title);
        if (message != null) tvMessage.setText(message);
        if (button != null) btnDone.setText(button);

        btnDone.setOnClickListener(v -> finish());
    }
}
