package com.komsiluk.taxi.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import com.komsiluk.taxi.ui.profile.fragments.CarProfileFragment;
import com.komsiluk.taxi.ui.profile.fragments.ProfileDetailsFragment;
import com.komsiluk.taxi.ui.profile.fragments.ProfileSidebarBottomFragment;
import com.komsiluk.taxi.ui.profile.fragments.ProfileSidebarTopFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileActivity extends BaseNavDrawerActivity
        implements ProfileDetailsFragment.Host, CarProfileFragment.Host {

    private boolean isDriver = false;
    @Inject
    AuthManager authManager;
    @Override
    protected int getContentLayoutId() {
        // inserting activity layout
        return R.layout.activity_profile;
    }

    @Override
    protected int getDrawerMenuResId() {
        if (authManager.getRole().equals(UserRole.DRIVER)) {
            return R.menu.menu_driver_drawer;
        } else if(authManager.getRole().equals(UserRole.ADMIN)){
            return R.menu.menu_admin_drawer;
        }
        return R.menu.menu_app_drawer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isDriver = authManager.getRole().equals(UserRole.DRIVER);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.details_panel, ProfileDetailsFragment.newInstance(isDriver))
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.sidebar_top_container, ProfileSidebarTopFragment.newInstance())
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.sidebar_bottom_container, ProfileSidebarBottomFragment.newInstance(isDriver))
                .commit();
    }

    @Override
    public void onEditProfileClicked(boolean isDriver) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra(EditProfileActivity.EXTRA_IS_DRIVER, isDriver);
        startActivity(intent);
    }

    @Override
    public void onCarClicked() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.details_panel, CarProfileFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackFromCarProfile() {
        getSupportFragmentManager().popBackStack();
    }
}
