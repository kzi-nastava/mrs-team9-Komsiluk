package com.komsiluk.taxi;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.komsiluk.taxi.databinding.ActivityMainBinding;
import com.komsiluk.taxi.ui.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnUserProfile.setOnClickListener(v -> openProfile("user"));
        binding.btnDriverProfile.setOnClickListener(v -> openProfile("driver"));
    }

    private void openProfile(String role) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("role", role);
        startActivity(i);
    }
}