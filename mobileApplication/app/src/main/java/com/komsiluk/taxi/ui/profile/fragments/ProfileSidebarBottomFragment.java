package com.komsiluk.taxi.ui.profile.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.komsiluk.taxi.MainActivity;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.driver.history.DriverHistoryActivity;
import com.komsiluk.taxi.ui.shared.InfoMessageActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileSidebarBottomFragment extends Fragment {

    private static final String ARG_IS_DRIVER = "is_driver";
    @Inject
    AuthManager authManager;
    public static ProfileSidebarBottomFragment newInstance(boolean isDriver) {
        ProfileSidebarBottomFragment f = new ProfileSidebarBottomFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_DRIVER, isDriver);
        f.setArguments(args);
        return f;
    }

    private boolean isDriver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isDriver = getArguments().getBoolean(ARG_IS_DRIVER, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_sidebar_bottom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvActiveToday = view.findViewById(R.id.tvActiveToday);
        View btnLogout = view.findViewById(R.id.btnLogout);

        if (!isDriver) {
            tvActiveToday.setVisibility(View.INVISIBLE);
        }

        view.findViewById(R.id.btnRideHistory)
                .setOnClickListener(v ->{
                        Intent i = new Intent(getContext(), DriverHistoryActivity.class);
                        startActivity(i);
                });

        btnLogout.setOnClickListener(v ->{
            Intent i = InfoMessageActivity.createLogoutIntent(
                    this.getContext(),
                    getString(R.string.logout_title),
                    getString(R.string.logout_message),
                    getString(R.string.logout_button)
            );
            startActivity(i);
        });
    }
}