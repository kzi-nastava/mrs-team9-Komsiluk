package com.komsiluk.taxi.ui.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.komsiluk.taxi.R;

import java.util.ArrayList;

public class DriverChangeRequestsAdapter extends RecyclerView.Adapter<DriverChangeRequestsAdapter.VH> {

    public interface Listener {
        void onRequestClicked(DriverChangeRequest req);
    }

    private final ArrayList<DriverChangeRequest> items;
    private final Listener listener;

    public DriverChangeRequestsAdapter(ArrayList<DriverChangeRequest> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_driver_change_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DriverChangeRequest r = items.get(position);

        h.tvEmail.setText(r.getDriverEmail());
        h.tvMeta.setText(h.itemView.getContext().getString(
                R.string.admin_req_meta_fmt, r.getId(), r.getCreatedAt()
        ));

        h.chips.removeAllViews();
        if (r.getTags() != null) {
            for (String tag : r.getTags()) {
                Chip chip = new Chip(h.itemView.getContext());
                chip.setText(tag);
                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setChipBackgroundColorResource(R.color.black_bg);
                chip.setTextColor(h.itemView.getContext().getColor(R.color.white));
                chip.setChipStrokeWidth(1f);
                chip.setChipStrokeColorResource(R.color.secondary_bg);
                h.chips.addView(chip);
            }
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRequestClicked(r);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvEmail, tvMeta, tvHint, tvBadge;
        ChipGroup chips;

        VH(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvReqEmail);
            tvMeta  = itemView.findViewById(R.id.tvReqMeta);
            chips   = itemView.findViewById(R.id.cgReqTags);
            tvHint  = itemView.findViewById(R.id.tvReqHint);
            tvBadge = itemView.findViewById(R.id.tvReqBadge);
        }
    }
}
