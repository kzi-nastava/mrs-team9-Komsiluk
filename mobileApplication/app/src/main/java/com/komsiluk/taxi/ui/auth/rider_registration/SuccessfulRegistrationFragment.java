package com.komsiluk.taxi.ui.auth.rider_registration;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.komsiluk.taxi.databinding.FragmentSuccessfulRegistrationBinding;
import com.komsiluk.taxi.ui.auth.AuthActivity;
import com.komsiluk.taxi.ui.auth.login.ResetPasswordFragment;


public class SuccessfulRegistrationFragment extends Fragment {

    private FragmentSuccessfulRegistrationBinding binding;

    public static SuccessfulRegistrationFragment newInstance(String token) {
        SuccessfulRegistrationFragment fragment = new SuccessfulRegistrationFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentSuccessfulRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);



        binding.btnLogin.setOnClickListener(v -> {
            ((AuthActivity)requireActivity()).showLogin();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
