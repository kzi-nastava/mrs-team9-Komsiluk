package com.komsiluk.taxi.ui.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.ActivityAuthBinding;
import com.komsiluk.taxi.ui.auth.login.LoginFragment;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            binding.authFragmentContainer.getId(),
                            resolveStartFragment()
                    )
                    .commit();
        }

    }

    private Fragment resolveStartFragment() {
        String dest = getIntent().getStringExtra("AUTH_DESTINATION");

        if (dest == null) {
            return new LoginFragment();
        }

        switch (dest) {
//            case "REGISTER":
//                return new RegisterFragment();
//            case "RESET":
//                return new ResetPasswordFragment();
//            case "REGISTER_SUCCESS":
//                return new RegisterSuccessFragment();
            case "LOGIN":
            default:
                return new LoginFragment();
        }
    }

}