package com.komsiluk.taxi.ui.report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.report.ReportService;
import com.komsiluk.taxi.data.remote.report.RideReportResponse;
import com.komsiluk.taxi.data.remote.profile.UserService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class UsageReportsViewModel extends ViewModel {

    private final ReportService reportApi;
    private final UserService userApi;

    private final MutableLiveData<UsageReportsState> state = new MutableLiveData<>(new UsageReportsState());

    private Call<RideReportResponse> reportCall;
    private Call<List<String>> emailCall;

    @Inject
    public UsageReportsViewModel(ReportService reportApi, UserService userApi) {
        this.reportApi = reportApi;
        this.userApi = userApi;
    }

    public LiveData<UsageReportsState> getState() {
        return state;
    }

    private void setLoading(boolean loading) {
        UsageReportsState s = state.getValue();
        if (s == null) s = new UsageReportsState();
        s.loading = loading;
        if (loading) s.error = null;
        state.setValue(s);
    }

    private void setError(String msg) {
        UsageReportsState s = state.getValue();
        if (s == null) s = new UsageReportsState();
        s.loading = false;
        s.error = msg;
        state.setValue(s);
    }

    public void loadReportForUserId(Long userId, String startIso, String endIso) {
        cancelReport();
        setLoading(true);

        reportCall = reportApi.getUserReport(userId, startIso, endIso);
        reportCall.enqueue(new Callback<RideReportResponse>() {
            @Override public void onResponse(Call<RideReportResponse> call, Response<RideReportResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    setError("Report failed (" + resp.code() + ")");
                    return;
                }
                UsageReportsState s = state.getValue();
                if (s == null) s = new UsageReportsState();
                s.loading = false;
                s.error = null;
                s.report = resp.body();
                state.setValue(s);
            }

            @Override public void onFailure(Call<RideReportResponse> call, Throwable t) {
                setError("Network error: " + (t.getMessage() == null ? "unknown" : t.getMessage()));
            }
        });
    }

    public void loadReportAllDrivers(String startIso, String endIso) {
        cancelReport();
        setLoading(true);

        reportCall = reportApi.getAllDrivers(startIso, endIso);
        reportCall.enqueue(commonReportCallback());
    }

    public void loadReportAllPassengers(String startIso, String endIso) {
        cancelReport();
        setLoading(true);

        reportCall = reportApi.getAllPassengers(startIso, endIso);
        reportCall.enqueue(commonReportCallback());
    }

    public void loadReportByEmail(String email, String startIso, String endIso) {
        cancelReport();
        setLoading(true);

        reportCall = reportApi.getUserByEmail(email, startIso, endIso);
        reportCall.enqueue(commonReportCallback());
    }

    private Callback<RideReportResponse> commonReportCallback() {
        return new Callback<RideReportResponse>() {
            @Override public void onResponse(Call<RideReportResponse> call, Response<RideReportResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    setError("Report failed (" + resp.code() + ")");
                    return;
                }
                UsageReportsState s = state.getValue();
                if (s == null) s = new UsageReportsState();
                s.loading = false;
                s.error = null;
                s.report = resp.body();
                state.setValue(s);
            }

            @Override public void onFailure(Call<RideReportResponse> call, Throwable t) {
                setError("Network error: " + (t.getMessage() == null ? "unknown" : t.getMessage()));
            }
        };
    }

    public void autocompleteEmails(String query, int limit) {
        cancelEmails();

        if (query == null) query = "";
        query = query.trim();
        if (query.length() < 3) {
            UsageReportsState s = state.getValue();
            if (s == null) s = new UsageReportsState();
            s.emailSuggestions = Collections.emptyList();
            state.setValue(s);
            return;
        }

        emailCall = userApi.autocompleteEmails(query, limit);
        emailCall.enqueue(new Callback<List<String>>() {
            @Override public void onResponse(Call<List<String>> call, Response<List<String>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    UsageReportsState s = state.getValue();
                    if (s == null) s = new UsageReportsState();
                    s.emailSuggestions = Collections.emptyList();
                    state.setValue(s);
                    return;
                }
                UsageReportsState s = state.getValue();
                if (s == null) s = new UsageReportsState();
                s.emailSuggestions = resp.body();
                state.setValue(s);
            }

            @Override public void onFailure(Call<List<String>> call, Throwable t) {
                UsageReportsState s = state.getValue();
                if (s == null) s = new UsageReportsState();
                s.emailSuggestions = Collections.emptyList();
                state.setValue(s);
            }
        });
    }

    private void cancelReport() {
        if (reportCall != null) {
            reportCall.cancel();
            reportCall = null;
        }
    }

    private void cancelEmails() {
        if (emailCall != null) {
            emailCall.cancel();
            emailCall = null;
        }
    }
}
