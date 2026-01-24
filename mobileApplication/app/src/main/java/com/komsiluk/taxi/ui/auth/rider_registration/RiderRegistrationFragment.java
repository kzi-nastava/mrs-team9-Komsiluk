package com.komsiluk.taxi.ui.auth.rider_registration;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.FragmentRiderRegistrationBinding;
import com.komsiluk.taxi.ui.add_driver.AddDriverViewModel;
import com.komsiluk.taxi.ui.add_driver.DriverVehicleFragment;



import java.io.File;

import com.komsiluk.taxi.ui.auth.AuthActivity;
import com.komsiluk.taxi.ui.auth.login.ForgotPasswordMessageFragment;
import com.komsiluk.taxi.ui.auth.login.VerificationMessageFragment;
import com.komsiluk.taxi.util.FileUtils;

public class RiderRegistrationFragment extends Fragment {

    private static final String NAME_REGEX =
            "^[\\p{L}][\\p{L}\\s'-]{1,49}$";

    private static final String PHONE_REGEX =
            "^\\+?\\d{7,15}$";

    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$";


    private static final String ARG_ADMIN_ADD_DRIVER = "ARG_ADMIN_ADD_DRIVER";

    private ActivityResultLauncher<String> imagePickerLauncher;

    private FragmentRiderRegistrationBinding binding;
    private Drawable normalBg;
    private Drawable errorBg;

    private boolean isAdminAddDriver;
    private AddDriverViewModel vm;

    private PassengerRegistrationViewModel prViewModel;

    public static RiderRegistrationFragment newInstanceAdminAddDriver() {
        RiderRegistrationFragment f = new RiderRegistrationFragment();
        Bundle b = new Bundle();
        b.putBoolean(ARG_ADMIN_ADD_DRIVER, true);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentRiderRegistrationBinding.inflate(inflater, container, false);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) return;

