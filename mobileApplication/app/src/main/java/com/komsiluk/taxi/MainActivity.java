package com.komsiluk.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.databinding.ActivityMainBinding;
import com.komsiluk.taxi.ui.about.AboutUsActivity;
import com.komsiluk.taxi.ui.auth.AuthActivity;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseNavDrawerActivity {

    @Inject AuthManager authManager;

    private ActivityMainBinding mainBinding;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getDrawerMenuResId() {
        return R.menu.menu_guest_drawer;
    }

    @Override
    protected boolean shouldShowBottomNav() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contentRoot = findViewById(R.id.main);
        mainBinding = ActivityMainBinding.bind(contentRoot);
    }

    @Override
    protected void handleDrawerItemClick(int itemId) {
        if (itemId == R.id.nav_login) {
            Intent i = new Intent(this, AuthActivity.class);
            i.putExtra("AUTH_DESTINATION", "LOGIN");
            startActivity(i);
        } else if (itemId == R.id.nav_register) {
            Intent i = new Intent(this, AuthActivity.class);
            i.putExtra("AUTH_DESTINATION", "REGISTER");
            startActivity(i);
        } else if (itemId == R.id.nav_about) {
            startActivity(new Intent(this, AboutUsActivity.class));
        }
    }
}
