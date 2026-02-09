package com.komsiluk.taxi.ui.profile.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.profile.ChangePasswordActivity;
import com.komsiluk.taxi.ui.profile.ProfileViewModel;
import com.komsiluk.taxi.util.FileUtils;

import java.io.File;

public class ProfileSidebarTopFragment extends Fragment {

    private static final long MAX_IMAGE_BYTES = 8L * 1024L * 1024L;

    private ProfileViewModel viewModel;
    private ActivityResultLauncher<String> imagePickerLauncher;

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

        TextView btnChangeImage = view.findViewById(R.id.btnChangeImage);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        ImageView imgAvatar = view.findViewById(R.id.imgAvatar);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {

                if (uri == null) return;

                String type = requireContext().getContentResolver().getType(uri);
                if (type == null || !type.startsWith("image/")) {
                    Toast.makeText(requireContext(), getString(R.string.select_image_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                File file;
                try {
                    file = FileUtils.from(requireContext(), uri);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Unable to read image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (file.length() > MAX_IMAGE_BYTES) {
                    Toast.makeText(requireContext(), getString(R.string.image_too_large), Toast.LENGTH_SHORT).show();
                    return;
                }

                imgAvatar.setImageURI(uri);

                viewModel.updateProfileImage(file);
            }
        );

        btnChangeImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        viewModel.getProfile().observe(getViewLifecycleOwner(), dto -> {

            if (dto == null) return;

            String first = safe(dto.getFirstName());
            String last  = safe(dto.getLastName());
            String fullName = (first + " " + last).trim();

            tvName.setText(fullName.equals("- -") ? "-" : fullName);
            tvEmail.setText(safe(dto.getEmail()));

            renderAvatar(imgAvatar, dto.getProfileImageUrl(), false);
        });

        viewModel.getImageVersion().observe(getViewLifecycleOwner(), v -> {
            if (v == null || v == 0L) return;

            var dto = viewModel.getProfile().getValue();
            if (dto == null) return;

            renderAvatar(imgAvatar, dto.getProfileImageUrl(), true);
        });

        viewModel.getImageUploadSuccess().observe(getViewLifecycleOwner(), event -> {
            String msg = event.getContentIfNotHandled();
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getImageUploadError().observe(getViewLifecycleOwner(), event -> {
            String msg = event.getContentIfNotHandled();
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    private void renderAvatar(ImageView imgAvatar, String relativeUrl, boolean bustCache) {
        String url = viewModel.buildAbsoluteImageUrl(relativeUrl, bustCache);

        if (url == null) {
            imgAvatar.setImageResource(R.drawable.ic_launcher_foreground);
            return;
        }

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(imgAvatar);
    }
}
