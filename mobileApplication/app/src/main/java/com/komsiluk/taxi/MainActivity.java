package com.komsiluk.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.komsiluk.taxi.databinding.ActivityMainBinding;
import com.komsiluk.taxi.ui.auth.AuthActivity;
import com.komsiluk.taxi.ui.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        binding.btnUserProfile.setOnClickListener(v -> openProfile("user"));
        binding.btnDriverProfile.setOnClickListener(v -> openProfile("driver"));
        binding.btnLogin.setOnClickListener(v -> {
            Intent i = new Intent(this, AuthActivity.class);
            i.putExtra("AUTH_DESTINATION","LOGIN");
            startActivity(i);
        });
    }

    private void openProfile(String role) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("role", role);
        startActivity(i);
    }
}