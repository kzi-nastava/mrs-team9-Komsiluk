package com.komsiluk.taxi.ui.profile;

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

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.ActivityProfileBinding;
import com.komsiluk.taxi.ui.profile.fragments.CarProfileFragment;
import com.komsiluk.taxi.ui.profile.fragments.ProfileDetailsFragment;
import com.komsiluk.taxi.ui.profile.fragments.ProfileSidebarBottomFragment;
import com.komsiluk.taxi.ui.profile.fragments.ProfileSidebarTopFragment;

public class ProfileActivity extends AppCompatActivity implements ProfileDetailsFragment.Host, CarProfileFragment.Host {

    private boolean isDriver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
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

        String role = getIntent().getStringExtra("role");
        isDriver = "driver".equals(role);

        getSupportFragmentManager().beginTransaction().replace(R.id.details_panel, ProfileDetailsFragment.newInstance(isDriver)).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.details_panel, ProfileDetailsFragment.newInstance(isDriver)).replace(R.id.sidebar_top_container, ProfileSidebarTopFragment.newInstance()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.sidebar_bottom_container, ProfileSidebarBottomFragment.newInstance(isDriver)).commit();
    }

    @Override
    public void onEditProfileClicked(boolean isDriver) {
        //
        // if (isDriver) startActivity(new Intent(this, DriverEditActivity.class));
        // else startActivity(new Intent(this, UserEditActivity.class));
    }

    @Override
    public void onCarClicked() {
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.details_panel, CarProfileFragment.newInstance())
                .addToBackStack(null).commit();
    }

    @Override
    public void onBackFromCarProfile() {
        getSupportFragmentManager().popBackStack();
    }
}