                    // validacija
                    String type = requireContext().getContentResolver().getType(uri);
                    if (type == null || !type.startsWith("image/")) {
                        Toast.makeText(
                                requireContext(),
                                getString(R.string.select_image_error),
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    File file = FileUtils.from(requireContext(), uri);
                    if (file.length() > 8 * 1024 * 1024) {
                        Toast.makeText(
                                requireContext(),
                                getString(R.string.image_too_large),
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    // sacuvaj u ViewModel
                    prViewModel.profileImageFile = file;

                    // preview
                    binding.ivProfilePhoto.setImageURI(uri);
                    binding.tvChoosePhoto.setText(file.getName());
                }
        );


        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isAdminAddDriver = getArguments() != null && getArguments().getBoolean(ARG_ADMIN_ADD_DRIVER, false);
        if (isAdminAddDriver) {
            vm = new ViewModelProvider(requireActivity()).get(AddDriverViewModel.class);
        } else {
            prViewModel = new ViewModelProvider(requireActivity()).get(PassengerRegistrationViewModel.class);
        }

        normalBg = requireContext().getDrawable(R.drawable.bg_input_normal);
        errorBg  = requireContext().getDrawable(R.drawable.bg_input_error);

        if (isAdminAddDriver) {
            binding.tvTitle.setText(getString(R.string.admin_add_driver_title)); // dodaj string
            binding.btnSubmit.setText(getString(R.string.btn_next));             // dodaj string

            binding.tvLabelPassword.setVisibility(View.GONE);
            binding.etPassword.setVisibility(View.GONE);
            binding.tvErrorPassword.setVisibility(View.GONE);

            binding.tvLabelRepeat.setVisibility(View.GONE);
            binding.etRepeat.setVisibility(View.GONE);
            binding.tvErrorRepeat.setVisibility(View.GONE);
        }

        // Binding za firstName
        binding.etFirstname.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isAdminAddDriver) {
                    vm.firstName = s.toString();
                } else {
                    prViewModel.firstName.setValue(s.toString());
                }
                validateFirstname();
            }
        });

        // Binding za lastName
        binding.etLastname.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isAdminAddDriver) {
                    vm.lastName = s.toString();
                } else {
                    prViewModel.lastName.setValue(s.toString());
                }
                validateLastname();
            }
        });

        // Binding za address
        binding.etAddress.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isAdminAddDriver) {
                    vm.address = s.toString();
                } else {
                    prViewModel.address.setValue(s.toString());
                }
                validateAddress();
            }
        });

        // Binding za city
        binding.etCity.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isAdminAddDriver) {
                    vm.city = s.toString();
                } else {
                    prViewModel.city.setValue(s.toString());
                }
                validateCity();
            }
        });

        // Binding za phone
        binding.etPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isAdminAddDriver) {
                    vm.phone = s.toString();
                } else {
                    prViewModel.phone.setValue(s.toString());
                }
                validatePhone();
            }
        });

        // Binding za email
        binding.etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isAdminAddDriver) {
                    vm.email = s.toString();
                } else {
                    prViewModel.email.setValue(s.toString());
                }
                validateEmail();
            }
        });

        // Binding za password (samo ako nije admin add driver)
        if (!isAdminAddDriver) {
            binding.etPassword.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    prViewModel.password.setValue(s.toString());
                    validatePassword();
                }
            });

            binding.etRepeat.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    prViewModel.repeat.setValue(s.toString());
                    validateRepeat();
                }
            });
        }

        binding.tvChoosePhoto.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });


        prViewModel.getSuccessMessageEvent().observe(getViewLifecycleOwner(), event -> {
            String successMessage = event.getContentIfNotHandled();

            if (successMessage == null) return;

            VerificationMessageFragment verificationFragment = VerificationMessageFragment.newInstance(binding.etEmail.getText().toString().trim());
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.authFragmentContainer, verificationFragment)
                    .commit();
            Toast.makeText(
                    requireContext(),
                    successMessage,
                    Toast.LENGTH_SHORT
            ).show();
        });

        prViewModel.getErrorMessageEvent().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message == null) return;

            Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_SHORT
            ).show();
        });




        binding.btnSubmit.setOnClickListener(v -> {
            if (!validateAll()) return;

            if (isAdminAddDriver) {

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.addDriverFragmentContainer, new DriverVehicleFragment())
                        .addToBackStack("add_driver_step1")
                        .commit();
            } else {
                prViewModel.submit();
            }
        });
    }

    private boolean validateAll() {
        boolean ok =  validateFirstname() &
                validateLastname() &
                validateAddress() &
                validateCity() &
                validatePhone() &
                validateEmail();

        if (!isAdminAddDriver) {
            ok = ok & validatePassword() & validateRepeat();
        }
        return ok;
    }

    private boolean validateFirstname() {
        String v = binding.etFirstname.getText().toString().trim();

        if (v.isEmpty()) {
            showError(binding.etFirstname, binding.tvErrorFirstname,
                    getString(R.string.err_firstname_required));
            return false;
        }
        if (!v.matches(NAME_REGEX)) {
            showError(binding.etFirstname, binding.tvErrorFirstname,
                    getString(R.string.err_firstname_invalid));
            return false;
        }
        clearError(binding.etFirstname, binding.tvErrorFirstname);
        return true;
    }

    private boolean validateLastname() {
        String v = binding.etLastname.getText().toString().trim();

        if (v.isEmpty()) {
            showError(binding.etLastname, binding.tvErrorLastname,
                    getString(R.string.err_lastname_required));
            return false;
        }
        if (!v.matches(NAME_REGEX)) {
            showError(binding.etLastname, binding.tvErrorLastname,
                    getString(R.string.err_lastname_invalid));
            return false;
        }
        clearError(binding.etLastname, binding.tvErrorLastname);
        return true;
    }


    private boolean validateAddress() {
        String v = binding.etAddress.getText().toString().trim();

        if (v.length() < 5) {
            showError(binding.etAddress, binding.tvErrorAddress,
                    getString(R.string.err_address_required));
            return false;
        }
        clearError(binding.etAddress, binding.tvErrorAddress);
        return true;
    }


    private boolean validateCity() {
        String v = binding.etCity.getText().toString().trim();

        if (v.length() < 2) {
            showError(binding.etCity, binding.tvErrorCity,
                    getString(R.string.err_city_required));
            return false;
        }
        clearError(binding.etCity, binding.tvErrorCity);
        return true;
    }


    private boolean validatePhone() {
        String phone = binding.etPhone.getText().toString().trim();

        if (!phone.matches(PHONE_REGEX)) {
            showError(binding.etPhone, binding.tvErrorPhone,
                    getString(R.string.err_phone_invalid));
            return false;
        }
        clearError(binding.etPhone, binding.tvErrorPhone);
        return true;
    }


    private boolean validateEmail() {
        String email = binding.etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            showError(binding.etEmail, binding.tvErrorEmail, getString(R.string.error_email_required));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(binding.etEmail, binding.tvErrorEmail, getString(R.string.error_email_invalid));
            return false;
        }
        clearError(binding.etEmail, binding.tvErrorEmail);
        return true;
    }

    private boolean validatePassword() {
        String pwd = binding.etPassword.getText().toString();

        if (!pwd.matches(PASSWORD_REGEX)) {
            showError(
                    binding.etPassword,
                    binding.tvErrorPassword,
                    getString(R.string.error_password_invalid_format)
            );
            return false;
        }
        clearError(binding.etPassword, binding.tvErrorPassword);
        return true;
    }


    private boolean validateRepeat() {
        String pwd = binding.etPassword.getText().toString();
        String rep = binding.etRepeat.getText().toString();

        if (rep.isEmpty()) {
            showError(binding.etRepeat, binding.tvErrorRepeat,
                    getString(R.string.error_repeat_required));
            return false;
        }
        if (!rep.equals(pwd)) {
            showError(binding.etRepeat, binding.tvErrorRepeat,
                    getString(R.string.error_repeat_mismatch));
            return false;
        }
        clearError(binding.etRepeat, binding.tvErrorRepeat);
        return true;
    }


    private boolean validateText(View field, View errorView, String value, String error, int minLen) {
        if (value.isEmpty() || value.length() < minLen) {
            showError(field, errorView, error);
            return false;
        }
        clearError(field, errorView);
        return true;
    }

    private void showError(View field, View errorView, String msg) {
        field.setBackground(errorBg);
        if (errorView instanceof android.widget.TextView) {
            ((android.widget.TextView) errorView).setText(msg);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void clearError(View field, View errorView) {
        field.setBackground(normalBg);
        errorView.setVisibility(View.GONE);
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
