package com.komsiluk.taxi.ui.edit;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.komsiluk.taxi.R;

public class AdminDialogs {

    public interface VoidAction { void run(); }

    public static void showDriverChangeRequestDetails(
            Context ctx,
            DriverChangeRequest req,
            VoidAction onReject,
            VoidAction onApprove
    ) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_driver_change_request_details, null, false);

        TextView tvTitle = v.findViewById(R.id.tvDcrTitle);
        TextView tvEmail = v.findViewById(R.id.tvDcrEmail);
        TextView tvDate  = v.findViewById(R.id.tvDcrDate);

        LinearLayout container = v.findViewById(R.id.containerDcrRows);

        MaterialButton btnReject = v.findViewById(R.id.btnDcrReject);
        MaterialButton btnApprove = v.findViewById(R.id.btnDcrApprove);
        ImageButton btnClose = v.findViewById(R.id.btnDcrClose);

        tvTitle.setText(ctx.getString(R.string.admin_dcr_dialog_title));
        tvEmail.setText(ctx.getString(R.string.admin_dcr_dialog_driver_fmt, req.getDriverEmail()));
        tvDate.setText(ctx.getString(R.string.admin_dcr_dialog_requested_at_fmt, req.getCreatedAt()));

        container.removeAllViews();

        // Header row
        container.addView(buildHeaderRow(ctx));

        if (req.getChanges() != null) {
            for (DriverChangeRequest.FieldChange row : req.getChanges()) {
                container.addView(buildDataRow(ctx, row.getField(), row.getCurrentValue(), row.getRequestedValue()));
            }
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(ctx, R.style.DialogNoInset)
                .setView(v)
                .setCancelable(true)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            int width = (int) (ctx.getResources().getDisplayMetrics().widthPixels * 0.95);
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

            dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        }

        btnClose.setOnClickListener(x -> dialog.dismiss());

        btnReject.setOnClickListener(x -> {
            dialog.dismiss();
            if (onReject != null) onReject.run();
        });

        btnApprove.setOnClickListener(x -> {
            dialog.dismiss();
            if (onApprove != null) onApprove.run();
        });
    }

    private static View buildHeaderRow(Context ctx) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundResource(R.drawable.bg_table_header);
        row.setPadding(dp(ctx, 10), dp(ctx, 10), dp(ctx, 10), dp(ctx, 10));

        TextView a = headerCell(ctx, ctx.getString(R.string.admin_dcr_field));
        TextView b = headerCell(ctx, ctx.getString(R.string.admin_dcr_current));
        TextView c = headerCell(ctx, ctx.getString(R.string.admin_dcr_requested));

        a.setLayoutParams(weightLp(0.28f));
        b.setLayoutParams(weightLp(0.36f));
        c.setLayoutParams(weightLp(0.36f));

        row.addView(a);
        row.addView(b);
        row.addView(c);
        return row;
    }

    private static View buildDataRow(Context ctx, String field, String cur, String req) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundResource(R.drawable.bg_table_row);
        row.setPadding(dp(ctx, 10), dp(ctx, 10), dp(ctx, 10), dp(ctx, 10));

        TextView a = dataCell(ctx, field, true);
        TextView b = dataCell(ctx, cur, false);
        TextView c = dataCell(ctx, req, false);

        a.setLayoutParams(weightLp(0.28f));
        b.setLayoutParams(weightLp(0.36f));
        c.setLayoutParams(weightLp(0.36f));

        row.addView(a);
        row.addView(b);
        row.addView(c);
        return row;
    }

    private static LinearLayout.LayoutParams weightLp(float w) {
        return new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, w);
    }

    private static TextView headerCell(Context ctx, String text) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(ContextCompat.getColor(ctx, R.color.secondary));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setTypeface(ResourcesCompat.getFont(ctx, R.font.roboto_serif_semibold));
        return tv;
    }

    private static TextView dataCell(Context ctx, String text, boolean isField) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setTypeface(ResourcesCompat.getFont(ctx, isField ? R.font.roboto_serif_semibold : R.font.roboto_serif_medium));
        tv.setTextColor(ContextCompat.getColor(ctx, isField ? R.color.white : R.color.secondary));
        return tv;
    }

    private static int dp(Context ctx, int dp) {
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    }
}
