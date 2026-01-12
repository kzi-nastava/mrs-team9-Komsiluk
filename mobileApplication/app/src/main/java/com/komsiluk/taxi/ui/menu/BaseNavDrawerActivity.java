package com.komsiluk.taxi.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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

import com.google.android.material.navigation.NavigationView;
import com.komsiluk.taxi.AdminActivity;
import com.komsiluk.taxi.DriverActivity;
import com.komsiluk.taxi.MainActivity;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.UserActivity;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.databinding.ActivityBaseNavDrawerBinding;
import com.komsiluk.taxi.driver.history.DriverHistoryActivity;
import com.komsiluk.taxi.ui.about.AboutUsActivity;
import com.komsiluk.taxi.ui.block.AdminBlockUserActivity;
import com.komsiluk.taxi.ui.edit.AdminDriverChangeRequestsActivity;
import com.komsiluk.taxi.ui.ride.FavoritesActivity;
import com.komsiluk.taxi.ui.profile.ProfileActivity;
import com.komsiluk.taxi.ui.ride.ScheduledActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseNavDrawerActivity extends AppCompatActivity {

    @Inject
    AuthManager authManager;

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected ImageButton btnNavMenu;
    protected LinearLayout bottomNav;

    /** every child activity provides its own content layout */
    protected abstract int getContentLayoutId();

    protected int getDrawerMenuResId() {
        return R.menu.menu_app_drawer;
    }

    protected boolean shouldShowBottomNav() {
        return true;
    }

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

        // --- bind drawer stuff ---
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navigationView;
        bottomNav = binding.bottomNav;

        // --- inflate child content into container ---
        FrameLayout contentContainer = binding.contentContainer;
        getLayoutInflater().inflate(getContentLayoutId(), contentContainer, true);

        // --- NEW NAVBAR ONLY: btnNavMenu ---
        View topBar = findViewById(R.id.topAppBar);
        if (topBar != null) {
            btnNavMenu = topBar.findViewById(R.id.btnNavMenu);
            View imgLogo = topBar.findViewById(R.id.imgNavLogo);

            if (btnNavMenu != null) {
                btnNavMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
            }

            if (imgLogo != null) {
                imgLogo.setOnClickListener(v -> navigateToHome());
            }
        }

        navigationView.getMenu().clear();
        navigationView.inflateMenu(getDrawerMenuResId());

        // Bottom nav show/hide
        if (bottomNav != null) {
            bottomNav.setVisibility(shouldShowBottomNav() ? View.VISIBLE : View.GONE);

            View btnBottomLeft = bottomNav.findViewById(R.id.navLeft);
            View btnBottomCenter = bottomNav.findViewById(R.id.navHome);
            View btnBottomRight  = bottomNav.findViewById(R.id.navProfile);

            if (btnBottomLeft != null) {
                ImageButton navLeftButton = (ImageButton) btnBottomLeft;
                if (authManager.getRole() == UserRole.PASSENGER) {
                    btnBottomLeft.setOnClickListener(v -> navigateToFavorites());
                }else if (authManager.getRole() == UserRole.DRIVER) {
                    navLeftButton.setImageResource(R.drawable.clock);
                    btnBottomLeft.setOnClickListener(v -> navigateToScheduled());
                }else{
                    navLeftButton.setImageResource(R.drawable.support);
                    btnBottomLeft.setOnClickListener(v -> navigateToSupport());
                }
            }

            if (btnBottomCenter != null) {
                btnBottomCenter.setOnClickListener(v -> navigateToHome());
            }

            if (btnBottomRight != null) {
                btnBottomRight.setOnClickListener(v -> navigateToProfile());
            }
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            handleDrawerItemClick(item.getItemId());
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    /** every child activity decides what to do for drawer items */
    protected void handleDrawerItemClick(int itemId) {
        if (itemId == R.id.nav_profile) {
            navigateToProfile();
        } else if (itemId == R.id.nav_favorites) {
            navigateToFavorites();
        }else if (itemId == R.id.nav_scheduled_rides) {
            navigateToScheduled();
        }else if (itemId == R.id.nav_block) {
            navigateToBlock();
        }else if (itemId == R.id.nav_usage) {
            // ...
        }else if (itemId == R.id.nav_edit_requests){
            navigateToEditRequests();
        }else if (itemId == R.id.nav_history) {
            navigateToRideHistory();
        } else if (itemId == R.id.nav_chat) {
            // ...
        } else if (itemId == R.id.nav_about) {
            navigateToAbout();
        }
    }

    protected void navigateToHome() {
        if (authManager == null) {
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        Intent intent;
        switch (authManager.getRole()) {
            case PASSENGER:
                intent = new Intent(this, UserActivity.class);
                break;
            case DRIVER:
                intent = new Intent(this, DriverActivity.class);
                break;
            case ADMIN:
                intent = new Intent(this, AdminActivity.class);
                break;
            case GUEST:
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void navigateToProfile() {
        Intent i = new Intent(this, ProfileActivity.class);

        if (authManager.getRole() == UserRole.DRIVER) {
            i.putExtra("role", "driver");
        } else {
            i.putExtra("role", "user");
        }

        startActivity(i);
    }

    protected void navigateToRideHistory() {
        switch (authManager.getRole()) {
            case DRIVER:
                startActivity(new Intent(this, DriverHistoryActivity.class));
                break;
            case PASSENGER:
                break;
            default:
                break;
        }
    }

    protected void navigateToFavorites() {
        startActivity(new Intent(this, FavoritesActivity.class));
    }

    protected void navigateToScheduled() {
        startActivity(new Intent(this, ScheduledActivity.class));
    }

    protected void navigateToAbout() {
        startActivity(new Intent(this, AboutUsActivity.class));
    }

    protected void navigateToBlock() {
        startActivity(new Intent(this, AdminBlockUserActivity.class));
    }

    protected void navigateToEditRequests() {
        startActivity(new Intent(this, AdminDriverChangeRequestsActivity.class));
    }

    protected void navigateToSupport() {
        // Placeholder for support navigation
    }
}
