package com.komsiluk.taxi.ui.profile.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.data.remote.block.BlockNoteResponse;
import com.komsiluk.taxi.data.remote.block.BlockService;
import com.komsiluk.taxi.data.remote.profile.UserBlockedResponse;
import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.admin.ride_history.AdminRideHistoryActivity;
import com.komsiluk.taxi.ui.driver_history.DriverHistoryActivity;
import com.komsiluk.taxi.ui.edit.AdminDriverChangeRequestsActivity;
import com.komsiluk.taxi.ui.passenger.ride_history.PassengerRideHistoryActivity;
import com.komsiluk.taxi.ui.profile.ProfileViewModel;
import com.komsiluk.taxi.ui.report.UsageReportsActivity;
import com.komsiluk.taxi.ui.ride.FavoritesActivity;
import com.komsiluk.taxi.ui.ride.ScheduledActivity;
import com.komsiluk.taxi.ui.ride.ScheduledRidesActivity;
import com.komsiluk.taxi.ui.shared.InfoMessageActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ProfileSidebarBottomFragment extends Fragment {

    private static final String ARG_IS_DRIVER = "is_driver";
    @Inject
    AuthManager authManager;
    @Inject
    SessionManager sessionManager;
    @Inject
    UserService userService;
    @Inject
    BlockService blockService;

    public static ProfileSidebarBottomFragment newInstance(boolean isDriver) {
        ProfileSidebarBottomFragment f = new ProfileSidebarBottomFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_DRIVER, isDriver);
        f.setArguments(args);
        return f;
    }

    private boolean isDriver;
    private ProfileViewModel viewModel;

    private TextView tvActiveToday;

    private boolean isBlocked = false;
    private String lastBlockReason = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isDriver = getArguments().getBoolean(ARG_IS_DRIVER, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_sidebar_bottom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvActiveToday = view.findViewById(R.id.tvActiveToday);
        View btnLogout = view.findViewById(R.id.btnLogout);

        if (!isDriver) {
            tvActiveToday.setVisibility(View.INVISIBLE);
        }

        view.findViewById(R.id.btnRideHistory)
            .setOnClickListener(v -> {
                if(authManager.getRole().equals(UserRole.PASSENGER))
                {
                    Intent i = new Intent(getContext(), PassengerRideHistoryActivity.class);
                    startActivity(i);
                }
                else if(authManager.getRole().equals(UserRole.DRIVER))
                {
                    Intent i = new Intent(getContext(), DriverHistoryActivity.class);
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(getContext(), AdminRideHistoryActivity.class);
                    startActivity(i);
                }
            });

        View btnTopLeft = view.findViewById(R.id.btnTopLeft);
        TextView tvTopLeft = btnTopLeft.findViewById(R.id.tvTopLeft);
        ImageView ivTopLeft = view.findViewById(R.id.iconTopLeft);

        UserRole role = authManager.getRole();

        if (role == UserRole.PASSENGER) {
            tvTopLeft.setText(getString(R.string.profile_menu_favorite_rides));
            ivTopLeft.setImageResource(R.drawable.favorite);
        } else if (role == UserRole.DRIVER) {
            tvTopLeft.setText(getString(R.string.nav_scheduled_rides));
            ivTopLeft.setImageResource(R.drawable.clock);
        } else {
            tvTopLeft.setText(getString(R.string.menu_edit_requests));
            ivTopLeft.setImageResource(R.drawable.edit);
        }

        btnTopLeft.setOnClickListener(v -> {
                if (role == UserRole.PASSENGER) {
                    Intent i = new Intent(getContext(), FavoritesActivity.class);
                    startActivity(i);
                } else if (role == UserRole.DRIVER) {
                    Intent i = new Intent(getContext(), ScheduledActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getContext(), AdminDriverChangeRequestsActivity.class);
                    startActivity(i);
                }
            });

        view.findViewById(R.id.btnUsageReport)
            .setOnClickListener(v -> {
                Intent i = new Intent(getContext(), UsageReportsActivity.class);
                startActivity(i);
            });

        btnLogout.setOnClickListener(v -> {
            Intent i = InfoMessageActivity.createLogoutIntent(
                    this.getContext(),
                    getString(R.string.logout_title),
                    getString(R.string.logout_message),
                    getString(R.string.logout_button)
            );
            startActivity(i);
        });

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        viewModel.getProfile().observe(getViewLifecycleOwner(), dto -> {
            if (dto == null) return;
            if (!isDriver) return;

            if(isBlocked)
            {
                applyBlockedUi();
                return;
            }

            Long mins = dto.getActiveMinutesLast24h();
            if (mins == null) mins = 0L;

            String pretty = formatMinutes(mins);
            tvActiveToday.setText(getString(R.string.profile_active_minutes_fmt, pretty));

            tvActiveToday.setOnClickListener(null);
            tvActiveToday.setPaintFlags(tvActiveToday.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isDriver) return;
        checkBlockedAndUpdateUi();
    }

    private void checkBlockedAndUpdateUi() {
        Long userId = sessionManager != null ? sessionManager.getUserId() : null;
        if (userId == null) return;

        userService.isUserBlocked(userId).enqueue(new Callback<UserBlockedResponse>() {
            @Override
            public void onResponse(Call<UserBlockedResponse> call, Response<UserBlockedResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    isBlocked = false;
                    lastBlockReason = null;
                    return;
                }

                isBlocked = resp.body().isBlocked();

                if (isBlocked) {
                    applyBlockedUi();
                    lastBlockReason = null;
                } else {
                    lastBlockReason = null;
                    if (tvActiveToday != null) {
                        tvActiveToday.setOnClickListener(null);
                        tvActiveToday.setPaintFlags(tvActiveToday.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserBlockedResponse> call, Throwable t) {
                isBlocked = false;
                lastBlockReason = null;
            }
        });
    }

    private void applyBlockedUi() {
        if (tvActiveToday == null) return;

        tvActiveToday.setVisibility(View.VISIBLE);
        tvActiveToday.setText("Blocked");

        tvActiveToday.setPaintFlags(tvActiveToday.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvActiveToday.setOnClickListener(v -> openBlockedDialog());
    }

    private void openBlockedDialog() {
        Long userId = sessionManager != null ? sessionManager.getUserId() : null;
        if (userId == null) return;

        if (lastBlockReason != null) {
            showBlockedDialog(lastBlockReason);
            return;
        }

        blockService.getForUser(userId).enqueue(new Callback<BlockNoteResponse>() {
            @Override
            public void onResponse(Call<BlockNoteResponse> call, Response<BlockNoteResponse> resp) {
                String reason = "(Reason unavailable)";

                if (resp.isSuccessful() && resp.body() != null) {
                    String r = resp.body().getReason();
                    if (r != null && !r.trim().isEmpty()) reason = r.trim();
                }

                lastBlockReason = reason;
                showBlockedDialog(reason);
            }

            @Override
            public void onFailure(Call<BlockNoteResponse> call, Throwable t) {
                showBlockedDialog("(Reason unavailable)");
            }
        });
    }

    private void showBlockedDialog(String reason) {
        if (getContext() == null) return;

        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_blocked, null, false);

        ImageButton btnClose = v.findViewById(R.id.btnBlockedClose);
        TextView tvReason = v.findViewById(R.id.tvBlockedReason);
        MaterialButton btnOk = v.findViewById(R.id.btnBlockedOk);

        tvReason.setText(reason);

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setView(v)
                .setCancelable(true)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        btnClose.setOnClickListener(x -> dialog.dismiss());
        btnOk.setOnClickListener(x -> dialog.dismiss());
    }

    private String formatMinutes(long totalMin) {
        if (totalMin <= 0) return "0m";

        long h = totalMin / 60;
        long m = totalMin % 60;

        if (h <= 0) return m + "m";
        if (m == 0) return h + "h";
        return h + "h " + m + "m";
    }
}