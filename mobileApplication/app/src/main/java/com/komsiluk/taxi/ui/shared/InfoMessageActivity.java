package com.komsiluk.taxi.ui.shared;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.ActivityInfoMessageBinding;
import com.komsiluk.taxi.databinding.ActivityProfileBinding;

public class InfoMessageActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_BUTTON = "extra_button";

    // helper
    public static Intent createIntent(Context ctx, String title, String message, String buttonText) {
        Intent i = new Intent(ctx, InfoMessageActivity.class);
        i.putExtra(EXTRA_TITLE, title);
        i.putExtra(EXTRA_MESSAGE, message);
        i.putExtra(EXTRA_BUTTON, buttonText);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityInfoMessageBinding binding = ActivityInfoMessageBinding.inflate(getLayoutInflater());
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