package com.komsiluk.taxi.ui.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.ActivityAuthBinding;
import com.komsiluk.taxi.ui.auth.login.LoginFragment;
import com.komsiluk.taxi.ui.auth.login.ResetPasswordFragment;
import com.komsiluk.taxi.ui.auth.login.VerificationMessageFragment;
import com.komsiluk.taxi.ui.auth.rider_registration.RiderRegistrationFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
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
            case "REGISTER":
                return new RiderRegistrationFragment();
//            case "RESET":
//                return new ResetPasswordFragment();
//            case "REGISTER_SUCCESS":
//                return new RegisterSuccessFragment();
            case "VERIFY":
                return new VerificationMessageFragment();
            case "LOGIN":
            default:
                return new LoginFragment();
        }
    }

    public void showLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.authFragmentContainer, new LoginFragment())
                .commit();
    }

}