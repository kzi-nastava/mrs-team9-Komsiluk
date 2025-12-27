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

import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.databinding.ActivityMainBinding;
import com.komsiluk.taxi.ui.auth.AuthActivity;
import com.komsiluk.taxi.ui.auth.login.ResetPasswordFragment;
import com.komsiluk.taxi.ui.profile.ProfileActivity;
import com.komsiluk.taxi.driver.history.DriverHistoryActivity;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {


    @Inject AuthManager authManager;
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

        binding.btnProfile.setOnClickListener(v -> openProfile());
        binding.btnLogin.setOnClickListener(v -> {
            Intent i = new Intent(this, AuthActivity.class);
            i.putExtra("AUTH_DESTINATION","LOGIN");
            startActivity(i);
        });

        binding.btnRegister.setOnClickListener(v -> {
            Intent i = new Intent(this, AuthActivity.class);
            i.putExtra("AUTH_DESTINATION","REGISTER");
            startActivity(i);
        });



        binding.btnGoResetPassword.setOnClickListener(v -> {
            Intent i = new Intent(this, AuthActivity.class);
            i.putExtra("AUTH_DESTINATION","RESET");
            startActivity(i);
        });
        binding.btnDriverHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, DriverHistoryActivity.class));
        });

    }

    private void openProfile() {
        if(this.authManager.getRole() == UserRole.GUEST) return;
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }
}