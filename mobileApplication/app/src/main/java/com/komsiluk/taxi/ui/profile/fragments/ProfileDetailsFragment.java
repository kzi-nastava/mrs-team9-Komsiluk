package com.komsiluk.taxi.ui.profile.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.R;

public class ProfileDetailsFragment extends Fragment {

    private static final String ARG_IS_DRIVER = "is_driver";

    private boolean isDriver = false;

    public interface Host {
        void onEditProfileClicked(boolean isDriver);
        void onCarClicked();
    }

    private Host host;

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

        MaterialButton btnCar  = view.findViewById(R.id.btnCar);
        MaterialButton btnEdit = view.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(v -> {
            if (host != null) {
                host.onEditProfileClicked(isDriver);
            }
        });

        if (isDriver) {
            btnCar.setVisibility(View.VISIBLE);
            btnCar.setOnClickListener(v -> {
                if (host != null) {
                    host.onCarClicked();
                }
            });
        } else {
            btnCar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        host = null;
    }
}