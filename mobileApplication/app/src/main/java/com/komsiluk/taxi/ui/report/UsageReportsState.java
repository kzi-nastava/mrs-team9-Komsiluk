package com.komsiluk.taxi.ui.report;

import com.komsiluk.taxi.data.remote.report.RideReportResponse;
import java.util.List;

public class UsageReportsState {
    public boolean loading;
    public String error;

    public RideReportResponse report;
    public List<String> emailSuggestions;
}
