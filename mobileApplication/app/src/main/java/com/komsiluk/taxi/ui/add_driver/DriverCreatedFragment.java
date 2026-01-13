package com.komsiluk.taxi.ui.add_driver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.FragmentDriverCreatedBinding;
import com.komsiluk.taxi.ui.auth.rider_registration.RiderRegistrationFragment;

public class DriverCreatedFragment extends Fragment {

    private FragmentDriverCreatedBinding binding;

    public static DriverCreatedFragment newInstance() {
        return new DriverCreatedFragment();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentDriverCreatedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnDone.setOnClickListener(v -> {
            if (requireActivity() instanceof AddDriverActivity) {
                ((AddDriverActivity) requireActivity()).goToUserStepAndReset();
            } else {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.addDriverFragmentContainer, new RiderRegistrationFragment())
                        .commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
