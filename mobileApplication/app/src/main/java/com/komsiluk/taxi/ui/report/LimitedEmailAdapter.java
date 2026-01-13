package com.komsiluk.taxi.ui.report;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class LimitedEmailAdapter extends ArrayAdapter<String> {

    private final List<String> all;
    private final List<String> filtered = new ArrayList<>();
    private final int limit;

    public LimitedEmailAdapter(@NonNull Context ctx, @NonNull List<String> all, int limit) {
        super(ctx, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        this.all = all;
        this.limit = limit;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filtered.clear();

                String q = constraint == null ? "" : constraint.toString().toLowerCase().trim();
                if (!q.isEmpty()) {
                    for (String s : all) {
                        if (s.toLowerCase().contains(q)) {
                            filtered.add(s);
                            if (filtered.size() >= limit) break;
                        }
                    }
                }

                FilterResults res = new FilterResults();
                res.values = filtered;
                res.count = filtered.size();
                return res;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                if (results != null && results.values instanceof List) {
                    //noinspection unchecked
                    addAll((List<String>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }
}
