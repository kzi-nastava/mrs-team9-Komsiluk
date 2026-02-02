package com.komsiluk.taxi.ui.profile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.komsiluk.taxi.data.remote.profile.ProfileChangeRequestCreate;
import com.komsiluk.taxi.data.remote.profile.UserProfileResponse;
import com.komsiluk.taxi.ui.profile.DriverEditRequestViewModel;
import com.komsiluk.taxi.ui.shared.InfoMessageActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditDriverProfileFragment extends Fragment {

    private EditText etFirstname, etLastname, etAddress, etCity, etPhone;
    private EditText etModel, etLicence, etSeats;
    private AutoCompleteTextView etType;

    private TextView errFirstname, errLastname, errAddress, errCity, errPhone;
    private TextView errModel, errType, errLicence, errSeats;

    private CheckBox cbPetFriendly, cbChildSeat;

    private DriverEditRequestViewModel vm;
    private boolean prefilled = false;

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

        ArrayAdapter<CharSequence> typeAdapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.car_types, android.R.layout.simple_list_item_1);
        etType.setAdapter(typeAdapter);
        etType.setOnClickListener(view -> etType.showDropDown());

        vm = new ViewModelProvider(requireActivity()).get(DriverEditRequestViewModel.class);

        // PREFILL
        vm.getCurrentProfile().observe(getViewLifecycleOwner(), dto -> {
            if (dto == null || prefilled) return;

            etFirstname.setText(nullToEmpty(dto.getFirstName()));
            etLastname.setText(nullToEmpty(dto.getLastName()));
            etAddress.setText(nullToEmpty(dto.getAddress()));
            etCity.setText(nullToEmpty(dto.getCity()));
            etPhone.setText(nullToEmpty(dto.getPhoneNumber()));

            if (dto.getVehicle() != null) {
                etModel.setText(nullToEmpty(dto.getVehicle().getModel()));
                etLicence.setText(nullToEmpty(dto.getVehicle().getLicencePlate()));
                etSeats.setText(dto.getVehicle().getSeatCount() > 0 ? String.valueOf(dto.getVehicle().getSeatCount()) : "");

                if (dto.getVehicle().getType() != null) {
                    etType.setText(dto.getVehicle().getType().toString(), false);
                }

                cbChildSeat.setChecked(dto.getVehicle().getBabyFriendly());
                cbPetFriendly.setChecked(dto.getVehicle().getPetFriendly());
            }

            prefilled = true;
        });

        vm.getErrorMessageEvent().observe(getViewLifecycleOwner(), event -> {
            String msg = event.getContentIfNotHandled();
            if (msg == null) return;
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });

        vm.getSubmitSuccessEvent().observe(getViewLifecycleOwner(), event -> {
            if (event.getContentIfNotHandled() == null) return;

            Context ctx = requireContext();
            Intent i = InfoMessageActivity.createIntent(
                    ctx,
                    getString(R.string.profile_update_submitted_title),
                    getString(R.string.profile_update_submitted_body),
                    getString(R.string.btn_done)
            );
            startActivity(i);
            requireActivity().finish();
        });

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
            if (!validateAll()) return;

            ProfileChangeRequestCreate req = new ProfileChangeRequestCreate();

            req.newName = trimOrNull(etFirstname.getText().toString());
            req.newSurname = trimOrNull(etLastname.getText().toString());
            req.newAddress = trimOrNull(etAddress.getText().toString());
            req.newCity = trimOrNull(etCity.getText().toString());
            req.newPhoneNumber = trimOrNull(etPhone.getText().toString());

            req.newModel = trimOrNull(etModel.getText().toString());
            req.newType = mapTypeToEnum(etType.getText().toString());
            req.newLicencePlate = trimOrNull(etLicence.getText().toString());

            req.newSeatCount = parseIntOrNull(etSeats.getText().toString());

            req.newPetFriendly = cbPetFriendly.isChecked();
            req.newBabyFriendly = cbChildSeat.isChecked();

            vm.submitRequest(req);
        });

        vm.fetchProfile();

        return v;
    }

    private String nullToEmpty(String s) { return s == null ? "" : s; }
    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? "" : t;
    }
    private Integer parseIntOrNull(String s) {
        try {
            String t = s.trim();
            if (t.isEmpty()) return null;
            return Integer.parseInt(t);
        } catch (Exception e) {
            return null;
        }
    }

    private interface Validator { @Nullable String validate(String value); }

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

    private String mapTypeToEnum(String uiValue) {
        String t = uiValue.trim().toUpperCase();
        if (t.equals("LUXURY")) return "LUXURY";
        if (t.equals("STANDARD")) return "STANDARD";
        if (t.equals("VAN")) return "VAN";
        return t;
    }

    private boolean validateAll() {
        boolean ok = true;

        String e;

        e = validateFirstname(etFirstname.getText().toString());
        if (e != null) { showError(etFirstname, errFirstname, e); ok = false; }

        e = validateLastname(etLastname.getText().toString());
        if (e != null) { showError(etLastname, errLastname, e); ok = false; }

        e = validateAddress(etAddress.getText().toString());
        if (e != null) { showError(etAddress, errAddress, e); ok = false; }

        e = validateCity(etCity.getText().toString());
        if (e != null) { showError(etCity, errCity, e); ok = false; }

        e = validatePhone(etPhone.getText().toString());
        if (e != null) { showError(etPhone, errPhone, e); ok = false; }

        e = validateModel(etModel.getText().toString());
        if (e != null) { showError(etModel, errModel, e); ok = false; }

        e = validateType(etType.getText().toString());
        if (e != null) { showError(etType, errType, e); ok = false; }

        e = validateLicence(etLicence.getText().toString());
        if (e != null) { showError(etLicence, errLicence, e); ok = false; }

        e = validateSeats(etSeats.getText().toString());
        if (e != null) { showError(etSeats, errSeats, e); ok = false; }

        return ok;
    }

    private @Nullable String validateFirstname(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_firstname_required);
        if (t.length() > 30) return getString(R.string.err_firstname_too_long);
        return null;
    }

    private @Nullable String validateLastname(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_lastname_required);
        if (t.length() > 30) return getString(R.string.err_lastname_too_long);
        return null;
    }

    private @Nullable String validateAddress(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_address_required);
        if (t.length() > 100) return getString(R.string.err_address_too_long);
        return null;
    }

    private @Nullable String validateCity(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_city_required);
        if (t.length() > 50) return getString(R.string.err_city_too_long);
        return null;
    }

    private @Nullable String validatePhone(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_phone_required);

        String noSpaces = t.replace(" ", "");
        if (!noSpaces.matches("^\\+?[0-9]{8,15}$")) {
            return getString(R.string.err_phone_invalid);
        }
        return null;
    }

    private @Nullable String validateModel(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_model_required);
        if (t.length() > 50) return getString(R.string.err_model_too_long);
        return null;
    }

    private @Nullable String validateType(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_type_required);
        return null;
    }

    private @Nullable String validateLicence(String s) {
        String t = s.trim().replace(" ", "");
        if (t.isEmpty()) return getString(R.string.err_licence_required);

        if (!t.matches("^[A-Z0-9\\-]{3,15}$")) {
            return getString(R.string.err_licence_invalid);
        }
        return null;
    }

    private @Nullable String validateSeats(String s) {
        String t = s.trim();
        if (t.isEmpty()) return getString(R.string.err_seats_required);

        try {
            int n = Integer.parseInt(t);
            if (n < 1 || n > 8) return getString(R.string.err_seats_invalid);
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
