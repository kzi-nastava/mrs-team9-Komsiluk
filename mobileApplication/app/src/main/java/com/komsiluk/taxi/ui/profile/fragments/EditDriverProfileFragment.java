package com.komsiluk.taxi.ui.profile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.shared.InfoMessageActivity;

public class EditDriverProfileFragment extends Fragment {

    private EditText etFirstname, etLastname, etAddress, etCity, etPhone;
    private EditText etModel, etLicence, etSeats;
    private AutoCompleteTextView etType;
    private TextView errFirstname, errLastname, errAddress, errCity, errPhone;
    private TextView errModel, errType, errLicence, errSeats;
    private CheckBox cbPetFriendly, cbChildSeat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_edit_driver_profile, container, false);

        etFirstname = v.findViewById(R.id.etFirstname);
        etLastname  = v.findViewById(R.id.etLastname);
        etAddress   = v.findViewById(R.id.etAddress);
        etCity      = v.findViewById(R.id.etCity);
        etPhone     = v.findViewById(R.id.etPhone);

        etModel     = v.findViewById(R.id.etModel);
        etType      = v.findViewById(R.id.etType);
        etLicence   = v.findViewById(R.id.etLicence);
        etSeats     = v.findViewById(R.id.etSeats);

        errFirstname = v.findViewById(R.id.tvErrorFirstname);
        errLastname  = v.findViewById(R.id.tvErrorLastname);
        errAddress   = v.findViewById(R.id.tvErrorAddress);
        errCity      = v.findViewById(R.id.tvErrorCity);
        errPhone     = v.findViewById(R.id.tvErrorPhone);

        errModel     = v.findViewById(R.id.tvErrorModel);
        errType      = v.findViewById(R.id.tvErrorType);
        errLicence   = v.findViewById(R.id.tvErrorLicence);
        errSeats     = v.findViewById(R.id.tvErrorSeats);

        cbPetFriendly = v.findViewById(R.id.cbPetFriendly);
        cbChildSeat   = v.findViewById(R.id.cbChildSeat);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.car_types, android.R.layout.simple_list_item_1);
        etType.setAdapter(typeAdapter);
        etType.setOnClickListener(view -> etType.showDropDown());

        // watchers
        addWatcher(etFirstname, errFirstname, this::validateFirstname);
        addWatcher(etLastname,  errLastname,  this::validateLastname);
        addWatcher(etAddress,   errAddress,   this::validateAddress);
        addWatcher(etCity,      errCity,      this::validateCity);
        addWatcher(etPhone,     errPhone,     this::validatePhone);

        addWatcher(etModel,     errModel,     this::validateModel);
        addWatcher(etType,      errType,      this::validateType);
        addWatcher(etLicence,   errLicence,   this::validateLicence);
        addWatcher(etSeats,     errSeats,     this::validateSeats);

        v.findViewById(R.id.btnCancel).setOnClickListener(view -> requireActivity().finish());

        v.findViewById(R.id.btnConfirm).setOnClickListener(view -> {
            if (validateAll()) {
                Context ctx = requireContext();
                Intent i = InfoMessageActivity.createIntent(ctx, getString(R.string.profile_update_submitted_title), getString(R.string.profile_update_submitted_body), getString(R.string.btn_done));
                startActivity(i);
                requireActivity().finish();
            }
        });

        return v;
    }

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

        String m = etModel.getText().toString();
        String t = etType.getText().toString();
        String li = etLicence.getText().toString();
        String s = etSeats.getText().toString();

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

        e = validateModel(m);
        if (e != null) { showError(etModel, errModel, e); ok = false; }

        e = validateType(t);
        if (e != null) { showError(etType, errType, e); ok = false; }

        e = validateLicence(li);
        if (e != null) { showError(etLicence, errLicence, e); ok = false; }

        e = validateSeats(s);
        if (e != null) { showError(etSeats, errSeats, e); ok = false; }

        return ok;
    }

    // user fields

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

    // car fields

    private @Nullable String validateModel(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_model_required);
        if (t.length() < 3) return getString(R.string.err_model_short);
        return null;
    }

    private @Nullable String validateType(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_type_required);
        return null;
    }

    private @Nullable String validateLicence(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_licence_required);

        String normalized = t.replace(" ", "");
        if (!normalized.matches("[A-Za-z0-9-]{4,}")) {
            return getString(R.string.err_licence_invalid);
        }
        return null;
    }

    private @Nullable String validateSeats(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_seats_required);

        try {
            int n = Integer.parseInt(t);
            if (n < 1 || n > 8) {
                return getString(R.string.err_seats_invalid);
            }
        } catch (NumberFormatException ex) {
            return getString(R.string.err_seats_invalid);
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

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}