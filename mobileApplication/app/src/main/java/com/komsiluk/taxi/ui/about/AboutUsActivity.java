package com.komsiluk.taxi.ui.about;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AboutUsActivity extends BaseNavDrawerActivity {

    private static final String PHONE_NUMBER = "+381111111111";
    private static final String EMAIL = "email@gmail.com";

    @Inject
    AuthManager authManager;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected int getDrawerMenuResId() {
        if (authManager.getRole().equals(UserRole.DRIVER)) {
            return R.menu.menu_driver_drawer;
        } else if(authManager.getRole().equals(UserRole.ADMIN)){
            return R.menu.menu_admin_drawer;
        }
        return R.menu.menu_app_drawer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rowFacebook = findViewById(R.id.rowFacebook);
        View rowInstagram = findViewById(R.id.rowInstagram);
        View rowPhone = findViewById(R.id.rowPhone);
        View rowEmail = findViewById(R.id.rowEmail);

        rowFacebook.setOnClickListener(v -> openAppOrUrl("com.facebook.katana", "https://www.facebook.com/"));
        rowInstagram.setOnClickListener(v -> openAppOrUrl("com.instagram.android", "https://www.instagram.com/"));
        rowPhone.setOnClickListener(v -> openDialer(PHONE_NUMBER));
        rowEmail.setOnClickListener(v -> openEmailComposePreferGmail(EMAIL));
    }

    private void openAppOrUrl(String packageName, String fallbackUrl) {
        Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch != null) {
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)));
        }
    }

    private void openDialer(String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        startActivity(new Intent(Intent.ACTION_DIAL, uri));
    }

    private void openEmailComposePreferGmail(String email) {
        Uri uri = Uri.parse("mailto:" + email);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.putExtra(Intent.EXTRA_SUBJECT, "Fake Taxi - Contact");
        i.putExtra(Intent.EXTRA_TEXT, "");

        if (isPackageInstalled("com.google.android.gm")) {
            i.setPackage("com.google.android.gm");
        }

        try {
            startActivity(i);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/")));
        }
    }

    private boolean isPackageInstalled(String pkg) {
        try {
            getPackageManager().getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
