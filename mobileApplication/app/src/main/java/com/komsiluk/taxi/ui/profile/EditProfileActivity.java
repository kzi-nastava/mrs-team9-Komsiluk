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
import androidx.fragment.app.Fragment;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.ActivityChangePasswordBinding;
import com.komsiluk.taxi.databinding.ActivityEditProfileBinding;
import com.komsiluk.taxi.ui.profile.fragments.EditDriverProfileFragment;
import com.komsiluk.taxi.ui.profile.fragments.EditUserProfileFragment;

public class EditProfileActivity extends AppCompatActivity {

    public static final String EXTRA_IS_DRIVER = "extra_is_driver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityEditProfileBinding binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
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

        boolean isDriver = getIntent().getBooleanExtra(EXTRA_IS_DRIVER, false);

        Fragment fragment = isDriver ? new EditDriverProfileFragment() : new EditUserProfileFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.editContentContainer, fragment).commit();
    }
}