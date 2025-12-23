package com.komsiluk.taxi.ui.menu;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.ActivityBaseNavDrawerBinding;

public abstract class BaseNavDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected ImageButton btnNavMenu;
    protected LinearLayout bottomNav;

    /** every child activity provides its own content layout */
    protected abstract int getContentLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        ActivityBaseNavDrawerBinding binding = ActivityBaseNavDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View rootContent = binding.rootContent;
        View root = binding.getRoot();
        Window window = getWindow();

        root.setBackgroundColor(ContextCompat.getColor(this, R.color.black));

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, rootContent);
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(rootContent, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = binding.drawerLayout;
        navigationView = binding.navigationView;
        btnNavMenu = findViewById(R.id.topAppBar).findViewById(R.id.btnNavMenu);
        bottomNav = binding.bottomNav;

        FrameLayout contentContainer = binding.contentContainer;
        getLayoutInflater().inflate(getContentLayoutId(), contentContainer, true);
        btnNavMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        View header = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(item -> {
            handleDrawerItemClick(item.getItemId());
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    protected void handleBottomNavClick(int itemId) {
        // override in child if needed
    }

    /** every child activity decides what to do for drawer items */
    protected void handleDrawerItemClick(int itemId) {
        if (itemId == R.id.nav_profile) {
            // startActivity(new Intent(this, ProfileActivity.class));
        } else if (itemId == R.id.nav_favorites) {
            // ...
        } else if (itemId == R.id.nav_usage) {
            // ...
        } else if (itemId == R.id.nav_history) {
            // ...
        } else if (itemId == R.id.nav_chat) {
            // ...
        } else if (itemId == R.id.nav_about) {
            // ...
        }
    }
}
