package com.komsiluk.taxi.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import com.komsiluk.taxi.ui.profile.fragments.EditDriverProfileFragment;
import com.komsiluk.taxi.ui.profile.fragments.EditUserProfileFragment;

public class EditProfileActivity extends BaseNavDrawerActivity {

    public static final String EXTRA_IS_DRIVER = "extra_is_driver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDriver = getIntent().getBooleanExtra(EXTRA_IS_DRIVER, false);
        Fragment fragment = isDriver ? new EditDriverProfileFragment() : new EditUserProfileFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.editContentContainer, fragment)
                .commit();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_edit_profile;
    }
}
