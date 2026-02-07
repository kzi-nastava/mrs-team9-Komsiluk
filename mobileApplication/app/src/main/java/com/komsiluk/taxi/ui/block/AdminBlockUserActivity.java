package com.komsiluk.taxi.ui.block;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminBlockUserActivity extends BaseNavDrawerActivity {

    private AutoCompleteTextView actEmail;
    private MaterialButton btnBlock;

    private AdminBlockUserViewModel vm;

    @Inject
    SessionManager sessionManager;

    private boolean pickedFromDropdown = false;
    private String pickedValue = "";

    private boolean suppressWatcher = false;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pending;

    private ArrayAdapter<String> adapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_admin_block_user;
    }

    @Override
    protected int getDrawerMenuResId() {
        return R.menu.menu_admin_drawer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actEmail = findViewById(R.id.actUserEmail);
        btnBlock = findViewById(R.id.btnBlockUser);

        vm = new ViewModelProvider(this).get(AdminBlockUserViewModel.class);

        adapter = new ArrayAdapter<>(this, R.layout.item_email_dropdown, new ArrayList<>());
        actEmail.setAdapter(adapter);
        actEmail.setThreshold(3);

        vm.getState().observe(this, st -> {
            if (st == null) return;

            if (st.blockSuccess) {
                Toast.makeText(this, "User blocked successfully.", Toast.LENGTH_LONG).show();
                resetUi();
                vm.clearSuccessFlag();
                return;
            }

            if (st.error != null) {
                Toast.makeText(this, st.error, Toast.LENGTH_LONG).show();
            }

            if (st.emailSuggestions != null) {
                List<String> list = st.emailSuggestions;

                adapter.clear();
                adapter.addAll(list);
                adapter.notifyDataSetChanged();

                boolean canShow =
                        !pickedFromDropdown
                                && actEmail.hasFocus()
                                && actEmail.getText() != null
                                && actEmail.getText().toString().trim().length() >= 3
                                && list != null
                                && !list.isEmpty();

                if (canShow) {
                    actEmail.post(actEmail::showDropDown);
                } else {
                    actEmail.dismissDropDown();
                }
            }
        });

        actEmail.setOnItemClickListener((parent, view, position, id) -> {
            String chosen = (String) parent.getItemAtPosition(position);

            pickedFromDropdown = true;
            pickedValue = chosen;
            btnBlock.setEnabled(true);

            suppressWatcher = true;
            actEmail.setText(chosen, false);
            actEmail.setSelection(chosen.length());
            actEmail.dismissDropDown();
            suppressWatcher = false;

            cancelPending();
        });

        actEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (suppressWatcher) return;

                String now = s == null ? "" : s.toString();
                String q = now.trim();

                if (!now.equals(pickedValue)) {
                    pickedFromDropdown = false;
                    btnBlock.setEnabled(false);
                }

                cancelPending();

                if (q.length() < 3) {
                    vm.setEmailSuggestions(new ArrayList<>());
                    actEmail.dismissDropDown();
                    return;
                }

                pending = () -> vm.autocompleteEmails(q, 10);
                handler.postDelayed(pending, 250);
            }

            @Override public void afterTextChanged(Editable s) { }
        });

        btnBlock.setEnabled(false);
        btnBlock.setOnClickListener(v -> {
            if (!pickedFromDropdown) return;

            cancelPending();
            actEmail.dismissDropDown();

            String email = actEmail.getText() == null ? "" : actEmail.getText().toString().trim();
            showConfirmDialog(email);
        });
    }

    private void showConfirmDialog(String email) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_admin_block_user, null, false);

        TextView tvMsg = v.findViewById(R.id.tvBlockDialogMsg);
        EditText etReason = v.findViewById(R.id.etBlockReason);

        MaterialButton btnCancel = v.findViewById(R.id.btnBlockCancel);
        MaterialButton btnConfirm = v.findViewById(R.id.btnBlockConfirm);

        tvMsg.setText(getString(R.string.dialog_block_message_fmt, email));

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(v)
                .setCancelable(true)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        btnCancel.setOnClickListener(x -> dialog.dismiss());
        btnConfirm.setOnClickListener(x -> {
            String reason = etReason.getText() == null ? "" : etReason.getText().toString().trim();
            if (reason.length() < 5) {
                Toast.makeText(this, "Reason must be at least 5 characters.", Toast.LENGTH_LONG).show();
                return;
            }

            Long adminId = sessionManager != null ? sessionManager.getUserId() : null;
            if (adminId == null) {
                Toast.makeText(this, "Not logged in.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                return;
            }

            dialog.dismiss();
            vm.blockUser(email, adminId, reason);
        });
    }

    private void resetUi() {
        cancelPending();

        pickedFromDropdown = false;
        pickedValue = "";

        suppressWatcher = true;
        actEmail.setText("", false);
        suppressWatcher = false;

        actEmail.dismissDropDown();
        btnBlock.setEnabled(false);

        vm.setEmailSuggestions(new ArrayList<>());
    }

    private void cancelPending() {
        if (pending != null) handler.removeCallbacks(pending);
        pending = null;
    }
}
