package com.komsiluk.taxi.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.ActivityAuthBinding;
import com.komsiluk.taxi.ui.auth.login.LoginFragment;
import com.komsiluk.taxi.ui.auth.login.ForgotPasswordFragment;
import com.komsiluk.taxi.ui.auth.login.ResetPasswordFragment;
import com.komsiluk.taxi.ui.about.AboutUsActivity;
import com.komsiluk.taxi.ui.auth.login.VerificationMessageFragment;
import com.komsiluk.taxi.ui.auth.rider_registration.RiderRegistrationFragment;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends BaseNavDrawerActivity {

    private ActivityAuthBinding binding;


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_auth;
    }

    @Override
    protected boolean shouldShowBottomNav() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthBinding.bind(findViewById(R.id.authRoot));

        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.menu_guest_drawer);

        Fragment startFragment;

        // Provera deep link-a
        Uri data = getIntent().getData();
        if (data != null && data.getPath() != null && data.getPath().startsWith("/reset-password")) {
            String token = data.getQueryParameter("token");
            // Prosledjuj token fragmentu
            startFragment = ResetPasswordFragment.newInstance(token);
        } else {
            // Normalna logika
            startFragment = resolveStartFragment();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.authFragmentContainer.getId(), startFragment)
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

    @Override
    protected void handleDrawerItemClick(int itemId) {
        if (itemId == R.id.nav_login) {
            showLogin();
        } else if (itemId == R.id.nav_register) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.authFragmentContainer, new RiderRegistrationFragment())
                    .commit();
        } else if (itemId == R.id.nav_about) {
            Intent intent = new Intent(this,AboutUsActivity.class);
            startActivity(intent);
        }
    }


}