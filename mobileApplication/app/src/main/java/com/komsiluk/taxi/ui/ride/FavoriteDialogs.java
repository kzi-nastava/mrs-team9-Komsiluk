package com.komsiluk.taxi.ui.ride;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.komsiluk.taxi.R;

public class FavoriteDialogs {

    public interface VoidAction { void run(); }
    public interface StringAction { void run(String s); }

    public static void showFavoriteDetails(
            Context ctx,
            FavoriteRide ride,
            Runnable onBook,
            Runnable onRename,
            Runnable onDelete
    ) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_favorite_details, null, false);

        TextView tvTitle = v.findViewById(R.id.tvFavDetailsTitle);
        TextView btnChangeName = v.findViewById(R.id.btnFavDetailsChangeName);

        TextView tvPickup = v.findViewById(R.id.tvDetailsPickupValue);
        TextView tvDest = v.findViewById(R.id.tvDetailsDestinationValue);

        TextView tvStationsCount = v.findViewById(R.id.tvDetailsStationsValue);
        LinearLayout stationsContainer = v.findViewById(R.id.containerStations);

        TextView tvUsersCount = v.findViewById(R.id.tvDetailsUsersValue);
        LinearLayout usersContainer = v.findViewById(R.id.containerUsers);

        TextView tvCarType = v.findViewById(R.id.tvDetailsCarTypeValue);
        TextView tvPet = v.findViewById(R.id.tvDetailsPetValue);
        TextView tvChild = v.findViewById(R.id.tvDetailsChildValue);

        TextView tvKm = v.findViewById(R.id.tvFavKm);
        TextView tvTime = v.findViewById(R.id.tvFavTime);
        TextView tvPrice = v.findViewById(R.id.tvFavPrice);

        MaterialButton btnCancel = v.findViewById(R.id.btnFavDetailsCancel);
        MaterialButton btnDel = v.findViewById(R.id.btnFavDetailsDelete);
        MaterialButton btnBook = v.findViewById(R.id.btnFavDetailsBook);

        tvTitle.setText(ride.getName());
        tvPickup.setText(ride.getPickup());
        tvDest.setText(ride.getDestination());

        // stations
        int stCount = ride.getStations() == null ? 0 : ride.getStations().size();
        tvStationsCount.setText(String.valueOf(stCount));
        stationsContainer.removeAllViews();

        if (stCount > 0) {
            for (String s : ride.getStations()) {
                TextView row = buildDetailsListRow(ctx, s);
                stationsContainer.addView(row);
            }
        }

        // users
        int uCount = ride.getUsers() == null ? 0 : ride.getUsers().size();
        tvUsersCount.setText(String.valueOf(uCount));
        usersContainer.removeAllViews();

        if (uCount > 0) {
            for (String email : ride.getUsers()) {
                TextView row = buildDetailsListRow(ctx, email);
                usersContainer.addView(row);
            }
        }

        tvCarType.setText(ride.getCarType()); // "Standard", "Luxury", "Van"
        tvPet.setText(ride.isPetFriendly() ? ctx.getString(R.string.yes) : ctx.getString(R.string.no));
        tvChild.setText(ride.isChildSeat() ? ctx.getString(R.string.yes) : ctx.getString(R.string.no));

        tvKm.setText(ctx.getString(R.string.placeholder_kilometers));
        tvTime.setText(ctx.getString(R.string.placeholder_time));
        tvPrice.setText(ctx.getString(R.string.placeholder_price));

        AlertDialog dialog = new MaterialAlertDialogBuilder(ctx)
                .setView(v)
                .create();

        // ---- Buttons ----
        btnCancel.setOnClickListener(x -> dialog.dismiss());

        btnChangeName.setOnClickListener(x -> onRename.run());
        btnDel.setOnClickListener(x -> onDelete.run());

        btnBook.setOnClickListener(x -> {
            dialog.dismiss();
            onBook.run();
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            int width = (int) (ctx.getResources().getDisplayMetrics().widthPixels*0.95);
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    private static TextView buildDetailsListRow(Context ctx, String value) {
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


    public static void showRenameDialog(Context ctx, String oldName, StringAction onConfirm) {
        View v = View.inflate(ctx, R.layout.dialog_favorite_rename, null);

        TextView tvTitle = v.findViewById(R.id.tvRenameTitle);
        EditText et = v.findViewById(R.id.etRename);

        MaterialButton btnCancel = v.findViewById(R.id.btnRenameCancel);
        MaterialButton btnOk = v.findViewById(R.id.btnRenameConfirm);

        tvTitle.setText(ctx.getString(R.string.fav_rename_title_fmt, oldName));

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(ctx)
                        .setView(v)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(x -> dialog.dismiss());
        btnOk.setOnClickListener(x -> {
            String name = et.getText() == null ? "" : et.getText().toString().trim();
            dialog.dismiss();
            onConfirm.run(name);
        });

        dialog.show();
    }

    public static void showDeleteDialog(Context ctx, String name, VoidAction onConfirmDelete) {
        View v = View.inflate(ctx, R.layout.dialog_favorite_delete, null);

        TextView tvTitle = v.findViewById(R.id.tvDeleteTitle);
        MaterialButton btnCancel = v.findViewById(R.id.btnDeleteCancel);
        MaterialButton btnDel = v.findViewById(R.id.btnDeleteConfirm);

        tvTitle.setText(ctx.getString(R.string.fav_delete_title_fmt, name));

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(ctx)
                        .setView(v)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(x -> dialog.dismiss());
        btnDel.setOnClickListener(x -> {
            dialog.dismiss();
            onConfirmDelete.run();
        });

        dialog.show();
    }
}
