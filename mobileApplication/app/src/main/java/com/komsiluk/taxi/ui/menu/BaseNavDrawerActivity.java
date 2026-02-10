package com.komsiluk.taxi.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;
import com.komsiluk.taxi.AdminActivity;
import com.komsiluk.taxi.DriverActivity;
import com.komsiluk.taxi.MainActivity;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.UserActivity;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.data.remote.add_driver.DriverResponse;
import com.komsiluk.taxi.data.remote.change_driver_status.DriverStatus;
import com.komsiluk.taxi.data.remote.change_driver_status.DriverStatusUpdate;
import com.komsiluk.taxi.data.remote.driver_history.DriverService;
import com.komsiluk.taxi.data.remote.profile.UserProfileResponse;
import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.databinding.ActivityBaseNavDrawerBinding;
import com.komsiluk.taxi.ui.driver_history.DriverHistoryActivity;
import com.komsiluk.taxi.ui.about.AboutUsActivity;
import com.komsiluk.taxi.ui.add_driver.AddDriverActivity;
import com.komsiluk.taxi.ui.block.AdminBlockUserActivity;
import com.komsiluk.taxi.ui.edit.AdminDriverChangeRequestsActivity;
import com.komsiluk.taxi.ui.admin.ride_history.AdminRideHistoryActivity;
import com.komsiluk.taxi.ui.passenger.ride_history.PassengerRideHistoryActivity;
import com.komsiluk.taxi.ui.report.UsageReportsActivity;
import com.komsiluk.taxi.ui.ride.FavoritesActivity;
import com.komsiluk.taxi.ui.profile.ProfileActivity;
import com.komsiluk.taxi.ui.ride.ScheduledActivity;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public abstract class BaseNavDrawerActivity extends AppCompatActivity {

    @Inject
    AuthManager authManager;

    @Inject
    SessionManager sessionManager;

    @Inject
    UserService userService;

    @Inject
    DriverService driverService;

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

    private View driverStatusDot;

    private View driverHeader;
    private TextView tvDriverStatus;
    private TextView tvDriverActiveToday;
    private MaterialSwitch swDriverActive;
    private boolean statusToggleInFlight = false;

    private boolean currentDriverActive= false;

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

            driverStatusDot = topBar.findViewById(R.id.viewDriverStatusDot);
            if (driverStatusDot != null) {
                driverStatusDot.setVisibility(authManager.getRole() == UserRole.DRIVER ? View.VISIBLE : View.GONE);
            }

            if (btnNavMenu != null) {
                btnNavMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
            }

            if (imgLogo != null) {
                imgLogo.setOnClickListener(v -> navigateToHome());
            }
        }

        navigationView.getMenu().clear();
        navigationView.inflateMenu(getDrawerMenuResId());

        View header = navigationView.getHeaderView(0);

        driverHeader = header.findViewById(R.id.driverStatusHeader);
        tvDriverStatus = header.findViewById(R.id.tvDriverStatus);
        tvDriverActiveToday = header.findViewById(R.id.tvDriverActiveToday);
        swDriverActive = header.findViewById(R.id.swDriverActive);

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (authManager.getRole() == UserRole.DRIVER) {
                    fetchDriverProfileAndUpdateStatusUi();
                }
            }
        });

        boolean isDriver = authManager.getRole() == UserRole.DRIVER;
        if (driverHeader != null) driverHeader.setVisibility(isDriver ? View.VISIBLE : View.GONE);

        if(isDriver)
                fetchDriverProfileAndUpdateStatusUi();

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

    private void fetchDriverProfileAndUpdateStatusUi() {
        if (userService == null || sessionManager == null) return;

        Long id = sessionManager.getUserId();
        if (id == null) return;

        userService.getProfile(id).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> resp) {

                if (!resp.isSuccessful() || resp.body() == null) return;

                UserProfileResponse p = resp.body();

                boolean isActive = isDriverActive(p.getDriverStatus());

                updateDriverDot(isActive);

                if (tvDriverStatus != null) {
                    tvDriverStatus.setText(isActive ? "Active" : "Inactive");
                    tvDriverStatus.setTextColor(ContextCompat.getColor(
                            BaseNavDrawerActivity.this,
                            isActive ? R.color.green : R.color.red
                    ));
                }

                currentDriverActive=isActive;

                if (swDriverActive != null) {
                    swDriverActive.setOnCheckedChangeListener(null);
                    swDriverActive.setOnClickListener(null);

                    swDriverActive.setChecked(currentDriverActive);

                    swDriverActive.setOnClickListener(v -> {
                        if (statusToggleInFlight) {
                            swDriverActive.setChecked(currentDriverActive);
                            return;
                        }

                        boolean targetActive = !currentDriverActive;

                        swDriverActive.setChecked(currentDriverActive);

                        showConfirmDriverStatusDialog(targetActive);
                    });
                }


                if (tvDriverActiveToday != null) {
                    long mins = p.getActiveMinutesLast24h() != null ? p.getActiveMinutesLast24h() : 0L;
                    tvDriverActiveToday.setText("Active today: " + formatMinutes(mins));
                }
            }

            @Override
            public void onFailure(Call<com.komsiluk.taxi.data.remote.profile.UserProfileResponse> call, Throwable t) {
            }
        });
    }

    private String formatMinutes(long totalMin) {
        if (totalMin <= 0) return "0m";

        long h = totalMin / 60;
        long m = totalMin % 60;

        if (h <= 0) return m + "m";
        if (m == 0) return h + "h";
        return h + "h " + m + "m";
    }

    private void showConfirmDriverStatusDialog(boolean targetActive) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_driver_status, null, false);

        ImageButton btnClose = v.findViewById(R.id.btnStatusClose);
        TextView tvTitle = v.findViewById(R.id.tvStatusTitle);
        TextView tvMsg = v.findViewById(R.id.tvStatusMessage);
        MaterialButton btnCancel = v.findViewById(R.id.btnStatusCancel);
        MaterialButton btnConfirm = v.findViewById(R.id.btnStatusConfirm);

        tvTitle.setText(targetActive ? R.string.driver_go_active_title : R.string.driver_go_inactive_title);
        tvMsg.setText(targetActive ? R.string.driver_go_active_msg : R.string.driver_go_inactive_msg);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(v)
                .setCancelable(true)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        View.OnClickListener dismiss = x -> dialog.dismiss();
        btnClose.setOnClickListener(dismiss);
        btnCancel.setOnClickListener(dismiss);

        btnConfirm.setOnClickListener(x -> {
            if (statusToggleInFlight) return;

            Long id = sessionManager != null ? sessionManager.getUserId() : null;
            if (id == null) return;

            statusToggleInFlight = true;
            btnConfirm.setEnabled(false);
            btnCancel.setEnabled(false);
            btnClose.setEnabled(false);

            callUpdateDriverStatus(id, targetActive,
                    () -> {
                        statusToggleInFlight = false;
                        dialog.dismiss();

                        currentDriverActive = targetActive;
                        if (swDriverActive != null) swDriverActive.setChecked(currentDriverActive);

                        fetchDriverProfileAndUpdateStatusUi();
                    },
                    (err) -> {
                        statusToggleInFlight = false;
                        Toast.makeText(this, err, Toast.LENGTH_LONG).show();

                        if (swDriverActive != null) swDriverActive.setChecked(currentDriverActive);

                        fetchDriverProfileAndUpdateStatusUi();
                        dialog.dismiss();
                    }
            );
        });
    }

    private void callUpdateDriverStatus(Long driverId, boolean targetActive, Runnable onOk, Consumer<String> onErr) {
        DriverStatus status = targetActive ? DriverStatus.ACTIVE : DriverStatus.INACTIVE;

        if (driverService == null) {
            onErr.accept("Driver service not available.");
            return;
        }

        driverService.changeDriverStatus(driverId, new DriverStatusUpdate(status))
                .enqueue(new retrofit2.Callback<DriverResponse>() {
                    @Override
                    public void onResponse(Call<DriverResponse> call,Response<DriverResponse> resp) {
                        if (!resp.isSuccessful()) {
                            onErr.accept("Failed (" + resp.code() + ")");
                            return;
                        }
                        onOk.run();
                    }

                    @Override
                    public void onFailure(Call<DriverResponse> call, Throwable t) {
                        onErr.accept("Network error.");
                    }
                });
    }

    private boolean isDriverActive(String driverStatus) {
        if (driverStatus == null) return false;
        String s = driverStatus.trim().toUpperCase();
        return s.equals("ACTIVE");
    }

    private void updateDriverDot(boolean isActive) {
        if (driverStatusDot == null) return;

        driverStatusDot.setVisibility(View.VISIBLE);
        driverStatusDot.setBackgroundResource(
                isActive ? R.drawable.bg_status_dot_green : R.drawable.bg_status_dot_red
        );
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
        }else if(itemId == R.id.nav_add_driver){
            navigateToAddDriver();
        }else if (itemId == R.id.nav_usage) {
            navigateToUsageReports();
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
                startActivity(new Intent(this, PassengerRideHistoryActivity.class));
                break;
            case ADMIN:
                startActivity(new Intent(this, AdminRideHistoryActivity.class));
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

    protected void navigateToUsageReports() {
        startActivity(new Intent(this, UsageReportsActivity.class).putExtra(UsageReportsActivity.EXTRA_IS_ADMIN, authManager.getRole().equals(UserRole.ADMIN)));
    }

    protected void navigateToAddDriver() {
        startActivity(new Intent(this, AddDriverActivity.class));
    }
}
