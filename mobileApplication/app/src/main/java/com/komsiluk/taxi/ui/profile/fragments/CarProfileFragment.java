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
import com.komsiluk.taxi.data.remote.profile.VehicleResponse;
import com.komsiluk.taxi.ui.profile.ProfileViewModel;

public class CarProfileFragment extends Fragment {

    private boolean isDriver = true;

    public interface Host {
        void onBackFromCarProfile();
        void onEditProfileClicked(boolean isDriver);
    }

    private Host host;
    private ProfileViewModel viewModel;

    public static CarProfileFragment newInstance() {
        return new CarProfileFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Host) {
            host = (Host) context;
        } else {
            throw new IllegalStateException("CarProfileFragment host must implement CarProfileFragment.Host");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        TextView tvModel = view.findViewById(R.id.tvValueModel);
        TextView tvType = view.findViewById(R.id.tvValueType);
        TextView tvPlate = view.findViewById(R.id.tvValueLicencePlate);
        TextView tvSeats = view.findViewById(R.id.tvValueSeats);
        TextView tvPet = view.findViewById(R.id.tvPetFriendly);
        TextView tvChild = view.findViewById(R.id.tvChildSeat);

        MaterialButton btnBack = view.findViewById(R.id.btnBack);
        MaterialButton btnEditCar = view.findViewById(R.id.btnEditCar);

        btnBack.setOnClickListener(v -> {
            if (host != null) host.onBackFromCarProfile();
        });

        btnEditCar.setOnClickListener(v -> {
            if (host != null) host.onEditProfileClicked(isDriver);
        });

        viewModel.getProfile().observe(getViewLifecycleOwner(), dto -> {
            if (dto == null) return;

            VehicleResponse v = dto.getVehicle();
            if (v == null) {
                tvModel.setText("-");
                tvType.setText("-");
                tvPlate.setText("-");
                tvSeats.setText("-");
                tvPet.setText(getString(R.string.profile_pet_friendly_fmt, getString(R.string.profile_unknown)));
                tvChild.setText(getString(R.string.profile_child_seat_fmt, getString(R.string.profile_unknown)));
                return;
            }

            tvModel.setText(safe(v.getModel()));
            tvType.setText(safe(v.getType()));
            tvPlate.setText(safe(v.getLicencePlate()));
            tvSeats.setText(v.getSeatCount() == null ? "-" : String.valueOf(v.getSeatCount()));

            tvPet.setText(getString(R.string.profile_pet_friendly_fmt, yesNo(v.getPetFriendly())));
            tvChild.setText(getString(R.string.profile_child_seat_fmt, yesNo(v.getBabyFriendly())));
        });
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }

    private String yesNo(Boolean b) {
        if (b == null) return getString(R.string.profile_unknown);
        return b ? getString(R.string.profile_yes) : getString(R.string.profile_no);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        host = null;
    }
}