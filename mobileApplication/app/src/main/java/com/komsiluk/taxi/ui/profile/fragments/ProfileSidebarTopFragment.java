package com.komsiluk.taxi.ui.profile.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.komsiluk.taxi.R;

public class ProfileSidebarTopFragment extends Fragment {

    public ProfileSidebarTopFragment() {
        // public empty ctor
    }

    public static ProfileSidebarTopFragment newInstance() {
        return new ProfileSidebarTopFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_profile_sidebar_top, container, false);
    }
}