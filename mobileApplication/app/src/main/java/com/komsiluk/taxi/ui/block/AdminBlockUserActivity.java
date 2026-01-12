package com.komsiluk.taxi.ui.block;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;
import java.util.Locale;

public class AdminBlockUserActivity extends BaseNavDrawerActivity {

    private AutoCompleteTextView actEmail;
    private MaterialButton btnBlock;

    private boolean pickedFromDropdown = false;
    private String pickedValue = "";

    private final ArrayList<String> mockEmails = new ArrayList<>();

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

        seedMockEmails();


        android.widget.ArrayAdapter<String> adapter =
                new android.widget.ArrayAdapter<>(this, R.layout.item_email_dropdown, mockEmails);

        actEmail.setAdapter(adapter);

        actEmail.setThreshold(3);

        actEmail.setOnItemClickListener((parent, view, position, id) -> {
            String chosen = (String) parent.getItemAtPosition(position);
            pickedFromDropdown = true;
            pickedValue = chosen;
            btnBlock.setEnabled(true);
        });

        actEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String now = s == null ? "" : s.toString();
                if (!now.equals(pickedValue)) {
                    pickedFromDropdown = false;
                    btnBlock.setEnabled(false);
                }
            }

            @Override public void afterTextChanged(Editable s) { }
        });

        btnBlock.setEnabled(false);
        btnBlock.setOnClickListener(v -> {
            if (!pickedFromDropdown) return; // sigurnost
            String email = actEmail.getText() == null ? "" : actEmail.getText().toString().trim();
            showConfirmDialog(email);
        });
    }

    private void seedMockEmails() {
        mockEmails.add("user@gmail.com");
        mockEmails.add("user1@gmail.com");
        mockEmails.add("user2@gmail.com");
        mockEmails.add("user3@gmail.com");
        mockEmails.add("admin@gmail.com");
        mockEmails.add("driver@gmail.com");
        mockEmails.add("passenger@gmail.com");
        mockEmails.add("blocked.user@gmail.com");
        mockEmails.add("somebody@outlook.com");
        mockEmails.add("test.user@yahoo.com");
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
            dialog.dismiss();
        });
    }
}
