package com.komsiluk.taxi.ui.report;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UsageReportsActivity extends BaseNavDrawerActivity {

    public static final String EXTRA_IS_ADMIN = "extra_is_admin";

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    @Inject
    AuthManager authManager;

    @Override
    protected int getDrawerMenuResId() {
        if (authManager.getRole().equals(UserRole.DRIVER)) {
            return R.menu.menu_driver_drawer;
        } else if (authManager.getRole().equals(UserRole.ADMIN)) {
            return R.menu.menu_admin_drawer;
        }
        return R.menu.menu_app_drawer;
    }

    private TextInputEditText etFrom, etTo;
    private MaterialButton btnInsert;
    private TextView tvValidation;

    private View rowTarget;
    private View rowUser;

    private AutoCompleteTextView actTarget;
    private AutoCompleteTextView actUser;

    private boolean isAdmin;

    private boolean userSelectedFromDropdown = false;
    private String selectedUserEmail = "";
    private int selectedTargetIndex = 0;

    private ChartBlock moneyBlock, ridesBlock, kmBlock;

    private final String[] targets = new String[]{"All passengers", "All drivers", "Single user"};

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_usage_reports;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isAdmin = getIntent().getBooleanExtra(EXTRA_IS_ADMIN, false);

        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        btnInsert = findViewById(R.id.btnInsert);
        tvValidation = findViewById(R.id.tvValidation);

        rowTarget = findViewById(R.id.rowTarget);
        rowUser = findViewById(R.id.rowUser);

        actTarget = findViewById(R.id.actTarget);
        actUser = findViewById(R.id.actUserEmail);

        moneyBlock = ChartBlock.bind(findViewById(R.id.chartMoneyInclude));
        ridesBlock = ChartBlock.bind(findViewById(R.id.chartRidesInclude));
        kmBlock = ChartBlock.bind(findViewById(R.id.chartKmInclude));

        moneyBlock.title.setText(getString(R.string.chart_money_title));
        ridesBlock.title.setText(getString(R.string.chart_rides_title));
        kmBlock.title.setText(getString(R.string.chart_km_title));

        styleChart(moneyBlock.chart);
        styleChart(ridesBlock.chart);
        styleChart(kmBlock.chart);

        clearCharts();

        etFrom.setOnClickListener(v -> pickDateInto(etFrom));
        etTo.setOnClickListener(v -> pickDateInto(etTo));

        setupAdminUiIfNeeded();

        btnInsert.setOnClickListener(v -> onInsert());
    }

    private void setupAdminUiIfNeeded() {
        if (!isAdmin) {
            rowTarget.setVisibility(View.GONE);
            rowUser.setVisibility(View.GONE);
            return;
        }

        rowTarget.setVisibility(View.VISIBLE);

        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_email_dropdown,
                targets
        );
        actTarget.setAdapter(targetAdapter);
        actTarget.setText(targets[0], false);
        selectedTargetIndex = 0;

        actTarget.setOnClickListener(v -> actTarget.showDropDown());

        actTarget.setOnItemClickListener((parent, view, position, id) -> {
            selectedTargetIndex = position;

            boolean single = (position == 2);
            rowUser.setVisibility(single ? View.VISIBLE : View.GONE);

            selectedUserEmail = "";
            userSelectedFromDropdown = false;
            actUser.setText("", false);
        });

        List<String> mockEmails = new ArrayList<>();
        for (int i = 0; i < 30; i++) mockEmails.add("user" + i + "@gmail.com");
        mockEmails.add("driver@test.com");
        mockEmails.add("passenger@test.com");

        ArrayAdapter<String> emailAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_email_dropdown,
                mockEmails
        );
        actUser.setAdapter(emailAdapter);

        actUser.setThreshold(3);

        actUser.setOnItemClickListener((parent, view, position, id) -> {
            String chosen = (String) parent.getItemAtPosition(position);
            selectedUserEmail = chosen;
            userSelectedFromDropdown = true;
            actUser.setText(chosen, false);
            actUser.setSelection(chosen.length());
        });

        actUser.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String now = s == null ? "" : s.toString();
                if (!now.equals(selectedUserEmail)) {
                    userSelectedFromDropdown = false;
                }
            }
        });

        rowUser.setVisibility(View.GONE);
    }

    private void onInsert() {
        tvValidation.setVisibility(View.GONE);

        LocalDate from = parseDate(etFrom);
        LocalDate to = parseDate(etTo);

        if (from == null || to == null) {
            showError(getString(R.string.err_dates_required));
            return;
        }
        if (from.isAfter(to)) {
            showError(getString(R.string.err_date_order));
            return;
        }

        if (isAdmin) {
            boolean single = (selectedTargetIndex == 2);
            if (single && !userSelectedFromDropdown) {
                showError(getString(R.string.err_user_required));
                return;
            }
        }

        renderAllCharts(from, to);
    }

    private void renderAllCharts(LocalDate from, LocalDate to) {
        List<LocalDate> days = enumerateDays(from, to);

        int seed = 1337;
        if (isAdmin) {
            seed += (selectedTargetIndex * 1000);
            if (selectedTargetIndex == 2) seed += selectedUserEmail.hashCode();
        }

        List<Float> money = generateSeries(days.size(), seed + 1, -10f, 25f);
        List<Float> rides = generateSeries(days.size(), seed + 2, 0f, 6f);
        List<Float> km = generateSeries(days.size(), seed + 3, 0f, 30f);

        setChartData(moneyBlock, days, money, "Money");
        setChartData(ridesBlock, days, rides, "Rides");
        setChartData(kmBlock, days, km, "Km");

        updateSumAvg(moneyBlock, money);
        updateSumAvg(ridesBlock, rides);
        updateSumAvg(kmBlock, km);
    }

    private void clearCharts() {
        moneyBlock.chart.setData(null);
        ridesBlock.chart.setData(null);
        kmBlock.chart.setData(null);

        moneyBlock.sum.setText(getString(R.string.sum_fmt, 0f));
        moneyBlock.avg.setText(getString(R.string.avg_fmt, 0f));

        ridesBlock.sum.setText(getString(R.string.sum_fmt, 0f));
        ridesBlock.avg.setText(getString(R.string.avg_fmt, 0f));

        kmBlock.sum.setText(getString(R.string.sum_fmt, 0f));
        kmBlock.avg.setText(getString(R.string.avg_fmt, 0f));

        moneyBlock.chart.invalidate();
        ridesBlock.chart.invalidate();
        kmBlock.chart.invalidate();
    }

    private void setChartData(ChartBlock block, List<LocalDate> days, List<Float> values, String label) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            entries.add(new Entry(i, values.get(i)));
        }

        LineDataSet ds = new LineDataSet(entries, label);
        ds.setColor(getResources().getColor(R.color.secondary, null));
        ds.setCircleColor(getResources().getColor(R.color.secondary, null));
        ds.setCircleRadius(3f);
        ds.setLineWidth(2f);
        ds.setValueTextSize(0f);

        LineData data = new LineData(ds);
        block.chart.setData(data);

        block.chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                if (idx < 0 || idx >= days.size()) return "";
                return String.valueOf(days.get(idx).getDayOfMonth());
            }
        });

        block.chart.invalidate();
    }

    private void updateSumAvg(ChartBlock block, List<Float> vals) {
        float sum = 0f;
        for (Float v : vals) sum += (v == null ? 0f : v);
        float avg = vals.isEmpty() ? 0f : (sum / vals.size());

        block.sum.setText(getString(R.string.sum_fmt, sum));
        block.avg.setText(getString(R.string.avg_fmt, avg));
    }

    private void styleChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);

        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(true);
        x.setGranularity(1f);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(true);
    }

    private void pickDateInto(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(
                this,
                R.style.AppDatePickerDialogTheme,
                (view, year, month, dayOfMonth) -> {
                    LocalDate d = LocalDate.of(year, month + 1, dayOfMonth);
                    target.setText(df.format(d));
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dlg.show();
    }

    private LocalDate parseDate(TextInputEditText et) {
        String s = et.getText() == null ? "" : et.getText().toString().trim();
        if (s.isEmpty()) return null;
        try {
            return LocalDate.parse(s, df);
        } catch (Exception ex) {
            return null;
        }
    }

    private List<LocalDate> enumerateDays(LocalDate from, LocalDate to) {
        ArrayList<LocalDate> list = new ArrayList<>();
        LocalDate d = from;
        while (!d.isAfter(to)) {
            list.add(d);
            d = d.plusDays(1);
        }
        return list;
    }

    private List<Float> generateSeries(int n, int seed, float min, float max) {
        ArrayList<Float> out = new ArrayList<>();
        Random r = new Random(seed);
        float range = (max - min);

        for (int i = 0; i < n; i++) {
            float base = (float) Math.sin(i * 0.9f) * (range * 0.25f);
            float noise = (r.nextFloat() - 0.5f) * (range * 0.20f);
            float v = min + (range * 0.55f) + base + noise;

            if (v < min) v = min;
            if (v > max) v = max;

            out.add(v);
        }
        return out;
    }

    private void showError(String msg) {
        tvValidation.setText(msg);
        tvValidation.setVisibility(View.VISIBLE);
    }

    static class ChartBlock {
        TextView title;
        LineChart chart;
        TextView sum;
        TextView avg;

        static ChartBlock bind(View root) {
            ChartBlock b = new ChartBlock();
            b.title = root.findViewById(R.id.tvChartTitle);
            b.chart = root.findViewById(R.id.lineChart);
            b.sum = root.findViewById(R.id.tvSum);
            b.avg = root.findViewById(R.id.tvAvg);
            return b;
        }
    }
}
