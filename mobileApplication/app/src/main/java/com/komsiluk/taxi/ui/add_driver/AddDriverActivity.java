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
        AddDriverViewModel vm = new androidx.lifecycle.ViewModelProvider(this).get(AddDriverViewModel.class);
        vm.firstName = "";
        vm.lastName = "";
        vm.address = "";
        vm.city = "";
        vm.phone = "";
        vm.email = "";
        vm.profileImageFile = null;
        vm.carModel = "";
        vm.carType = "";
        vm.licencePlate = "";
        vm.seats = 0;
        vm.petFriendly = false;
        vm.childSeat = false;

        RiderRegistrationFragment f = RiderRegistrationFragment.newInstanceAdminAddDriver();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.addDriverFragmentContainer, f)
                .commit();
    }
}
