package com.komsiluk.taxi.ui.add_driver;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.auth.rider_registration.RiderRegistrationFragment;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddDriverActivity extends BaseNavDrawerActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_add_driver;
    }

    @Override
    protected boolean shouldShowBottomNav() {
        return false;
    }

    @Override
    protected int getDrawerMenuResId() {
        return R.menu.menu_admin_drawer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            RiderRegistrationFragment f = RiderRegistrationFragment.newInstanceAdminAddDriver();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.addDriverFragmentContainer, f)
                    .commit();
        }
    }

    @Override
    protected void handleDrawerItemClick(int itemId) {
        super.handleDrawerItemClick(itemId);
    }

    public void goToUserStepAndReset() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.addDriverFragmentContainer, new RiderRegistrationFragment())
                .commit();
    }
}
