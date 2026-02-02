package com.komsiluk.taxi.ui.profile.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.profile.ChangePasswordActivity;
import com.komsiluk.taxi.ui.profile.ProfileViewModel;

public class ProfileSidebarTopFragment extends Fragment {

    private ProfileViewModel viewModel;

    public static ProfileSidebarTopFragment newInstance() {
        return new ProfileSidebarTopFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_sidebar_top, container, false);

        TextView btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        ImageView imgAvatar = view.findViewById(R.id.imgAvatar);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        viewModel.getProfile().observe(getViewLifecycleOwner(), dto -> {
            if (dto == null) return;

            String first = safe(dto.getFirstName());
            String last  = safe(dto.getLastName());
            String fullName = (first + " " + last).trim();

            tvName.setText(fullName.equals("- -") ? "-" : fullName);
            tvEmail.setText(safe(dto.getEmail()));

            String url = buildImageUrl(dto.getProfileImageUrl());
            if (url == null) {
                imgAvatar.setImageResource(R.drawable.ic_launcher_foreground);
                return;
            }

            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imgAvatar);
        });

        return view;
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    private String buildImageUrl(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.trim().isEmpty()) return null;

        String p = profileImageUrl.trim();
        String base = "http://" + BuildConfig.IP_ADDR + ":8081";

        return base + p;
    }
}