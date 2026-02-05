package com.komsiluk.taxi.ui.report;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

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
import com.komsiluk.taxi.data.remote.report.DailyValueResponse;
import com.komsiluk.taxi.data.session.SessionManager;
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

    @Inject
    SessionManager sessionManager;

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

    private final Handler emailHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable emailRunnable;
    private UsageReportsViewModel vm;

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

        vm = new ViewModelProvider(this).get(UsageReportsViewModel.class);

        ArrayAdapter<String> emailAdapter = new ArrayAdapter<>(this, R.layout.item_email_dropdown, new ArrayList<>());
        actUser.setAdapter(emailAdapter);
        actUser.setThreshold(3);

        vm.getState().observe(this, st -> {
            if (st == null) return;

            if (st.loading) {
                return;
            }

            if (st.error != null) {
                showError(st.error);
                return;
            }

            if (st.emailSuggestions != null) {
                emailAdapter.clear();
                emailAdapter.addAll(st.emailSuggestions);
                emailAdapter.notifyDataSetChanged();
                if (!suppressEmailAutocomplete && selectedTargetIndex == 2 && actUser.hasFocus() && !userSelectedFromDropdown && actUser.getText() != null && actUser.getText().length() >= 3 && !emailAdapter.isEmpty()) {
                    actUser.showDropDown();
                } else {
                    actUser.dismissDropDown();
                }
            }

            if (st.report != null) {
                renderReport(st.report);
            }
        });

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

            suppressAutocompleteFor(600);

            selectedUserEmail = chosen;
            userSelectedFromDropdown = true;

            if (emailRunnable != null) emailHandler.removeCallbacks(emailRunnable);

            actUser.setText(chosen, false);
            actUser.setSelection(chosen.length());

            actUser.dismissDropDown();
        });


        actUser.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (suppressEmailAutocomplete) return;

                String now = s == null ? "" : s.toString();

                if (!now.equals(selectedUserEmail)) userSelectedFromDropdown = false;

                if (selectedTargetIndex != 2) return;

                if (emailRunnable != null) emailHandler.removeCallbacks(emailRunnable);

                String q = now.trim();
                emailRunnable = () -> {
                    vm.autocompleteEmails(q, 10);
                };
                emailHandler.postDelayed(emailRunnable, 250);
            }
        });

        rowUser.setVisibility(View.GONE);
    }

    private boolean suppressEmailAutocomplete = false;

    private void suppressAutocompleteFor(int ms) {
        suppressEmailAutocomplete = true;
        emailHandler.removeCallbacksAndMessages(null);
        emailHandler.postDelayed(() -> suppressEmailAutocomplete = false, ms);
    }

    private void renderReport(com.komsiluk.taxi.data.remote.report.RideReportResponse rep) {
        clearCharts();

        java.util.List<com.komsiluk.taxi.data.remote.report.DailyValueResponse> money = rep.getMoneyPerDay();
        java.util.List<com.komsiluk.taxi.data.remote.report.DailyValueResponse> rides = rep.getRidesPerDay();
        java.util.List<com.komsiluk.taxi.data.remote.report.DailyValueResponse> km = rep.getDistancePerDay();

        setChartDataFromDaily(moneyBlock, money, "Money");
        setChartDataFromDaily(ridesBlock, rides, "Rides");
        setChartDataFromDaily(kmBlock, km, "Km");

        float moneySum = rep.getTotalMoney() == null ? 0f : rep.getTotalMoney().floatValue();
        float moneyAvg = rep.getAverageMoneyPerDay() == null ? 0f : rep.getAverageMoneyPerDay().floatValue();
        moneyBlock.sum.setText(getString(R.string.sum_fmt, moneySum));
        moneyBlock.avg.setText(getString(R.string.avg_fmt, moneyAvg));

        ridesBlock.sum.setText(getString(R.string.sum_fmt, (float) rep.getTotalRides()));
        ridesBlock.avg.setText(getString(R.string.avg_fmt, (float) rep.getAverageRidesPerDay()));

        kmBlock.sum.setText(getString(R.string.sum_fmt, (float) rep.getTotalDistanceKm()));
        kmBlock.avg.setText(getString(R.string.avg_fmt, (float) rep.getAverageDistanceKmPerDay()));
    }

    private void setChartDataFromDaily(ChartBlock block, List<DailyValueResponse> daily, String label) {

        if (daily == null) daily = new ArrayList<>();

        ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dayLabels = new ArrayList<>();

        DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;

        for (int i = 0; i < daily.size(); i++) {
            DailyValueResponse d = daily.get(i);
            float v = (float) (d == null ? 0.0 : d.getValue());
            entries.add(new Entry(i, v));

            String dateStr = d == null ? "" : d.getDate();
            try {
                LocalDate ld = LocalDate.parse(dateStr, iso);
                dayLabels.add(String.valueOf(ld.getDayOfMonth()));
            } catch (Exception e) {
                dayLabels.add("");
            }
        }

        LineDataSet ds = new LineDataSet(entries, label);

        ds.setColor(getResources().getColor(R.color.secondary, null));
        ds.setCircleColor(getResources().getColor(R.color.secondary, null));
        ds.setCircleRadius(3f);
        ds.setLineWidth(2f);
        ds.setValueTextSize(0f);

        block.chart.setData(new LineData(ds));

        block.chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                if (idx < 0 || idx >= dayLabels.size()) return "";
                return dayLabels.get(idx);
            }
        });

        block.chart.invalidate();
    }

    private void onInsert() {
        suppressAutocompleteFor(800);
        if (emailRunnable != null) emailHandler.removeCallbacks(emailRunnable);

        if (actUser != null) {
            actUser.dismissDropDown();
            actUser.clearFocus();
        }

        View root = getCurrentFocus();
        if (root != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
        }

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

        String startIso = from.toString();
        String endIso = to.toString();

        if (!isAdmin) {
            Long id = sessionManager.getUserId();
            vm.loadReportForUserId(id, startIso, endIso);
            return;
        }

        if (selectedTargetIndex == 0) {
            vm.loadReportAllPassengers(startIso, endIso);
        } else if (selectedTargetIndex == 1) {
            vm.loadReportAllDrivers(startIso, endIso);
        } else {
            vm.loadReportByEmail(selectedUserEmail, startIso, endIso);
        }

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
