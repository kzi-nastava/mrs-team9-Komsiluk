package com.komsiluk.taxi.ui.ride;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.komsiluk.taxi.R;

public class ScheduledDialogs {

    public interface VoidAction { void run(); }

    public static void showScheduledDetails(
            Context ctx,
            ScheduledRide ride,
            VoidAction onCancelRide
    ) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_scheduled_details, null, false);

        TextView tvTitle = v.findViewById(R.id.tvSchDetailsTitle);

        TextView tvPickup = v.findViewById(R.id.tvSchDetailsPickupValue);
        TextView tvDest = v.findViewById(R.id.tvSchDetailsDestinationValue);

        TextView tvStationsCount = v.findViewById(R.id.tvSchDetailsStationsValue);
        LinearLayout stationsContainer = v.findViewById(R.id.containerSchStations);

        TextView tvUsersCount = v.findViewById(R.id.tvSchDetailsUsersValue);
        LinearLayout usersContainer = v.findViewById(R.id.containerSchUsers);

        TextView tvCarType = v.findViewById(R.id.tvSchDetailsCarTypeValue);
        TextView tvPet = v.findViewById(R.id.tvSchDetailsPetValue);
        TextView tvChild = v.findViewById(R.id.tvSchDetailsChildValue);

        TextView tvKm = v.findViewById(R.id.tvSchKm);
        TextView tvTime = v.findViewById(R.id.tvSchTime);
        TextView tvPrice = v.findViewById(R.id.tvSchPrice);

        TextView tvScheduledTime = v.findViewById(R.id.tvSchScheduledTimeValue);

        MaterialButton btnClose = v.findViewById(R.id.btnSchDetailsClose);
        MaterialButton btnCancel = v.findViewById(R.id.btnSchDetailsCancelRide);

        tvTitle.setText(ctx.getString(R.string.scheduled_details_title)); // "Scheduled ride"
        tvPickup.setText(ride.getPickup());
        tvDest.setText(ride.getDestination());

        // stations
        int stCount = ride.getStations() == null ? 0 : ride.getStations().size();
        tvStationsCount.setText(String.valueOf(stCount));
        stationsContainer.removeAllViews();
        if (stCount > 0) {
            for (String s : ride.getStations()) {
                stationsContainer.addView(buildSecondaryBullet(ctx, s));
            }
        }

        // users
        int uCount = ride.getUsers() == null ? 0 : ride.getUsers().size();
        tvUsersCount.setText(String.valueOf(uCount));
        usersContainer.removeAllViews();
        if (uCount > 0) {
            for (String email : ride.getUsers()) {
                usersContainer.addView(buildSecondaryBullet(ctx, email));
            }
        }

        tvCarType.setText(ride.getCarType());
        tvPet.setText(ride.isPetFriendly() ? ctx.getString(R.string.yes) : ctx.getString(R.string.no));
        tvChild.setText(ride.isChildSeat() ? ctx.getString(R.string.yes) : ctx.getString(R.string.no));

        // GUI-only placeholders (kao i favorites)
        tvKm.setText(ctx.getString(R.string.placeholder_kilometers));
        tvTime.setText(ctx.getString(R.string.placeholder_time));
        tvPrice.setText(ctx.getString(R.string.placeholder_price));

        tvScheduledTime.setText(ride.getScheduledTime());

        AlertDialog dialog = new MaterialAlertDialogBuilder(ctx)
                .setView(v)
                .create();

        btnClose.setOnClickListener(x -> dialog.dismiss());
        btnCancel.setOnClickListener(x -> {
            // GUI-only: ti kasnije ubaci logiku
            if (onCancelRide != null) onCancelRide.run();
            dialog.dismiss();
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            int width = (int) (ctx.getResources().getDisplayMetrics().widthPixels * 0.95f);
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    private static TextView buildSecondaryBullet(Context ctx, String value) {
        TextView tv = new TextView(ctx);
        tv.setText("• " + value);
        tv.setTextColor(ContextCompat.getColor(ctx, R.color.secondary));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setTypeface(ResourcesCompat.getFont(ctx, R.font.roboto_serif_medium));
        tv.setPadding(0, dp(ctx, 2), 0, 0);
        return tv;
    }

    private static int dp(Context ctx, int dp) {
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    }
}
