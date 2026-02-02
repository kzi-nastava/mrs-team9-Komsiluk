package com.komsiluk.taxi.ui.profile.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.profile.ProfileViewModel;

public class ProfileDetailsFragment extends Fragment {

    private static final String ARG_IS_DRIVER = "is_driver";

    private boolean isDriver = false;

    public interface Host {
        void onEditProfileClicked(boolean isDriver);
        void onCarClicked();
    }

    private Host host;
    private ProfileViewModel viewModel;

    public static ProfileDetailsFragment newInstance(boolean isDriver) {
        ProfileDetailsFragment f = new ProfileDetailsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_DRIVER, isDriver);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Host) {
            host = (Host) context;
        }
    }

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
        return inflater.inflate(R.layout.fragment_profile_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        TextView tvFirst = view.findViewById(R.id.tvValueFirstname);
        TextView tvLast = view.findViewById(R.id.tvValueLastname);
        TextView tvAddr = view.findViewById(R.id.tvValueAddress);
        TextView tvCity = view.findViewById(R.id.tvValueCity);
        TextView tvPhone = view.findViewById(R.id.tvValuePhone);

        MaterialButton btnCar = view.findViewById(R.id.btnCar);
        MaterialButton btnEdit = view.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(v -> {
            if (host != null) host.onEditProfileClicked(isDriver);
        });

        if (isDriver) {
            btnCar.setVisibility(View.VISIBLE);
            btnCar.setOnClickListener(v -> {
                if (host != null) host.onCarClicked();
            });
        } else {
            btnCar.setVisibility(View.GONE);
        }

        viewModel.getProfile().observe(getViewLifecycleOwner(), dto -> {
            if (dto == null) return;
            tvFirst.setText(safe(dto.getFirstName()));
            tvLast.setText(safe(dto.getLastName()));
            tvAddr.setText(safe(dto.getAddress()));
            tvCity.setText(safe(dto.getCity()));
            tvPhone.setText(safe(dto.getPhoneNumber()));
        });
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        host = null;
    }
}