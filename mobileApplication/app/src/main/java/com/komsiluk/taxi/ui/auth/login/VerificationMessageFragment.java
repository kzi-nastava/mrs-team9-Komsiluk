package com.komsiluk.taxi.ui.auth.login;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.FragmentVerificationMessageBinding;
import com.komsiluk.taxi.ui.auth.AuthActivity;
import com.komsiluk.taxi.ui.auth.rider_registration.PassengerRegistrationViewModel;


public class VerificationMessageFragment extends Fragment {

    private FragmentVerificationMessageBinding binding;

    private Drawable normalBg;
    private Drawable errorBg;

    private static final String ARG_MESSAGE = "ARG_MESSAGE";

    public static VerificationMessageFragment newInstance(String message) {
        VerificationMessageFragment fragment = new VerificationMessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentVerificationMessageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private String email;

    VerificationMessageViewModel viewModel;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(VerificationMessageViewModel.class);

        if (getArguments() != null) {
            email = getArguments().getString(ARG_MESSAGE);
        }


        binding.btnClose.setOnClickListener(v -> {
            ((AuthActivity) requireActivity())
                    .finish();
        });

        viewModel.getSuccessEvent().observe(getViewLifecycleOwner(), event -> {
            Boolean successMessage = event.getContentIfNotHandled();

            if (successMessage == null) return;

            Toast.makeText(requireActivity(),getString(R.string.auth_resend_message),Toast.LENGTH_SHORT).show();
        });

        viewModel.getErrorMessageEvent().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message == null) return;

            Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_SHORT
            ).show();
        });

        binding.btnResendEmail.setOnClickListener(v -> {

            viewModel.resendEmail(email);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
