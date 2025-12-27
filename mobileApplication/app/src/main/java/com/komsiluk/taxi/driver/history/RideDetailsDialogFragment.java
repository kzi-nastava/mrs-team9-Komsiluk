package com.komsiluk.taxi.driver.history;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.databinding.DialogRideDetailsBinding;

public class RideDetailsDialogFragment extends DialogFragment {

    private DialogRideDetailsBinding b;

    public static RideDetailsDialogFragment newInstance(DriverRide ride) {
        RideDetailsDialogFragment f = new RideDetailsDialogFragment();
        Bundle args = new Bundle();

        args.putString("date", ride.date);
        args.putString("start", ride.startTime);
        args.putString("end", ride.endTime);
        args.putString("pickup", ride.pickup);
        args.putString("dest", ride.destination);
        args.putString("status", ride.status);
        args.putInt("passengers", ride.passengers);
        args.putInt("km", ride.kilometers);
        args.putString("duration", ride.duration);
        args.putString("price", ride.price);

        f.setArguments(args);
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int w = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.92f);
            int h = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.85f);

            getDialog().getWindow().setLayout(w, h);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = DialogRideDetailsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) return;

        int labelWhite = android.R.color.white;
        int valueYellow = R.color.secondary;

        // ROUTE (koristi podatke iz DriverRide)
        b.tvPickup.setText(labelValue(
                "Pickup location: ",
                args.getString("pickup", ""),
                labelWhite, valueYellow
        ));

        // Za sad “dummy” (dok ne uvedeš stanice u model)
        b.tvStation1.setText(labelValue("Station 1: ", "Medical Faculty", labelWhite, valueYellow));
        b.tvStation2.setText(labelValue("Station 2: ", "Suboticka 12", labelWhite, valueYellow));

        b.tvDestination.setText(labelValue(
                "Destination: ",
                args.getString("dest", ""),
                labelWhite, valueYellow
        ));

        // START/END
        b.tvStart.setText(labelValue(
                "Start time: ",
                args.getString("start", ""),
                labelWhite, valueYellow
        ));
        b.tvEnd.setText(labelValue(
                "End time: ",
                args.getString("end", ""),
                labelWhite, valueYellow
        ));

        // STATS
        b.tvKm.setText(labelValue(
                "Kilometers: ",
                String.valueOf(args.getInt("km", 0)),
                labelWhite, valueYellow
        ));
        b.tvDuration.setText(labelValue(
                "Time: ",
                args.getString("duration", ""),
                labelWhite, valueYellow
        ));
        b.tvPrice.setText(labelValue(
                "Price: ",
                args.getString("price", ""),
                labelWhite, valueYellow
        ));

        // PASSENGERS placeholder
        b.tvPassengers.setText("user1@gmail.com\nuser2@gmail.com\nuser3@gmail.com");

        // RATINGS placeholder (ali obojeno: do ':' belo, posle ':' žuto)
        String ratingsText =
                "user1@gmail.com\n" +
                        "Driver rating: 5 stars\n" +
                        "Vehicle rating: 5 stars\n" +
                        "Comment: Very pleasant experience!\n\n" +
                        "user2@gmail.com\n" +
                        "Driver rating: 4 stars\n" +
                        "Vehicle rating: 5 stars\n" +
                        "Comment: N/A\n\n" +
                        "user3@gmail.com\n" +
                        "Driver rating: N/A\n" +
                        "Vehicle rating: N/A\n" +
                        "Comment: N/A";

        b.tvRatings.setText(colorizeLabelsUntilColon(ratingsText, labelWhite, valueYellow));

        b.tvPanicFlag.setText(labelValue("Panic button pressed: ", "False", labelWhite, valueYellow));
        b.tvInconsistencyFlag.setText(labelValue("Inconsistency report: ", "N/A", labelWhite, valueYellow));

        b.btnClose.setOnClickListener(v -> dismiss());
    }
    
    private CharSequence labelValue(String label, String value, int labelColorRes, int valueColorRes) {
        String full = label + value;
        SpannableString ss = new SpannableString(full);

        int labelColor = ContextCompat.getColor(requireContext(), labelColorRes);
        int valueColor = ContextCompat.getColor(requireContext(), valueColorRes);

        ss.setSpan(new ForegroundColorSpan(labelColor), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(valueColor), label.length(), full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    // Za format gde želiš "Label: value" u više linija:
    // sve do ':' je label boja, posle ':' je value boja (liniju po liniju).
    private CharSequence colorizeLabelsUntilColon(String text, int labelColorRes, int valueColorRes) {
        SpannableString ss = new SpannableString(text);

        int labelColor = ContextCompat.getColor(requireContext(), labelColorRes);
        int valueColor = ContextCompat.getColor(requireContext(), valueColorRes);

        int start = 0;
        while (start < text.length()) {
            int endLine = text.indexOf('\n', start);
            if (endLine == -1) endLine = text.length();

            int colon = text.indexOf(':', start);

            if (colon != -1 && colon < endLine) {
                // label: start..colon+1
                ss.setSpan(new ForegroundColorSpan(labelColor),
                        start, colon + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // value: colon+1..endLine
                ss.setSpan(new ForegroundColorSpan(valueColor),
                        colon + 1, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                // linije bez ':' (npr. email) -> sve belo
                ss.setSpan(new ForegroundColorSpan(labelColor),
                        start, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            start = endLine + 1;
        }

        return ss;
    }

    private CharSequence twoLineLabelValue(
            String label1, String value1,
            String label2, String value2,
            int labelColorRes, int valueColorRes
    ) {
        String line1 = label1 + value1;
        String line2 = label2 + value2;
        String full = line1 + "\n" + line2;

        SpannableString ss = new SpannableString(full);

        int labelColor = ContextCompat.getColor(requireContext(), labelColorRes);
        int valueColor = ContextCompat.getColor(requireContext(), valueColorRes);

        ss.setSpan(new ForegroundColorSpan(labelColor), 0, label1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(valueColor), label1.length(), line1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int offset = line1.length() + 1;
        ss.setSpan(new ForegroundColorSpan(labelColor), offset, offset + label2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(valueColor), offset + label2.length(), offset + line2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}

