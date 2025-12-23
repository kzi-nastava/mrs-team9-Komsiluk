package com.komsiluk.taxi.ui.profile.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.komsiluk.taxi.R;

public class EditUserProfileFragment extends Fragment {

    private EditText etFirstname, etLastname, etAddress, etCity, etPhone;
    private TextView errFirstname, errLastname, errAddress, errCity, errPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);

        etFirstname = v.findViewById(R.id.etFirstname);
        etLastname  = v.findViewById(R.id.etLastname);
        etAddress   = v.findViewById(R.id.etAddress);
        etCity      = v.findViewById(R.id.etCity);
        etPhone     = v.findViewById(R.id.etPhone);

        errFirstname = v.findViewById(R.id.tvErrorFirstname);
        errLastname  = v.findViewById(R.id.tvErrorLastname);
        errAddress   = v.findViewById(R.id.tvErrorAddress);
        errCity      = v.findViewById(R.id.tvErrorCity);
        errPhone     = v.findViewById(R.id.tvErrorPhone);

        // validation watchers
        addWatcher(etFirstname, errFirstname, this::validateFirstname);
        addWatcher(etLastname,  errLastname,  this::validateLastname);
        addWatcher(etAddress,   errAddress,   this::validateAddress);
        addWatcher(etCity,      errCity,      this::validateCity);
        addWatcher(etPhone,     errPhone,     this::validatePhone);

        v.findViewById(R.id.btnCancel).setOnClickListener(view -> requireActivity().finish());

        v.findViewById(R.id.btnSave).setOnClickListener(view -> {
            if (validateAll()) {
                Toast.makeText(requireContext(), R.string.edit_profile_success, Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        });

        return v;
    }

    // Helpers
    private interface Validator {
        @Nullable String validate(String value);
    }

    private void addWatcher(EditText field, TextView errorView, Validator validator) {
        field.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String error = validator.validate(s.toString());
                if (error == null) clearError(field, errorView);
                else showError(field, errorView, error);
            }
        });
    }

    private boolean validateAll() {
        boolean ok = true;

        String f = etFirstname.getText().toString();
        String l = etLastname.getText().toString();
        String a = etAddress.getText().toString();
        String c = etCity.getText().toString();
        String p = etPhone.getText().toString();

        String e;

        e = validateFirstname(f);
        if (e != null) { showError(etFirstname, errFirstname, e); ok = false; }

        e = validateLastname(l);
        if (e != null) { showError(etLastname, errLastname, e); ok = false; }

        e = validateAddress(a);
        if (e != null) { showError(etAddress, errAddress, e); ok = false; }

        e = validateCity(c);
        if (e != null) { showError(etCity, errCity, e); ok = false; }

        e = validatePhone(p);
        if (e != null) { showError(etPhone, errPhone, e); ok = false; }

        return ok;
    }

    // Validation rules

    private @Nullable String validateFirstname(String s) {
        if (s.trim().isEmpty()) return getString(R.string.err_firstname_required);
        if (s.trim().length() < 2) return getString(R.string.err_firstname_short);
        return null;
    }

    private @Nullable String validateLastname(String s) {
        if (s.trim().isEmpty()) return getString(R.string.err_lastname_required);
        if (s.trim().length() < 2) return getString(R.string.err_lastname_short);
        return null;
    }

    private @Nullable String validateAddress(String s) {
        if (s.trim().isEmpty()) return getString(R.string.err_address_required);
        return null;
    }

    private @Nullable String validateCity(String s) {
        if (s.trim().isEmpty()) return getString(R.string.err_city_required);
        return null;
    }

    private @Nullable String validatePhone(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_phone_required);

        String noSpaces = t.replace(" ", "");

        if (noSpaces.startsWith("+")) {
            noSpaces = noSpaces.substring(1);
        }

        if (!noSpaces.matches("\\d+")) {
            return getString(R.string.err_phone_invalid);
        }

        if (noSpaces.length() < 6) {
            return getString(R.string.err_phone_invalid);
        }

        return null;
    }

    private void showError(EditText field, TextView errorView, String msg) {
        field.setBackgroundResource(R.drawable.bg_input_error);
        errorView.setText(msg);
        errorView.setVisibility(View.VISIBLE);
    }

    private void clearError(EditText field, TextView errorView) {
        field.setBackgroundResource(R.drawable.bg_input_normal);
        errorView.setText("");
        errorView.setVisibility(View.GONE);
    }

    // helper
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}