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

public class CarProfileFragment extends Fragment {

    private boolean isDriver = true;

    public interface Host {
        void onBackFromCarProfile();
        void onEditProfileClicked(boolean isDriver);
    }

    private Host host;

    public static CarProfileFragment newInstance() {
        return new CarProfileFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Host) {
            host = (Host) context;
        } else {
            throw new IllegalStateException(
                    "CarProfileFragment host must implement CarProfileFragment.Host");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnBack = view.findViewById(R.id.btnBack);
        MaterialButton btnEditCar = view.findViewById(R.id.btnEditCar);

        btnBack.setOnClickListener(v -> {
            if (host != null) {
                host.onBackFromCarProfile();
            }
        });

        btnEditCar.setOnClickListener(v -> {
            if (host != null) {
                host.onEditProfileClicked(isDriver);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        host = null;
    }
}